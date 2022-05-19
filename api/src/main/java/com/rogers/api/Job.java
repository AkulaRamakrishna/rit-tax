package com.rogers.api;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.rogers.api.model.*;
import com.rogers.api.repository.*;
import com.rogers.api.service.EmailService;

import com.rogers.api.service.HttpService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpSession;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@lombok.Value
@Component
public class Job implements CommandLineRunner {

    EmailService emailService;

    HttpService httpService;

    DefaultSftpSessionFactory sftpSessionFactory;

    String rootFolder;

    TaxAuthorityRepository taxAuthorityRepository;

    TaxGroupRepository taxGroupRepository;

    TaxLocationRepository taxLocationRepository;

    TaxGroupRuleRepository taxGroupRuleRepository;

    TaxRateRuleRepository taxRateRuleRepository;

    RMSAddrRepository rmsAddrRepository;

    public Job(EmailService emailService, HttpService httpService, DefaultSftpSessionFactory sftpSessionFactory, @Value("${application.sftp.rootFolder}") final String rootFolder, TaxAuthorityRepository taxAuthorityRepository,
               TaxGroupRepository taxGroupRepository, TaxLocationRepository taxLocationRepository,
               TaxGroupRuleRepository taxGroupRuleRepository, TaxRateRuleRepository taxRateRuleRepository, RMSAddrRepository rmsAddrRepository) {
        this.emailService = emailService;
        this.httpService = httpService;
        this.sftpSessionFactory = sftpSessionFactory;
        this.rootFolder = rootFolder;
        this.taxAuthorityRepository = taxAuthorityRepository;
        this.taxGroupRepository = taxGroupRepository;
        this.taxLocationRepository = taxLocationRepository;
        this.taxGroupRuleRepository = taxGroupRuleRepository;
        this.taxRateRuleRepository = taxRateRuleRepository;
        this.rmsAddrRepository = rmsAddrRepository;
    }

    @Override
    public void run(String... args) {
        log.info("The job process has started");

        try {
            processDataToFiles();

        } catch (Exception e) {
            log.warn("An error has occurred while running the job", e);
            emailService.sendNotificationEmail("ECGENERIC", "An error has occurred while running the job", e.toString());
        }

        log.info("The job process has ended");
    }

    private void processDataToFiles() {

        try{
            // Create new file
            SftpSession  sftpSession = sftpSessionFactory.getSession();
            if(sftpSession != null){
                String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String timestampInMints = String.valueOf(System.currentTimeMillis() / (1000 * 60 * 60 ));
                String repFileName = "Rogers_tax_" + date + "_" +  timestampInMints + ".rep";
                String mntFileName = "Rogers_tax_group_" + date + "_" +  timestampInMints + ".mnt";
                sftpSession.mkdir(rootFolder + "/Outbound/" + dateFolder + "/");
                String repFilePath = rootFolder + "/Outbound/" + dateFolder + "/" + repFileName;
                String mntFilePath = rootFolder + "/Outbound/" + dateFolder + "/" + mntFileName;
                File repFile = new File(repFilePath);
                File mntFile = new File(mntFilePath);

                // If file doesn't exists, then create it
                if (!repFile.exists()) {
                    OutputStream outputStreamRepFile =  sftpSession.getClientInstance().put(repFilePath, ChannelSftp.OVERWRITE);
                    PrintWriter out = new PrintWriter(outputStreamRepFile);

                    out.println("<Header target_org_node=\"*:*\" download_time=\"IMMEDIATE\" apply_immediately=\"true\"/>");

                    // Write in .rep file
                    processTaxGroup(out);
                    processTaxAuthority(out);
                    processTaxLocation(out);
                    processTaxGroupRule(out);
                    processTaxRateRule(out);
                    processTaxRetailLocation(out);

                    out.close();

                    // process to zip file
                    //zipFiles(dateFolder, repFilePath, sftpSession);

                    // REST call Service POST, PUT and GET methods
                    InputStream inputFile = sftpSession.getClientInstance().get(repFilePath);
                    var bytes = inputFile.readAllBytes();
                    httpService.sendPutRequest(repFile, bytes);

                    int statusCode = httpService.getRequestStatusCode();
                    log.info(" status code ::" + statusCode);
                    if(statusCode == 200){
                        log.info("If status code is 200");
                        emailService.sendNotificationEmail("ECPROCESS", "Successfully this file to xcenter :: ", repFile.getName());
                    }

                }
                if (!mntFile.exists()) {
                    OutputStream outputStreamMntFile =  sftpSession.getClientInstance().put(mntFilePath, ChannelSftp.OVERWRITE);
                    PrintWriter out = new PrintWriter(outputStreamMntFile);

                    out.println("<Header target_org_node=\"*:*\" download_time=\"IMMEDIATE\" apply_immediately=\"true\"/>");

                    // Write in .mnt file
                    processMntFileData(out);

                    out.close();

                    // process to zip file
                    //zipFiles(dateFolder, mntFilePath, sftpSession);

                    // REST call Service POST, PUT and GET methods
                    InputStream inputFile = sftpSession.getClientInstance().get(mntFilePath);
                    var bytes = inputFile.readAllBytes();
                    httpService.sendPutRequest(mntFile, bytes);

                    int statusCode = httpService.getRequestStatusCode();
                    log.info(" status code ::" + statusCode);
                    if(statusCode == 200){
                        log.info("Rest call GET status code :: " + statusCode);
                        emailService.sendNotificationEmail("ECPROCESS", "Successfully this file to xcenter :: ", mntFile.getName());
                    }
                }

            }

        } catch(Exception e){
            System.out.println(e);
            log.warn("An error has occurred while running the job", e);
            emailService.sendNotificationEmail("ECGENERIC", "An error has occurred while running the job", e.toString());
        }
    }

