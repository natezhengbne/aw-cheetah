package com.asyncworking.controllers;

import com.asyncworking.services.UserService;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class EmailController {

    private final UserService userService;

    @SqsListener(value = "receive_queue")
    public void loadMessagesFromQueue(String email) {
        log.info("from sqs: " + email);
        userService.updateEmailSent(email);
    }
}
