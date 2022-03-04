package com.asyncworking.services;

import com.asyncworking.aws.AmazonSQSSender;
import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.EmailMessageDto;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.DateTimeUtility;
import com.asyncworking.utility.mapper.EmailMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final QueueMessagingTemplate queueMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final EmailMapper emailMapper;

    private final EmailSendRepository emailSendRepository;

    private final UserRepository userRepository;

    private final LinkService linkService;

    private final AmazonSQSSender amazonSQSSender;

    public void sendVerificationEmail(String email) {
        UserEntity unverifiedUserEntity = userRepository
                .findUnverifiedStatusByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find unverified user with email: " + email));

        sendVerificationEmail(unverifiedUserEntity);
    }

    public void sendVerificationEmail(UserEntity userEntity){
        String userVerificationLink = linkService.generateUserVerificationLink(
                userEntity.getEmail(),
                DateTimeUtility.MILLISECONDS_IN_DAY
        );

        sendLinkByEmail(
                EmailType.Verification,
                userVerificationLink,
                userEntity
        );
    }

    public void sendPasswordResetEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with id: " + email));

        String passwordRestLink = linkService.generateResetPasswordLink(
                email,
                DateTimeUtility.MILLISECONDS_IN_DAY
        );

        sendLinkByEmail(
                EmailType.Verification,
                passwordRestLink,
                userEntity
        );
    }

    @Transactional
    public void sendLinkByEmail(EmailType emailType, String linkToSend, UserEntity receiverEntity) {
        EmailSendRecord emailSendRecord = saveEmailSendingRecord(
                receiverEntity.getId(),
                emailType,
                receiverEntity.getEmail());

        EmailMessageDto messageDto = EmailMessageDto.builder()
                .emailRecordId(emailSendRecord.getId())
                .userName(receiverEntity.getName())
                .receiverEmail(emailSendRecord.getReceiver())
                .linkToSend(linkToSend)
                .templateType(emailSendRecord.getEmailType().toString())
                .build();
        amazonSQSSender.sendEmailMessage(messageDto);
    }

    @Transactional
    public void sendLinkByEmail(
            EmailType emailType, String linkToSend,
            String receiverName, String receiverEmail,
            String companyName, String companyOwnerName
    ) {
        EmailSendRecord emailSendRecord = saveEmailSendingRecord(null, emailType, receiverEmail);

        EmailMessageDto messageDto = EmailMessageDto.builder()
                .emailRecordId(emailSendRecord.getId())
                .userName(receiverName)
                .receiverEmail(receiverEmail)
                .companyName(companyName)
                .companyOwnerName(companyOwnerName)
                .linkToSend(linkToSend)
                .templateType(emailType.toString())
                .build();
        amazonSQSSender.sendEmailMessage(messageDto);
    }

//    public void sendCompanyInvitationMessageToSQS(
//            Long emailRecordId,
//            String receiverName,
//            String receiverEmail,
//            String companyName,
//            String companyOwnerName,
//            String invitationLink,
//            EmailType templateType
//    ) throws JsonProcessingException {
//        CompanyInvitationEmailMessageDto messageDto = toCompanyInvitationEmailMessageDto(
//                emailRecordId,
//                receiverName,
//                receiverEmail,
//                companyName,
//                companyOwnerName,
//                invitationLink,
//                templateType);
//        log.info("Company Email Message Record Id: {}", emailRecordId);
//        amazonSQSSender.sendEmailMessage(messageDto);
//        String payload = objectMapper.writeValueAsString(messageDto);
//        queueMessagingTemplate.send(endPoint, MessageBuilder.withPayload(payload).build());
//    }

//    private EmailMessageDto toEmailMessageDto(UserEntity userEntity, String link,
//                                              EmailType templateType, String receiverEmail) {
//        String userFirstName = userEntity.getName();
//        if (userFirstName.contains(" ")) {
//            userFirstName = userFirstName.substring(0, userFirstName.indexOf(" "));
//        }
//        return EmailMessageDto.builder()
//                .userName(userFirstName)
//                .verificationLink(link)
//                .templateType(templateType.toString())
//                .email(receiverEmail)
//                .templateS3Bucket(s3Bucket)
//                .templateS3Key(emailType.get(templateType))
//                .build();
//    }

    @Transactional
    public EmailSendRecord saveEmailSendingRecord(Long userId, EmailType templateType, String receiverEmail) {
        return emailSendRepository.save(
                emailMapper.toEmailSendRecord(userId, templateType, receiverEmail)
        );
    }

    @Transactional
    public int updateEmailRecordSendStatus(Long emailRecordId) {
        return emailSendRepository.updateEmailRecordStatus(emailRecordId, OffsetDateTime.now(UTC));
    }

//    private CompanyInvitationEmailMessageDto toCompanyInvitationEmailMessageDto(
//            Long emailRecordId, String receiverName, String receiverEmail,
//            String companyName, String companyOwnerName, String link, EmailType templateType) {
//        return CompanyInvitationEmailMessageDto.builder()
//                .emailRecordId(emailRecordId)
//                .email(receiverEmail)
//                .userName(receiverName)
//                .companyName(companyName)
//                .companyOwnerName(companyOwnerName)
//                .invitationLink(link)
//                .templateType(templateType)
//                .templateS3Bucket(s3Bucket)
//                .templateS3Key(emailType.get(templateType))
//                .build();
//    }

//    @Transactional
//    public EmailSendRecord saveCompanyInvitationEmailSendingRecord(
//            UserEntity receiver, EmailType templateType, String receiverEmail, Long companyId) {
//        EmailSendRecord emailSendRecord = toEmailSendRecordWithCompanyId(
//                receiver, templateType, receiverEmail, companyId);
//        log.info("Email Sending Record id: {}, Receiver Email: {}", emailSendRecord.getId(), emailSendRecord.getReceiver());
//        return emailSendRepository.save(emailSendRecord);
//    }
}
