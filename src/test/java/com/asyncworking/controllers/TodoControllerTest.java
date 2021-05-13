package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.services.TodoService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    TodoService todoService;


    @Test
    public void todoListCreateSuccess() throws Exception {
        TodoListDto todoListDto = TodoListDto.builder()
                .id(1L)
                .projectId(1L)
                .todoListTitle("test_todo_list")
                .build();
        when(todoService.createTodoList(todoListDto))
                .thenReturn(1L);
        mockMvc.perform(post("/todolist")
                .content(objectMapper.writeValueAsString(todoListDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void todoListCreateFailWhenTitleIsNull() throws Exception {
        TodoListDto todoListDto = TodoListDto.builder()
                .projectId(2L)
                .build();
        mockMvc.perform(post("/todolist")
                .content(objectMapper.writeValueAsString(todoListDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void returnTodoListDtoLists() throws Exception {
        when(todoService.findRequiredNumberTodoListsByProjectId(1L, 0))
                .thenReturn(new ArrayList<>());
        mockMvc.perform(get("/projects/1/todolists")
                .param("quantity", "0"))
                .andExpect(status().isOk());
    }

    @Test
    public void successfullyFindTodoListByTodoListId() throws Exception {
        TodoListDto mockTodoListDto = TodoListDto.builder()
                .projectId(1L)
                .todoListTitle("ha")
                .build();
        when(todoService.findTodoListById(1L))
                .thenReturn(mockTodoListDto);
        mockMvc.perform(get("/projects/todolists/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void throwNotFoundTodoListExceptionWhenTodoListIdIsNotExist() throws Exception {
        when(todoService.findTodoListById(2L))
                .thenThrow(new TodoListNotFoundException(""));
        mockMvc.perform(get("/projects/todolists/2"))
                .andExpect(status().isNotFound());
    }

}

