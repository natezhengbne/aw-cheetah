package com.asyncworking.services;

import com.asyncworking.aws.AmazonSQSSender;
import com.asyncworking.constants.EmailType;
import com.asyncworking.constants.Status;
import com.asyncworking.dtos.EmailContentDto;
import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.utility.mapper.EmailMapper;
import com.asyncworking.utility.mapper.EmailMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private EmailSendRepository emailSendRepository;

    private EmailService emailService;

    private EmailSendRecord mockEmailSendRecord;

    private EmailContentDto mockEmailContentDto;

    private EmailMapper emailMapper;

    @Mock
    private AmazonSQSSender amazonSQSSender;

    @BeforeEach
    public void setUP() {
        emailMapper = new EmailMapperImpl();
        emailService = new EmailService(
                emailMapper,
                emailSendRepository,
                amazonSQSSender);

        mockEmailContentDto = EmailContentDto.builder()
                .templateType(EmailType.Verification.toString())
                .email("test0@gmail.com")
                .build();

        mockEmailSendRecord = EmailSendRecord.builder()
                .id(1L)
                .emailType(EmailType.Verification)
                .receiver("test0@gmail.com")
                .sendTime(OffsetDateTime.now(UTC))
                .build();
    }

    @Test
    public void test_sendLinkByEmail1_ok() {
        doNothing().when(amazonSQSSender).sendEmailMessage(mockEmailContentDto, 1L);
        when(emailSendRepository.save(any(EmailSendRecord.class))).thenReturn(mockEmailSendRecord);

        emailService.sendLinkByEmail(mockEmailContentDto, 1L);

        verify(emailSendRepository, times(1)).save(any(EmailSendRecord.class));
        verify(amazonSQSSender, times(1)).sendEmailMessage(mockEmailContentDto, 1L);
    }

    @Test
    public void test_saveEmailSendRecord_ok() {
        when(emailSendRepository.save(any(EmailSendRecord.class))).thenReturn(any(EmailSendRecord.class));

        emailService.saveEmailSendRecord(1L, EmailType.Verification, "test0@gmail.com");

        verify(emailSendRepository, times(1)).save(any(EmailSendRecord.class));
    }

    @Test
    public void test_UpdateEmailRecordStatus_ok() {
        when(emailSendRepository.updateEmailRecordStatus(any(), any())).thenReturn(1);

        assertEquals(1, emailService.updateEmailRecordSendStatus(1L));
    }
}
