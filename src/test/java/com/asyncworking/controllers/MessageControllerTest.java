package com.asyncworking.controllers;


import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
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
                .projectId(2L)
                .projectUserId(3L)
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .build();

        when(messageService.createMessage(messagePostDto)).thenReturn(1L);
        mockMvc.perform(post("/message")
                .content(objectMapper.writeValueAsString(messagePostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void getMessageListSuccess() throws Exception {
        List<MessageGetDto> messageGetDtoList = new ArrayList<>();
        messageGetDtoList.add(MessageGetDto.builder()
            .id(1L)
            .companyId(3L)
            .projectId(4L)
            .messageTitle("first message title")
            .content("first message")
            .category(Category.ANNOUNCEMENT)
            .docURL("https:www.adc.com")
            .build());

        messageGetDtoList.add(MessageGetDto.builder()
                .id(2L)
                .companyId(3L)
                .projectId(4L)
                .messageTitle("second message title")
                .content("second message")
                .category(Category.ANNOUNCEMENT)
                .docURL("https:www.adc.com")
                .build());

        when(messageService.findMessageListByProjectId())
    }


}
