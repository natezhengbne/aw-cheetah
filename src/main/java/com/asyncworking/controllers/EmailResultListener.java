package com.asyncworking.controllers;

import com.asyncworking.models.SqsResponse;
import com.asyncworking.services.UserService;
import com.google.gson.Gson;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "sqs.enable",
    havingValue = "true",
    matchIfMissing = true)
public class EmailResultListener {

    private final UserService userService;

    @SqsListener(value = "${cloud.aws.sqs.incoming-queue.name}")
    public void loadMessagesFromQueue(String message){
        log.info("From sqs: " + message);
        userService.updateEmailSent(parseStringMessage(message).getEmail());
        log.info("DateTime: " + parseStringMessage(message).getTimeSent());
    }

    private SqsResponse parseStringMessage(String message){
        return new Gson().fromJson(message, SqsResponse.class);
    }
}
