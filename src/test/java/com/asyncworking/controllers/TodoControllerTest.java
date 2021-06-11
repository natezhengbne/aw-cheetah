package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.services.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
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
        mockMvc.perform(post("/projects/1/todolists")
                .content(objectMapper.writeValueAsString(todoListDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void todoListCreateFailWhenTitleIsNull() throws Exception {
        TodoListDto todoListDto = TodoListDto.builder()
                .projectId(2L)
                .build();
        mockMvc.perform(post("/projects/1/todolists")
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
        mockMvc.perform(get("/projects/1/todolists/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void throwNotFoundTodoListExceptionWhenTodoListIdIsNotExist() throws Exception {
        when(todoService.findTodoListById(2L))
                .thenThrow(new TodoListNotFoundException(""));
        mockMvc.perform(get("/projects/1/todolists/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void todoItemCreateSuccess() throws Exception {
        TodoItemPostDto todoItemPostDto = TodoItemPostDto.builder()
                .todolistId(1L)
                .notes("test1")
                .description("test des1")
                .dueDate(LocalDate.of(2017, 10, 21))
                .build();
        when(todoService.createTodoItem(todoItemPostDto))
                .thenReturn(1L);
        mockMvc.perform(post("/projects/1/todolists/1/todoitems")
                .content(objectMapper.writeValueAsString(todoItemPostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfGetTodoItemPageInfoSuccessful() throws Exception {
        TodoItemPageDto todoItemPageDto = TodoItemPageDto.builder().build();
        when(todoService.fetchTodoItemPageInfoByIds(1L, 1L))
                .thenReturn(todoItemPageDto);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/projects/1/todoitems/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }
}

