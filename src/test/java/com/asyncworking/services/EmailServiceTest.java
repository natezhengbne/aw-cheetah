package com.asyncworking.services;

import com.asyncworking.aws.AmazonSQSSender;
import com.asyncworking.constants.EmailType;
import com.asyncworking.constants.Status;
import com.asyncworking.dtos.EmailMessageDto;
import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.utility.mapper.EmailMapper;
import com.asyncworking.utility.mapper.EmailMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private EmailSendRepository emailSendRepository;

    private EmailService emailService;

    private EmailSendRecord mockEmailSendRecord;

    private UserEntity mockUserEntity;

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

        mockUserEntity = UserEntity.builder()
                .id(1L)
                .email("test0@gmail.com")
                .password("Iampassword")
                .name("Mail Test")
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        mockEmailSendRecord = EmailSendRecord.builder()
                .emailType(EmailType.Verification)
                .receiver("test0@gmail.com")
                .sendTime(OffsetDateTime.now(UTC))
                .build();
    }

    @Test
    public void test_sendLinkByEmail1_ok() {
        doNothing().when(amazonSQSSender).sendEmailMessage(any(EmailMessageDto.class));
        when(emailSendRepository.save(any(EmailSendRecord.class))).thenReturn(mockEmailSendRecord);

        emailService.sendLinkByEmail(EmailType.Verification, anyString(), mockUserEntity);

        verify(emailSendRepository, times(1)).save(any(EmailSendRecord.class));
        verify(amazonSQSSender, times(1)).sendEmailMessage(any(EmailMessageDto.class));
    }

    @Test
    public void test_sendLinkByEmail2_ok() {
        doNothing().when(amazonSQSSender).sendEmailMessage(any(EmailMessageDto.class));
        when(emailSendRepository.save(any(EmailSendRecord.class))).thenReturn(mockEmailSendRecord);

        emailService.sendLinkByEmail(
                EmailType.CompanyInvitation, anyString(),
                "tester", "test@gmail.com",
                "Async Working", "John Doe"
        );

        verify(emailSendRepository, times(1)).save(any(EmailSendRecord.class));
        verify(amazonSQSSender, times(1)).sendEmailMessage(any(EmailMessageDto.class));

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

//    @Test
//    public void createEmailSendRecordSuccess() {
//        lenient().when(userRepository.findById(1L))
//                .thenReturn(Optional.of(mockUserEntity));
//        ArgumentCaptor<EmailSendRecord> emailSendRecordArgumentCaptor = ArgumentCaptor
//                .forClass(EmailSendRecord.class);
//        emailService.saveEmailSendRecord(mockUserEntity, EmailType.Verification,
//                mockUserEntity.getEmail());
//        verify(emailSendRepository).save(emailSendRecordArgumentCaptor.capture());
//        assertEquals(mockUserEntity, emailSendRecordArgumentCaptor.getValue().getUserEntity());
//    }


//    public void test_saveEmailSendingRecord_ok() {

//        String receiverEmail = "test@gmail.com";
//        String receiverName = "Alice S";
//        Long companyId = 1L;
//        UserEntity receiver = UserEntity.builder()
//                .name(receiverName)
//                .email(receiverEmail)
//                .build();
//
//        ArgumentCaptor<EmailSendRecord> emailSendRecordCaptor = ArgumentCaptor.forClass(EmailSendRecord.class);
//        emailService.saveCompanyInvitationEmailSendingRecord(
//                receiver, EmailType.CompanyInvitation, receiverEmail, companyId);
//        verify(emailSendRepository).save(emailSendRecordCaptor.capture());
//
//        EmailSendRecord savedEmailSendRecord = emailSendRecordCaptor.getValue();
//        assertEquals(receiverEmail, savedEmailSendRecord.getReceiver());

//    }

//    @Test
//    public void shouldSendCompanyInvitationSQSMsg() throws JsonProcessingException {
//        Message expectedMessage = MessageBuilder.withPayload("payload").build();
//        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
//        when(objectMapper.writeValueAsString(any())).thenReturn("payload");
//        emailService.sendCompanyInvitationMessageToSQS(
//                1L,
//                "Alice S",
//                "test@gmail.com",
//                "companyA",
//                "companyOwner B",
//                "invitationLink",
//                EmailType.CompanyInvitation
//        );
//        verify(queueMessagingTemplate).send(endpointCaptor.capture(), messageCaptor.capture());
//        assertEquals(expectedMessage.getPayload(), messageCaptor.getValue().getPayload());
//    }


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
