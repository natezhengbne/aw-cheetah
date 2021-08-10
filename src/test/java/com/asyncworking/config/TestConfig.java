package com.asyncworking.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.awspring.cloud.messaging.listener.QueueMessageHandler;
import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;
import org.mockito.Mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

@TestConfiguration
public class TestConfig {
    @MockBean
    private SimpleMessageListenerContainer simpleMessageListenerContainer;

    @SpyBean
    private AmazonSQSConfig amazonSQSConfig;

    @SpyBean
    private QueueMessagingTemplate queueMessagingTemplate;
}
