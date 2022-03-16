package com.asyncworking.aws;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.EmailMessageDto;
import com.asyncworking.exceptions.EmailSendFailException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AmazonSQSSenderTest {

    @InjectMocks
    private AmazonSQSSender sqsSender;

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private EmailMessageDto mockMessageDto;

    @BeforeEach
    public void setUp() {
        sqsSender.setEndPoint("http://localhost:4566/000000000000/AWVerificationEmailBasicPP");
        sqsSender.setS3Bucket("aw-email-template");
        sqsSender.setS3Key("verification_email_template_updated.html");
        sqsSender.setS3resetPasswordTemplateKey("reset_password_email_template.txt");
        sqsSender.setS3CompanyInvitationTemplateKey("company_invitation_email_template.html");

        mockMessageDto = EmailMessageDto.builder()
                .emailRecordId(1L)
                .userName("Test")
                .receiverEmail("test@gmail.com")
                .companyOwnerName("Joe Doe")
                .companyName("AW")
                .linkToSend("http://test")
                .templateType(EmailType.CompanyInvitation.toString())
                .build();
    }

    @Test
    public void test_sendEmailMessage_OK() throws JsonProcessingException {
        doNothing().when(queueMessagingTemplate).send(anyString(), any(Message.class));
        when(objectMapper.writeValueAsString(mockMessageDto)).thenReturn("TestString");

        sqsSender.sendEmailMessage(mockMessageDto);

        verify(queueMessagingTemplate, times(1))
                .send(anyString(), any(Message.class));
        verify(objectMapper, times(1)).writeValueAsString(mockMessageDto);
    }

    @Test
    public void test_sendEmailMessage_whenJsonConversionFail() throws JsonProcessingException {
        doNothing().when(queueMessagingTemplate).send(anyString(), any(Message.class));
        when(objectMapper.writeValueAsString(new EmailMessageDto())).thenReturn("TestString");

        assertThrows(EmailSendFailException.class,
                () -> sqsSender.sendEmailMessage(mockMessageDto)
        );

        verify(queueMessagingTemplate, times(0))
                .send(anyString(), any(Message.class));
        verify(objectMapper, times(1)).writeValueAsString(mockMessageDto);
    }

    @Test
    public void test_sendEmailMessage_whenMessageSendFail() throws JsonProcessingException {

        doThrow(new RuntimeException()).when(queueMessagingTemplate).send(anyString(), any(Message.class));
        when(objectMapper.writeValueAsString(mockMessageDto)).thenReturn("TestString");

        assertThrows(EmailSendFailException.class,
                () -> sqsSender.sendEmailMessage(mockMessageDto)
        );

        verify(queueMessagingTemplate, times(1))
                .send(anyString(), any(Message.class));
        verify(objectMapper, times(1)).writeValueAsString(mockMessageDto);
    }
}
