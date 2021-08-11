package com.asyncworking.controllers;

import com.asyncworking.services.UserService;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;


@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "sqs.enable",
    havingValue = "true",
    matchIfMissing = true)
public class EmailResultListener {

    private final UserService userService;

    @SqsListener(value = "${cloud.aws.sqs.incoming-queue.name}")
    public void loadMessagesFromQueue(String email) {
        log.info("from sqs: " + email);
        userService.updateEmailSent(email);
    }
}
