package com.asyncworking.controllers;

import com.asyncworking.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class EmailControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailController emailController;

    @Test
    public void successfullyPassParameterWhenSqsListenerGetInfo() {
        String fakeEmail = "a@gmail.com";
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        emailController.loadMessagesFromQueue(fakeEmail);

        Mockito.verify(userService).updateEmailSent(captor.capture());

        Assertions.assertEquals(captor.getValue(), fakeEmail);
    }
}
