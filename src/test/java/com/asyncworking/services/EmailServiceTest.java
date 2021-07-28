package com.asyncworking.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.springframework.messaging.Message;
import com.asyncworking.models.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceTest {

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void sendCorrectMessageWithPayloadWhenPassParameters() throws Exception {
        UserEntity mockUser = UserEntity.builder()
                .name("aa")
                .email("a@gmail.com")
                .build();
        String verifyLink = "http://sdfsff";
        Map<String, String> message = new HashMap<>();
        message.put("userName", mockUser.getName());
        message.put("email", mockUser.getEmail());
        message.put("verificationLink", verifyLink);
        ArgumentCaptor<String> endPointCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Message<String>> messageCaptor = ArgumentCaptor.forClass(Message.class);

        Message<String> testMessage = MessageBuilder.withPayload(objectMapper.writeValueAsString(message)).build();

        emailService.sendMessageToSQS(mockUser, verifyLink);

        Mockito.verify(queueMessagingTemplate).send(endPointCaptor.capture(), messageCaptor.capture());

        Assertions.assertEquals(testMessage.getPayload(), messageCaptor.getValue().getPayload());
    }


}
