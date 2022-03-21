package com.asyncworking.aws;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.EmailContentDto;
import com.asyncworking.dtos.EmailMessageDto;
import com.asyncworking.exceptions.EmailSendFailException;
import com.asyncworking.utility.mapper.EmailMapperImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AmazonSQSSenderTest {

    private AmazonSQSSender sqsSender;

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    private EmailContentDto mockEmailContentDto;

    @BeforeEach
    public void setUp() {
        sqsSender = new AmazonSQSSender(queueMessagingTemplate, new ObjectMapper(), new EmailMapperImpl());
        ReflectionTestUtils.setField(sqsSender, "endPoint", "http://localhost:4566/000000000000/AWVerificationEmailBasicPP");
        ReflectionTestUtils.setField(sqsSender, "s3Bucket", "aw-email-template");
        ReflectionTestUtils.setField(sqsSender, "s3Key", "verification_email_template_updated.html");
        ReflectionTestUtils.setField(sqsSender, "s3resetPasswordTemplateKey", "reset_password_email_template.txt");
        ReflectionTestUtils.setField(sqsSender, "s3CompanyInvitationTemplateKey", "company_invitation_email_template.html");

        mockEmailContentDto = EmailContentDto.builder()
                .userName("Test")
                .email("test@gmail.com")
                .companyOwnerName("Joe Doe")
                .companyName("AW")
                .verificationLink("http://test")
                .templateType(EmailType.CompanyInvitation.toString())
                .build();
    }

    @Test
    public void test_sendEmailMessage_OK() {
        doNothing().when(queueMessagingTemplate).send(anyString(), any(Message.class));

        sqsSender.sendEmailMessage(mockEmailContentDto, 1L);

        verify(queueMessagingTemplate, times(1))
                .send(anyString(), any(Message.class));
    }

    @Test
    public void test_sendEmailMessage_whenMessageSendFail() {
        doThrow(new RuntimeException()).when(queueMessagingTemplate).send(anyString(), any(Message.class));

        assertThrows(EmailSendFailException.class,
                () -> sqsSender.sendEmailMessage(mockEmailContentDto, 1L)
        );

        verify(queueMessagingTemplate, times(1)).send(anyString(), any(Message.class));
    }
}
