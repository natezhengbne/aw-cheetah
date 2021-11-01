package com.asyncworking.controllers;

import com.asyncworking.dtos.MessageCategoryGetDto;
import com.asyncworking.dtos.MessageCategoryPostDto;
import com.asyncworking.models.MessageCategory;
import com.asyncworking.repositories.MessageCategoryRepository;
import com.asyncworking.services.MessageCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MessageCategoryControllerTest extends ControllerHelper {
    @Mock
    private MessageCategoryService messageCategoryService;
    private MessageCategoryController messageCategoryController;

    @Mock
    private MessageCategoryRepository messageCategoryRepository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        messageCategoryController = new MessageCategoryController(messageCategoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(
                controllerExceptionHandler,
                messageCategoryController
        ).build();
    }

    @Test
    public void createMessageCategorySuccess() throws Exception {
        MessageCategoryPostDto messageCategoryPostDto = MessageCategoryPostDto.builder()
                .projectId(1L)
                .categoryName("first test")
                .emoji("\uD83D\uDE00")
                .build();

        MessageCategoryGetDto mockMessageCategoryGetDto = MessageCategoryGetDto.builder()
                .messageCategoryId(1L)
                .projectId(1L)
                .categoryName("first test")
                .emoji("\uD83D\uDE00")
                .build();

        when(messageCategoryService.createMessageCategory(messageCategoryPostDto)).thenReturn(mockMessageCategoryGetDto);
        mockMvc.perform(post("/companies/1/projects/1/creation")
                .content(objectMapper.writeValueAsString(messageCategoryPostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getMessageCategoryListSuccess() throws Exception {
        List<MessageCategoryGetDto> messageCategoryGetDtoList = new ArrayList<>();
        messageCategoryGetDtoList.add(MessageCategoryGetDto.builder()
                .messageCategoryId(1L)
                .projectId(1L)
                .categoryName("first test")
                .emoji("\uD83D\uDE00")
                .build());

        messageCategoryGetDtoList.add(MessageCategoryGetDto.builder()
                .messageCategoryId(1L)
                .projectId(1L)
                .categoryName("second test")
                .emoji("\uD83D\uDE00")
                .build());

        when(messageCategoryService.findMessageCategoryListByCompanyIdAndProjectId(1L, 1L)).thenReturn(messageCategoryGetDtoList);
        mockMvc.perform(get("/companies/1/projects/1/message-categories"))
                .andExpect(status().isOk());

    }

    @Test
    public void editMessageCategorySuccess() throws Exception {
        MessageCategoryGetDto mockMessageCategoryGetDto = MessageCategoryGetDto.builder()
                .messageCategoryId(1L)
                .projectId(1L)
                .categoryName("first test")
                .emoji("\uD83D\uDE00")
                .build();

        mockMvc.perform(put("/companies/1/projects/1/categoryId/1/edition")
                .content(objectMapper.writeValueAsString(mockMessageCategoryGetDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
