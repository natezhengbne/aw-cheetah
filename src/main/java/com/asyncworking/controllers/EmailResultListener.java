package com.asyncworking.controllers;

import com.asyncworking.models.SqsResponse;
import com.asyncworking.services.EmailService;
import com.asyncworking.services.UserService;
import com.google.gson.Gson;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "sqs.enable",
        havingValue = "true",
        matchIfMissing = true)
public class EmailResultListener {

    private final UserService userService;

    private final EmailService emailService;

    @SqsListener(value = "${cloud.aws.sqs.incomingqueue.name}")
    public void loadMessagesFromQueue(String message) {
        log.info("Message" + message);
        log.info("Respond Time From SQS: " + OffsetDateTime.now());
        log.info("Respond SES ID: " + parseStringMessage(message).getSesResultId());
        SqsResponse sqsResponse = parseStringMessage(message);
        Long emailRecordId = sqsResponse.getEmailRecordId();
        log.info("SQS Listener - Email Record ID: {}", emailRecordId);
        if (emailRecordId == null) {
            userService.updateEmailSent(sqsResponse.getEmail());
        } else {
            emailService.updateEmailRecordSendStatus(emailRecordId);
        }
    }

    private SqsResponse parseStringMessage(String message) {
        return new Gson().fromJson(message, SqsResponse.class);
    }
}
