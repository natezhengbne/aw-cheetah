package com.asyncworking.services;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.asyncworking.config.AmazonSQSConfig;
import com.asyncworking.constants.EmailType;
import com.asyncworking.constants.Status;
import com.asyncworking.controllers.EmailResultListener;
import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.EmailMapper;
import com.asyncworking.utility.mapper.EmailMapperImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @MockBean
    private QueueMessagingTemplate queueMessagingTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EmailSendRepository emailSendRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    private EmailSendRecord mockEmailSendRecord;

    private UserEntity mockUserEntity;

    private EmailMapper emailMapper;

    private AmazonSQSAsync amazonSQSAsync;

    private EmailResultListener emailResultListener;
    private final Map<EmailType, String> emailType = new HashMap<>() {{
        put(EmailType.Verification, "s3Key");
        put(EmailType.ForgetPassword, "s3resetPasswordTemplateKey");
    }};
    @Mock
    private AmazonSQSConfig amazonSQSConfig;

    @BeforeEach
    public void setUP() {
        emailMapper = new EmailMapperImpl();
        emailService = new EmailService(
                queueMessagingTemplate,
                objectMapper,
                emailMapper,
                emailSendRepository);

        mockUserEntity = UserEntity.builder()
                .id(1L)
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
    public void createEmailSendRecordSuccess() {
       lenient().when(userRepository.findById(1L))
                .thenReturn(Optional.of(mockUserEntity));
        ArgumentCaptor<EmailSendRecord> emailSendRecordArgumentCaptor = ArgumentCaptor
                .forClass(EmailSendRecord.class);
        emailService.saveEmailSendingRecord(mockUserEntity, EmailType.Verification,
                mockUserEntity.getEmail());
        verify(emailSendRepository).save(emailSendRecordArgumentCaptor.capture());
        assertEquals(mockUserEntity, emailSendRecordArgumentCaptor.getValue().getUserEntity());
    }


    @Test
    public void send_withDestination_usesDestination() {
        AmazonSQSAsync amazonSqs = createAmazonSqs();
        QueueMessagingTemplate queueMessagingTemplate = new QueueMessagingTemplate(amazonSqs);

        Message<String> stringMessage = MessageBuilder.withPayload("message content").build();
        queueMessagingTemplate.send("test-queue", stringMessage);

        ArgumentCaptor<SendMessageRequest> sendMessageRequestArgumentCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(amazonSqs).sendMessage(sendMessageRequestArgumentCaptor.capture());
        assertEquals("http://test-queue-url.com", sendMessageRequestArgumentCaptor.getValue().getQueueUrl());
    }

    private AmazonSQSAsync createAmazonSqs() {
        AmazonSQSAsync amazonSqs = mock(AmazonSQSAsync.class);

        GetQueueUrlResult queueUrl = new GetQueueUrlResult();
        queueUrl.setQueueUrl("http://test-queue-url.com");
        when(amazonSqs.getQueueUrl(any(GetQueueUrlRequest.class))).thenReturn(queueUrl);

        return amazonSqs;
    }
}
