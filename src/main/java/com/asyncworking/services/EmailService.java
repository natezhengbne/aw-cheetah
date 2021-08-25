package com.asyncworking.services;

import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.constants.EmailType;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.utility.mapper.EmailSendRecordMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
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
    @Value("${cloud.aws.S3.templateS3Bucket}")
    private String templateS3Bucket;

    @Value("${cloud.aws.S3.templateS3Key}")
    private String templateS3Key;

    private final QueueMessagingTemplate queueMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final EmailSendRecordMapper emailSendRecordMapper;

    private final EmailSendRepository emailSendRepository;

    @Value("${cloud.aws.sqs.outgoing-queue.url}")
    private String endPoint;

    public void sendMessageToSQS(UserEntity userEntity, String verifyLink, EmailType templateType, String receiverEmail) {
        Map<String, String> message = new HashMap<>();
        message.put("email", receiverEmail);
        message.put("userName", userEntity.getName());
        message.put("verificationLink", verifyLink);
        message.put("templateType", templateType.toString());
        message.put("templateS3Bucket", templateS3Bucket);
        message.put("templateS3Key", templateS3Key);

        String messageStr = "";
        try {
            messageStr = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        queueMessagingTemplate.send(endPoint, MessageBuilder.withPayload(messageStr).build());
    }

    @Transactional
    public EmailSendRecord saveEmailSendingRecord(@Valid UserEntity userEntity, EmailType templateType, String receiverEmail) {
        return emailSendRepository.save(
                emailSendRecordMapper.toEmailSendRecord(userEntity, templateType, receiverEmail)
        );
    }
}
