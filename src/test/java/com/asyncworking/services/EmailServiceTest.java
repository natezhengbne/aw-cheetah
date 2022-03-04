package com.asyncworking.services;

import com.asyncworking.constants.EmailType;
import com.asyncworking.constants.Status;
import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.EmailMapper;
import com.asyncworking.utility.mapper.EmailMapperImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
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
                .name("GJFJH ABC")
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
    public void shouldSaveCompanyInvitationEmailSendingRecord() {
        String receiverEmail = "test@gmail.com";
        String receiverName = "Alice S";
        Long companyId = 1L;
        UserEntity receiver = UserEntity.builder()
                .name(receiverName)
                .email(receiverEmail)
                .build();

        ArgumentCaptor<EmailSendRecord> emailSendRecordCaptor = ArgumentCaptor.forClass(EmailSendRecord.class);
        emailService.saveCompanyInvitationEmailSendingRecord(
                receiver, EmailType.CompanyInvitation, receiverEmail, companyId);
        verify(emailSendRepository).save(emailSendRecordCaptor.capture());

        EmailSendRecord savedEmailSendRecord = emailSendRecordCaptor.getValue();
        assertEquals(receiverEmail, savedEmailSendRecord.getReceiver());
    }

    @Test
    public void shouldSendCompanyInvitationSQSMsg() throws JsonProcessingException {
        Message expectedMessage = MessageBuilder.withPayload("payload").build();
        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        when(objectMapper.writeValueAsString(any())).thenReturn("payload");
        emailService.sendCompanyInvitationMessageToSQS(
                1L,
                "Alice S",
                "test@gmail.com",
                "companyA",
                "companyOwner B",
                "invitationLink",
                EmailType.CompanyInvitation
        );
        verify(queueMessagingTemplate).send(endpointCaptor.capture(), messageCaptor.capture());
        assertEquals(expectedMessage.getPayload(), messageCaptor.getValue().getPayload());
    }

    @Test
    public void shouldUpdateEmailRecordStatus() {
        when(emailSendRepository.updateEmailRecordStatus(any(), any())).thenReturn(1);
        assertEquals(1, emailService.updateEmailRecordSendStatus(1L));
    }

//    @Test
//    public void shouldSendMessageToSQS() throws JsonProcessingException {
//        Message expectedMessage = MessageBuilder.withPayload("payload").build();
//        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
//        when(objectMapper.writeValueAsString(any())).thenReturn("payload");
//        emailService.sendMessageToSQS(
//                mockUserEntity,
//                "verificationLink",
//                EmailType.Verification,
//                "test@gmail.com"
//        );
//        verify(queueMessagingTemplate).send(endpointCaptor.capture(), messageCaptor.capture());
//        assertEquals(expectedMessage.getPayload(), messageCaptor.getValue().getPayload());
//    }
}
