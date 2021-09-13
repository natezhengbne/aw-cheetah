package com.asyncworking.services;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.EmailMessageDto;
import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmailSendRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.EmailMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final EmailMapper emailMapper;

    private final EmailSendRepository emailSendRepository;
    private final UserRepository userRepository;

    @Value("${cloud.aws.sqs.outgoingqueue.url}")
    private String endPoint;

    @Value("${cloud.aws.S3.templateS3Bucket}")
    private String S3Bucket;

    @Value("${cloud.aws.S3.templateS3Key}")
    private String S3Key;

    public void sendMessageToSQS(UserEntity userEntity, String verificationLink, EmailType templateType, String receiverEmail) {
        try {
            queueMessagingTemplate.send(endPoint, MessageBuilder.withPayload(
                    objectMapper.writeValueAsString(
                            toEmailMessageDto(userEntity, verificationLink, templateType, receiverEmail))
            ).build());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public EmailSendRecord saveEmailSendingRecord(UserEntity userEntity, EmailType templateType, String receiverEmail) {
        return emailSendRepository.save(
                emailMapper.toEmailSendRecord(userEntity, templateType, receiverEmail)
        );
    }

    private EmailMessageDto toEmailMessageDto(UserEntity userEntity, String verificationLink,
                                              EmailType templateType, String receiverEmail) {
        return EmailMessageDto.builder()
                .userName(userEntity.getName())
                .verificationLink(verificationLink)
                .templateType(templateType.toString())
                .email(receiverEmail)
                .templateS3Bucket(S3Bucket)
                .templateS3Key(S3Key)
                .build();
    }
}
