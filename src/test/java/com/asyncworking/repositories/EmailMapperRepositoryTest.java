package com.asyncworking.repositories;

import com.asyncworking.constants.EmailType;
import com.asyncworking.models.EmailSendRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@SpringBootTest
@ActiveProfiles("test")
public class EmailMapperRepositoryTest extends DBHelper {

    private EmailSendRecord mockEmailSendRecord;

    @BeforeEach
    public void setUp() {
        clearDb();
        mockEmailSendRecord = EmailSendRecord.builder()
                .emailType(EmailType.CompanyInvitation)
                .receiver("test@gmail.com")
                .companyId(1L)
                .userId(1L)
                .id(1L)
                .sendStatus(false)
                .sendTime(OffsetDateTime.now(UTC))
                .build();
    }


    @Test
    public void giveEmailInfo_whenSavedAndRetrievesRecord_thenOk() {
        EmailSendRecord savedEmailSendRecord = emailSendRepository.save(mockEmailSendRecord);
        Assertions.assertNotNull(savedEmailSendRecord);
        Assertions.assertEquals("test@gmail.com", savedEmailSendRecord.getReceiver());
    }
}
