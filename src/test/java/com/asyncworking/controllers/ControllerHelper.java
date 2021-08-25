package com.asyncworking.controllers;

import com.asyncworking.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class ControllerHelper {

    protected ObjectMapper objectMapper;

    protected ControllerExceptionHandler controllerExceptionHandler;

    protected CompanyController companyController;

    @Mock
    protected CompanyService companyService;

    protected MessageController messageController;

    @Mock
    protected MessageService messageService;

    protected ProjectController projectController;

    @Mock
    protected ProjectService projectService;

    protected TodoController todoController;

    @Mock
    protected TodoService todoService;

    protected UserController userController;

    @Mock
    protected UserService userService;

    protected MockMvc mockMvc;

    @BeforeEach
    protected void setUp() {
        objectMapper = new ObjectMapper();
        controllerExceptionHandler = new ControllerExceptionHandler();
        companyController = new CompanyController(companyService);
        messageController = new MessageController(messageService);
        projectController = new ProjectController(projectService);
        todoController = new TodoController(todoService);
        userController = new UserController(userService);

        mockMvc = MockMvcBuilders.standaloneSetup(
                controllerExceptionHandler,
                companyController,
                messageController,
                projectController,
                todoController,
                userController).build();

    }
}
