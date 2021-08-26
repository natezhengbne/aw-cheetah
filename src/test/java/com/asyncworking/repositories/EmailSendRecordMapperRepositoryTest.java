package com.asyncworking.repositories;

import com.asyncworking.constants.EmailType;
import com.asyncworking.constants.Status;
import com.asyncworking.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@SpringBootTest
@ActiveProfiles("test")
public class EmailSendRecordMapperRepositoryTest extends DBHelper{

    private UserEntity mockUserEntity;

    private EmailSendRecord mockEmailSendRecord;

    @BeforeEach
    public void createMockData() {
        clearDb();

        mockUserEntity = UserEntity.builder()
                .email("test0@gmail.com")
                .password("Iampassword")
                .name("GJFJH")
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        mockEmailSendRecord = EmailSendRecord.builder()
                .emailType(EmailType.Verification)
                .userEntity(mockUserEntity)
                .receiver("test0@gmail.com")
                .sendTime(OffsetDateTime.now(UTC))
                .build();
    }

    @Test
    public void giveEmailInfo_whenSavedAndRetrievesRecord_thenOk() {
        EmailSendRecord savedEmailSendRecord = emailSendRepository.save(mockEmailSendRecord);
        Assertions.assertNotNull(savedEmailSendRecord);
        Assertions.assertEquals("test0@gmail.com", savedEmailSendRecord.getReceiver());
    }
}
