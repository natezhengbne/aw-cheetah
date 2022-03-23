package com.asyncworking.services;

import com.asyncworking.aws.AmazonSQSSender;
import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.EmailContentDto;
import com.asyncworking.models.EmailSendRecord;
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
    public void sendLinkByEmail(EmailContentDto emailContentDto, Long userId) {
        EmailSendRecord emailSendRecord = saveEmailSendRecord(
                userId,
                EmailType.valueOf(emailContentDto.getTemplateType()),
                emailContentDto.getEmail()
        );

        amazonSQSSender.sendEmailMessage(emailContentDto, emailSendRecord.getId());
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
