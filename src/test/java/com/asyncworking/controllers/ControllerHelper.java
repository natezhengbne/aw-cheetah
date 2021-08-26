package com.asyncworking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ControllerHelper {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;
    protected ControllerExceptionHandler controllerExceptionHandler;

    @BeforeEach
    protected void setUp() {
        objectMapper = new ObjectMapper();
        controllerExceptionHandler = new ControllerExceptionHandler();
    }

}
