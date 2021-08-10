package com.asyncworking.controllers;

import com.asyncworking.config.TestConfig;
import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.MessageNotFoundException;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.services.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
@Import(TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class MessageControllerTest {

    @MockBean
    MessageService messageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createMessageSuccess() throws Exception {
        MessagePostDto messagePostDto = MessagePostDto.builder()
                .companyId(1L)
                .projectId(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .content("first message content")
                .messageCategoryId(1L)
                .originNotes("<p>list rich editor</p>")
                .docURL("https:www.adc.com")
                .build();

        MessageGetDto mockMessageGetDto = MessageGetDto.builder()
                .id(2L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("FL")
                .content("first message content")
                .messageCategoryId(1L)
                .originNotes("<p>list rich editor</p>")
                .postTime(OffsetDateTime.now(UTC))
                .docURL("https:www.adc.com")
                .build();

        when(messageService.createMessage(messagePostDto)).thenReturn(mockMessageGetDto);
        mockMvc.perform(post("/projects/1/messages")
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
                .messageCategoryId(1L)
                .originNotes("<p>list rich editor</p>")
                .postTime(OffsetDateTime.now(UTC))
                .docURL("https:www.adc.com")
                .build());

        messageGetDtoList.add(MessageGetDto.builder()
                .id(1L)
                .messageTitle("second message title")
                .content("second message")
                .originNotes("<p>list rich editor</p>")
                .posterUserId(1L)
                .posterUser("FL")
                .messageCategoryId(1L)
                .docURL("https:www.adc.com")
                .postTime(OffsetDateTime.now(UTC))
                .build());

        when(messageService.findMessageListByProjectId(1L)).thenReturn(messageGetDtoList);
        mockMvc.perform(get("/projects/1/messages"))
                .andExpect(status().isOk());
    }

    @Test
    public void createMessageFailWhenNotNullVariableAreNull() throws Exception {
        MessagePostDto messagePostDto = MessagePostDto.builder()
                .messageCategoryId(1L)
                .originNotes("<p>list rich editor</p>")
                .build();

        mockMvc.perform(post("/projects/1/messages")
                .content(objectMapper.writeValueAsString(messagePostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void throwProjectNotFoundExceptionWhenThisProjectNotExist() throws Exception {
        MessagePostDto messagePostDto = MessagePostDto.builder()
                .companyId(1L)
                .projectId(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .content("first message content")
                .messageCategoryId(1L)
                .originNotes("<p>list rich editor</p>")
                .docURL("https:www.adc.com")
                .build();
        when(messageService.createMessage(messagePostDto))
                .thenThrow(new ProjectNotFoundException("this project not exist"));
        mockMvc.perform(post("/projects/1/messages")
                .content(objectMapper.writeValueAsString(messagePostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getMessagesSuccess() throws Exception {
        MessageGetDto messageGetDto = MessageGetDto.builder()
                .id(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("FL")
                .content("first message content")
                .messageCategoryId(1L)
                .originNotes("<p>list rich editor</p>")
                .postTime(OffsetDateTime.now(UTC))
                .docURL("https:www.adc.com")
                .build();

        when(messageService.findMessageById(1L)).thenReturn(messageGetDto);
        mockMvc.perform(get("/projects/1/messages/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void throwMessageNotFoundExceptionWhenMessageIdNotExist() throws Exception {
        when(messageService.findMessageById(1L)).thenThrow(new MessageNotFoundException("this message not exist"));
        mockMvc.perform(get("/projects/1/messages/1"))
                .andExpect(status().isNotFound());
    }
}
