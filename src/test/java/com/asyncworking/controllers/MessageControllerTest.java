package com.asyncworking.controllers;


import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.Category;
import com.asyncworking.services.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    MessageService messageService;

    @Test
    public void createMessageSuccess() throws Exception {
        MessagePostDto messagePostDto = MessagePostDto.builder()
                .companyId(1L)
                .projectId(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .docURL("https:www.adc.com")
                .build();

        MessageGetDto mockMessageGetDto = MessageGetDto.builder()
                .id(2L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("FL")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(OffsetDateTime.now(UTC))
                .docURL("https:www.adc.com")
                .build();

        when(messageService.createMessage(messagePostDto)).thenReturn(mockMessageGetDto);
        mockMvc.perform(post("/messages")
                .content(objectMapper.writeValueAsString(messagePostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void getMessageListSuccess() throws Exception {
        List<MessageGetDto> messageGetDtoList = new ArrayList<>();
        messageGetDtoList.add(MessageGetDto.builder()
                .id(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("FL")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(OffsetDateTime.now(UTC))
                .docURL("https:www.adc.com")
                .build());


        messageGetDtoList.add(MessageGetDto.builder()
                .id(1L)
                .messageTitle("second message title")
                .content("second message")
                .posterUserId(1L)
                .posterUser("FL")
                .category(Category.ANNOUNCEMENT)
                .docURL("https:www.adc.com")
                .postTime(OffsetDateTime.now(UTC))
                .build());

        when(messageService.findMessageListByProjectId(1L)).thenReturn(messageGetDtoList);
        mockMvc.perform(get("/projects/4/messageLists"))
                .andExpect(status().isOk());
    }

    @Test
    public void createMessageFailWhenNotNullVariableAreNull() throws Exception {
        MessagePostDto messagePostDto = MessagePostDto.builder()
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .build();

        mockMvc.perform(post("/messages")
                .content(objectMapper.writeValueAsString(messagePostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void throwNotFoundProjectExceptionWhenThisProjectNotExist() throws Exception {
        MessagePostDto messagePostDto = MessagePostDto.builder()
                .companyId(1L)
                .projectId(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .docURL("https:www.adc.com")
                .build();
        when(messageService.createMessage(messagePostDto))
                .thenThrow(new ProjectNotFoundException("this project not exist"));
        mockMvc.perform(post("/messages")
                .content(objectMapper.writeValueAsString(messagePostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
