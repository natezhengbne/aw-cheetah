package com.asyncworking.services;

import com.asyncworking.aws.AmazonSQSSender;
import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.EmailMessageDto;
import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.utility.mapper.EmailMapper;
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

    private final EmailMapper emailMapper;

    private final EmailSendRepository emailSendRepository;

    private final AmazonSQSSender amazonSQSSender;

    @Transactional
    public void sendLinkByEmail(EmailType emailType, String linkToSend, UserEntity receiverEntity) {
        EmailSendRecord emailSendRecord = saveEmailSendRecord(
                receiverEntity.getId(),
                emailType,
                receiverEntity.getEmail());

        EmailMessageDto messageDto = EmailMessageDto.builder()
                .emailRecordId(emailSendRecord.getId())
                .userName(receiverEntity.getName())
                .email(emailSendRecord.getReceiver())
                .verificationLink(linkToSend)
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
        EmailSendRecord emailSendRecord = saveEmailSendRecord(null, emailType, receiverEmail);

        EmailMessageDto messageDto = EmailMessageDto.builder()
                .emailRecordId(emailSendRecord.getId())
                .userName(receiverName)
                .email(receiverEmail)
                .companyName(companyName)
                .companyOwnerName(companyOwnerName)
                .verificationLink(linkToSend)
                .templateType(emailType.toString())
                .build();
        amazonSQSSender.sendEmailMessage(messageDto);
    }

    @Transactional
    public EmailSendRecord saveEmailSendRecord(Long userId, EmailType templateType, String receiverEmail) {
        return emailSendRepository.save(
                emailMapper.toEmailSendRecord(userId, templateType, receiverEmail)
        );
    }

    @Transactional
    public int updateEmailRecordSendStatus(Long emailRecordId) {
        return emailSendRepository.updateEmailRecordStatus(emailRecordId, OffsetDateTime.now(UTC));
    }
}
