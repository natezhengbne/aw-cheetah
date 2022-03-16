package com.asyncworking.aws;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.EmailMessageDto;
import com.asyncworking.exceptions.EmailSendFailException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "sqs.enable",
        havingValue = "true",
        matchIfMissing = true)
public class AmazonSQSSender {

    private final QueueMessagingTemplate queueMessagingTemplate;

    private final ObjectMapper objectMapper;

    private final Map<EmailType, String> emailType = new HashMap<>();

    @Setter
    @Value("${cloud.aws.sqs.outgoingqueue.url}")
    private String endPoint;

    @Setter
    @Value("${cloud.aws.S3.templateS3Bucket}")
    private String s3Bucket;

    @Setter
    @Value("${cloud.aws.S3.templateS3Key}")
    private String s3Key;

    @Setter
    @Value("${cloud.aws.S3.templateResetPasswordS3Key}")
    private String s3resetPasswordTemplateKey;

    @Setter
    @Value("${cloud.aws.S3.templateCompanyInvitationS3Key}")
    private String s3CompanyInvitationTemplateKey;

    @PostConstruct
    private void initMap() {
        emailType.put(EmailType.Verification, s3Key);
        emailType.put(EmailType.ForgetPassword, s3resetPasswordTemplateKey);
        emailType.put(EmailType.CompanyInvitation, s3CompanyInvitationTemplateKey);
    }

    public void sendEmailMessage(EmailMessageDto messageDto) {
        try {
            messageDto.setTemplateS3Bucket(s3Bucket);
            messageDto.setTemplateS3Key(emailType.get(EmailType.valueOf(messageDto.getTemplateType())));
            queueMessagingTemplate.send(
                    endPoint,
                    MessageBuilder.withPayload(objectMapper.writeValueAsString(messageDto)).build()
            );
        } catch (Exception e) {
            throw new EmailSendFailException(e);
        }
    }
}