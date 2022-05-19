package com.rogers.api.service;

import java.time.LocalDateTime;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@lombok.Value
@Component
public class EmailService {

    JavaMailSender javaMailSender;

    String applicationName;

    String emailTo;

    String personal;
    String emailFrom;

    public EmailService(JavaMailSender javaMailSender,
            @Value("${spring.application.name}") final String applicationName,
            @Value("${application.email.to}") final String emailTo,
            @Value("${spring.mail.properties.personal}") final String personal,
            @Value("${spring.mail.username}") final String emailFrom,
            @Value("${spring.mail.properties.keystore.location}") final String keyStoreLocation,
            @Value("${spring.mail.properties.keystore.password}") final String keyStorePassword,
            @Value("${spring.mail.properties.keystore.type}") final String keyStoreType) {
        this.javaMailSender = javaMailSender;
        this.applicationName = applicationName;
        this.emailTo = emailTo;
        this.personal = personal;
        this.emailFrom = emailFrom;
        configureTrustStore(keyStoreLocation, keyStorePassword, keyStoreType);
    }

    private void configureTrustStore(String keyStoreLocation, String keyStorePassword, String keyStoreType) {
        System.setProperty("javax.net.ssl.trustStore", keyStoreLocation);
        System.setProperty("javax.net.ssl.trustStorePassword", keyStorePassword);
        System.setProperty("javax.net.ssl.trustStoreType", keyStoreType);
    }

    public void sendNotificationEmail(String errorCode, String message, String details) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(emailFrom, personal);
            helper.setTo(emailTo);
            helper.setSubject("Error notification: " + applicationName + " - " + errorCode);

            String body = "";
            body += "<p><b>Timestamp:</b> " + LocalDateTime.now() + "</p>";
            body += "<p><b>Application name:</b> " + applicationName + "</p>";
            body += "<p><b>Error code:</b> " + errorCode + "</p>";
            body += "<p><b>Message:</b> " + message + "</p>";
            body += "<p><b>Details:</b> " + details + "</p>";

            helper.setText(body, true);

            javaMailSender.send(mimeMessage);
            log.info("The notification email has been sent");
        } catch (Exception e) {
            log.error("Unable to send email notification", e);
        }
    }

}
