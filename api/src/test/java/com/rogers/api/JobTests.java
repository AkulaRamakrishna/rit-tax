package com.rogers.api;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import com.rogers.api.repository.*;
import com.rogers.api.service.EmailService;

import com.rogers.api.service.HttpService;
import org.junit.jupiter.api.Test;

import feign.FeignException;
import feign.Request;
import lombok.Value;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

@Value
public class JobTests {

    EmailService emailService = mock(EmailService.class);

    HttpService httpService = mock(HttpService.class);

    DefaultSftpSessionFactory sftpSessionFactory = mock(DefaultSftpSessionFactory.class);

    private static final String ROOT_FOLDER = "/tmp/mock";

    TaxAuthorityRepository taxAuthorityRepository = mock(TaxAuthorityRepository.class);

    TaxGroupRepository taxGroupRepository = mock(TaxGroupRepository.class);

    TaxLocationRepository taxLocationRepository = mock(TaxLocationRepository.class);

    TaxGroupRuleRepository taxGroupRuleRepository = mock(TaxGroupRuleRepository.class);

    TaxRateRuleRepository taxRateRuleRepository = mock(TaxRateRuleRepository.class);

    RMSAddrRepository rmsAddrRepository = mock(RMSAddrRepository.class);

    Job test = new Job(emailService, httpService, sftpSessionFactory, ROOT_FOLDER, taxAuthorityRepository, taxGroupRepository, taxLocationRepository, taxGroupRuleRepository, taxRateRuleRepository, rmsAddrRepository);

    Request request = mock(Request.class);
    FeignException.Unauthorized exception = mock(FeignException.Unauthorized.class);

    @Test
    void testProduce_ConsumeWebService() throws IOException {
        test.run();

        // verify(inventoryAdjustmentPortTypeFeignClient,
        // atLeastOnce()).lookupInventoryAdjustmentReason(any());
    }

    @Test
    void testProduce_FailConsumeWebService() throws IOException {
        // when(inventoryAdjustmentPortTypeFeignClient.lookupInventoryAdjustmentReason(any())).thenThrow(exception);

        test.run();

        // verify(inventoryAdjustmentReasonRepository, never()).saveAll(any());
        // verify(emailService, atLeastOnce()).sendNotificationEmail(any(), any(),
        // any());
    }

}
