package com.asyncworking.services;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.CompanyInvitationEmailMessageDto;
import com.asyncworking.dtos.EmailMessageDto;
import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.utility.mapper.EmailMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final EmailMapper emailMapper;

    private final EmailSendRepository emailSendRepository;

    @Value("${cloud.aws.sqs.outgoingqueue.url}")
    private String endPoint;

    @Value("${cloud.aws.S3.templateS3Bucket}")
    private String s3Bucket;

    @Value("${cloud.aws.S3.templateS3Key}")
    private String s3Key;

    @Value("${cloud.aws.S3.templateResetPasswordS3Key}")
    private String s3resetPasswordTemplateKey;

    @Value("${cloud.aws.S3.templateCompanyInvitationS3Key}")
    private String s3CompanyInvitationTemplateKey;

    private final Map<EmailType, String> emailType = new HashMap<>();

    @PostConstruct
    private void initMap() {
        emailType.put(EmailType.Verification, s3Key);
        emailType.put(EmailType.ForgetPassword, s3resetPasswordTemplateKey);
        emailType.put(EmailType.CompanyInvitation, s3CompanyInvitationTemplateKey);
    }

    public void sendMessageToSQS(EmailSendRecord emailSendRecord, UserEntity userEntity, String verificationLink) {
        try {
            EmailMessageDto messageDto = EmailMessageDto.builder()
                    .emailRecordId(emailSendRecord.getId())
                    .userName(userEntity.getName())
                    .verificationLink(verificationLink)
                    .templateType(emailSendRecord.getEmailType().toString())
                    .email(emailSendRecord.getReceiver())
                    .templateS3Bucket(s3Bucket)
                    .templateS3Key(emailType.get(emailSendRecord.getEmailType()))
                    .build();

            queueMessagingTemplate.send(
                    endPoint,
                    MessageBuilder.withPayload(objectMapper.writeValueAsString(messageDto)).build()
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendCompanyInvitationMessageToSQS(
            Long emailRecordId,
            String receiverName,
            String receiverEmail,
            String companyName,
            String companyOwnerName,
            String invitationLink,
            EmailType templateType
    ) throws JsonProcessingException {
        CompanyInvitationEmailMessageDto messageDto = toCompanyInvitationEmailMessageDto(
                emailRecordId,
                receiverName,
                receiverEmail,
                companyName,
                companyOwnerName,
                invitationLink,
                templateType);
        log.info("Company Email Message Record Id: {}", emailRecordId);
        String payload = objectMapper.writeValueAsString(messageDto);
        queueMessagingTemplate.send(endPoint, MessageBuilder.withPayload(payload).build());
    }

    @Transactional
    public EmailSendRecord saveEmailSendingRecord(UserEntity userEntity, EmailType templateType, String receiverEmail) {
        return emailSendRepository.save(
                emailMapper.toEmailSendRecord(userEntity, templateType, receiverEmail)
        );
    }

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
    public int updateEmailRecordSendStatus(Long emailRecordId) {
        return emailSendRepository.updateEmailRecordStatus(emailRecordId, OffsetDateTime.now(UTC));
    }

    private CompanyInvitationEmailMessageDto toCompanyInvitationEmailMessageDto(
            Long emailRecordId, String receiverName, String receiverEmail,
            String companyName, String companyOwnerName, String link, EmailType templateType) {
        return CompanyInvitationEmailMessageDto.builder()
                .emailRecordId(emailRecordId)
                .email(receiverEmail)
                .userName(receiverName)
                .companyName(companyName)
                .companyOwnerName(companyOwnerName)
                .invitationLink(link)
                .templateType(templateType)
                .templateS3Bucket(s3Bucket)
                .templateS3Key(emailType.get(templateType))
                .build();
    }

    @Transactional
    public EmailSendRecord saveCompanyInvitationEmailSendingRecord(
            UserEntity receiver, EmailType templateType, String receiverEmail, Long companyId) {
        EmailSendRecord emailSendRecord = toEmailSendRecordWithCompanyId(
                receiver, templateType, receiverEmail, companyId);
        log.info("Email Sending Record id: {}, Receiver Email: {}", emailSendRecord.getId(), emailSendRecord.getReceiver());
        return emailSendRepository.save(emailSendRecord);
    }

    public static EmailSendRecord toEmailSendRecordWithCompanyId(
            UserEntity userEntity, EmailType emailType, String receiverEmail, Long companyId) {
        if (userEntity == null && emailType == null && receiverEmail == null && companyId == null) {
            return null;
        }

        EmailSendRecord emailSendRecord = new EmailSendRecord();

        if (userEntity != null) {
            emailSendRecord.setUserEntity(userEntity);
        }
        if (emailType != null) {
            emailSendRecord.setEmailType(emailType);
        }
        if (receiverEmail != null) {
            emailSendRecord.setReceiver(receiverEmail);
        }
        if (companyId != null) {
            emailSendRecord.setCompanyId(companyId);
        }
        emailSendRecord.setSendStatus(false);
        emailSendRecord.setSendTime(OffsetDateTime.now());

        return emailSendRecord;
    }
}
