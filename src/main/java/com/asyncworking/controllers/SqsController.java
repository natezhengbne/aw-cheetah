package com.asyncworking.controllers;


import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SqsController {
    @SqsListener(value = "receive_queue")
    public void loadMessagesFromQueue(String message) {
        log.info("from sqs: " + message);
    }
}