    private void processTaxGroup(PrintWriter pw) throws IOException {
        List<TaxGroup> taxGroupList = taxGroupRepository.findAll();
        for(TaxGroup taxGroup : taxGroupList) {

            pw.println((taxGroup.getActionCode() != null ? taxGroup.getActionCode() : "") + "|"
                    + taxGroup.getRecordIdentifier() + "|"
                    + taxGroup.getTaxGroupId() + "|"
                    + taxGroup.getName() + "|"
                    + taxGroup.getDescription() + "|"
                    + (taxGroup.getOrganizationCode() != null ? taxGroup.getOrganizationCode() : "") + "|"
                    + (taxGroup.getOrganizationValue() != null ? taxGroup.getOrganizationValue() : ""));
        }
    }

    private void processTaxAuthority(PrintWriter pw) throws IOException {
        List<TaxAuthority> taxAuthorityList = taxAuthorityRepository.findAll();
        for(TaxAuthority taxAuthority : taxAuthorityList) {
            pw.println((taxAuthority.getActionCode() != null ? taxAuthority.getActionCode() : "") + "|"
                    + taxAuthority.getRecordIdentifier() + "|"
                    + taxAuthority.getTaxAuthorityId() + "|"
                    + taxAuthority.getName() + "|"
                    + taxAuthority.getRoundingCode() + "|"
                    + taxAuthority.getRoundingDigitsQuantity() + "|"
                    + (taxAuthority.getOrganizationCode() != null ? taxAuthority.getOrganizationCode() : "") + "|"
                    + (taxAuthority.getOrganizationValue() != null ? taxAuthority.getOrganizationValue() : ""));
        }
    }

    private void processTaxLocation(PrintWriter pw) throws IOException {
        List<TaxLocation> taxLocationList = taxLocationRepository.findAll();
        for(TaxLocation taxLocation : taxLocationList) {
            pw.println((taxLocation.getActionCode() != null ? taxLocation.getActionCode() : "") + "|"
                    + taxLocation.getRecordIdentifier() + "|"
                    + taxLocation.getTaxLocationId() + "|"
                    + taxLocation.getName() + "|"
                    + taxLocation.getDescription() + "|"
                    + (taxLocation.getOrganizationCode() != null ? taxLocation.getOrganizationCode() : "") + "|"
                    + (taxLocation.getOrganizationValue() != null ? taxLocation.getOrganizationValue() : ""));
        }
    }

    private void processTaxGroupRule(PrintWriter pw) throws IOException {
        List<TaxGroupRule> taxGroupRuleList = taxGroupRuleRepository.findAll();
        for(TaxGroupRule taxGroupRule : taxGroupRuleList) {
            pw.println((taxGroupRule.getActionCode() != null ? taxGroupRule.getActionCode() : "") + "|"
                    + taxGroupRule.getRecordIdentifier() + "|"
                    + taxGroupRule.getTaxLocationId() + "|"
                    + taxGroupRule.getTaxGroupId() + "|"
                    + taxGroupRule.getTaxGroupRuleId() + "|"
                    + taxGroupRule.getName() + "|"
                    + taxGroupRule.getTaxTypeCode() + "|"
                    + taxGroupRule.getTaxAuthorityId() + "|"
                    + (taxGroupRule.getTaxAtTransLevelFlag() != null ? taxGroupRule.getTaxAtTransLevelFlag() : "") + "|"
                    + (taxGroupRule.getCompoundFlag() != null ? taxGroupRule.getCompoundFlag() : "") + "|"
                    + (taxGroupRule.getCompoundId() != null ? taxGroupRule.getCompoundId() : "") + "|"
                    + taxGroupRule.getDescription() + "|"
                    + (taxGroupRule.getOrganizationCode() != null ? taxGroupRule.getOrganizationCode() : "") + "|"
                    + (taxGroupRule.getOrganizationValue() != null ? taxGroupRule.getOrganizationValue() : ""));
        }
    }

