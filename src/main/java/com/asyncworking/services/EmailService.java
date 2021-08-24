package com.asyncworking.services;

import com.asyncworking.models.EmailSend;
import com.asyncworking.models.EmailType;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
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

    private final EmailSendRepository emailSendRepository;

    @Value("${cloud.aws.sqs.outgoing-queue.url}")
    private String endPoint;

    public void sendMessageToSQS(UserEntity userEntity, String verifyLink, EmailType templateType) {
        Map<String, String> message = new HashMap<>();
        message.put("email", userEntity.getEmail());
        message.put("userName", userEntity.getName());
        message.put("verificationLink", verifyLink);
        message.put("templateType", templateType.toString());
        message.put("templateS3Bucket", "aw-email-template-jh");
        message.put("templateS3Key", "verification_email_template.txt");

        String messageStr = "";
        try {
            messageStr = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        queueMessagingTemplate.send(endPoint, MessageBuilder.withPayload(messageStr).build());
    }

    @Transactional
    public Long createEmailSendingRecord(EmailType templateType, String email, @Valid UserEntity userEntity) {
        EmailSend savedEmailSend = emailSendRepository.save(emailSendBuilder(templateType, email, userEntity));
        log.info("record email send log " + savedEmailSend.getId());
        return savedEmailSend.getId();
    }

    private EmailSend emailSendBuilder(EmailType templateType, String email, @Valid UserEntity userEntity) {
        return EmailSend.builder()
                .userEntity(userEntity)
                .emailType(templateType)
                .receiver(email)
                .sendStatus(Boolean.FALSE)
                .sendTime(OffsetDateTime.now(UTC))
                .build();
    }
}
