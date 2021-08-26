package com.asyncworking.services;

import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.constants.EmailType;
import com.asyncworking.constants.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.utility.mapper.EmailSendRecordMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class EmailServiceTest {
    private QueueMessagingTemplate queueMessagingTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EmailSendRepository emailSendRepository;

    @InjectMocks
    private EmailService emailService;

    private EmailSendRecord mockEmailSendRecord;

    private UserEntity mockUserEntity;

    private EmailSendRecordMapper emailSendRecordMapper;

    @BeforeEach
    public void setUP() {
        emailService = new EmailService(
                queueMessagingTemplate,
                objectMapper,
                emailSendRecordMapper,
                emailSendRepository

        );

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

//    @Test
//    @Transactional
//    public void shouldCreateEmailSendRecord() {
////        EmailSendRecord savedEmailSendRecord = emailService.saveEmailSendingRecord(mockUserEntity,
////                EmailType.Verification, mockUserEntity.getEmail());
////
////        Assertions.assertEquals("test0@gmail.com", savedEmailSendRecord.getReceiver());
////        Assertions.assertEquals(EmailType.Verification, savedEmailSendRecord.getEmailType());
//        verify(emailService, times(1)).saveEmailSendingRecord(mockUserEntity, EmailType.Verification, mockUserEntity.getEmail());
//    }
}