    private void processTaxRateRule(PrintWriter pw) throws IOException {
        List<TaxRateRule> taxRateRuleList = taxRateRuleRepository.findAll();
        for(TaxRateRule taxRateRule : taxRateRuleList) {
            pw.println((taxRateRule.getActionCode() != null ? taxRateRule.getActionCode() : "") + "|"
                    + taxRateRule.getRecordIdentifier() + "|"
                    + taxRateRule.getTaxGroupId() + "|"
                    + taxRateRule.getTaxLocationId() + "|"
                    + taxRateRule.getTaxRuleId() + "|"
                    + taxRateRule.getTaxRateRuleId() + "|"
                    + (taxRateRule.getTaxRateMinAmt() != null ? taxRateRule.getTaxRateMinAmt() : "") + "|"
                    + (taxRateRule.getTaxRateMaxAmt() != null ? taxRateRule.getTaxRateMaxAmt() : "") + "|"
                    + (taxRateRule.getAmount() != null ? taxRateRule.getAmount() : "") + "|"
                    + taxRateRule.getPercent() + "|"
                    + (taxRateRule.getBreakPointTypeCode() != null ? taxRateRule.getBreakPointTypeCode() : "") + "|"
                    + taxRateRule.getEffectiveDateTime() + "|"
                    + taxRateRule.getEffectiveDateTime() + "|"
                    + taxRateRule.getExpryDateTime() + "|"
                    + (taxRateRule.getDailyStartTime() != null ? taxRateRule.getDailyStartTime() : "") + "|"
                    + (taxRateRule.getDailyEndTime() != null ? taxRateRule.getDailyEndTime() : "") + "|"
                    + (taxRateRule.getTaxBracketId() != null ? taxRateRule.getTaxBracketId() : "") + "|"
                    + (taxRateRule.getOrganizationCode() != null ? taxRateRule.getOrganizationCode() : "") + "|"
                    + (taxRateRule.getOrganizationValue() != null ? taxRateRule.getOrganizationValue() : ""));
        }
    }

    private void processTaxRetailLocation(PrintWriter pw) throws IOException {
        Map<String, String> map = rmsAddrRepository.fetchRMSAddrByModuleAndAddrType();
        map.entrySet().forEach(entry -> {
            try {
                if( entry.getKey() != null && entry.getValue() != null){
                    pw.println("INSERT" + "|" + "TAX_RETAIL_LOCATION_MAPPING" + "|" + entry.getKey() + "|" + entry.getValue());
                }
            } catch (Exception e) {
                log.warn("An error has occurred while running the job", e);
            }
        });
    }

    private void processMntFileData(PrintWriter pw) throws IOException {
        List<String> itemLocList = rmsAddrRepository.fetchRMSItemLocData();
        for(String itemLoc : itemLocList) {
            if(itemLoc != null && !itemLoc.isEmpty()){
                pw.println(itemLoc);
            }
        }
    }

    private void zipFiles(String dateFolder, String file, SftpSession sftpSession)
            throws SftpException, IOException {
        HashMap<String, byte[]> files = new HashMap<>();
        InputStream inputFile = sftpSession.getClientInstance().get(file);
        var bytes = inputFile.readAllBytes();
        files.put(file.replace(rootFolder + "/Outbound/" + dateFolder + "/", ""), bytes);

        sendZipFileToServer(dateFolder,file.replace(rootFolder + "/Outbound/" + dateFolder + "/", ""),addFilesToZip(files), sftpSession);
    }

    public byte[] addFilesToZip(HashMap<String, byte[]> files) {
        byte[] result = null;
        try (ByteArrayOutputStream fos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(fos);) {
            for (Map.Entry<String, byte[]> fileToZip : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(fileToZip.getKey());
                zipOut.putNextEntry(zipEntry);
                IOUtils.copy(new ByteArrayInputStream(fileToZip.getValue()), zipOut);
            }
            zipOut.close();
            fos.close();
            result = fos.toByteArray();
        } catch (Exception e) {
            log.warn("An error has occurred while adding files to zip ", e);
        }
        return result;
    }

    private void sendZipFileToServer( String dateFolder, String zipFileName, byte[] byteArrayOutputStream, SftpSession sftpSession) {
        String outPutFilePath = rootFolder + "/Outbound/" + dateFolder + "/" + zipFileName + ".zip";
        try {
            sftpSession.getClientInstance().put(new ByteArrayInputStream(byteArrayOutputStream), outPutFilePath, ChannelSftp.OVERWRITE);
        } catch (SftpException e) {
            log.warn("Unable to move zip file", e);
        }
    }


}

