package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoBoardDto;
import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.exceptions.TodoBoardNotFoundException;
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
    public void todoBoardCreateSuccess() throws Exception {
        TodoBoardDto todoBoardDto = TodoBoardDto.builder()
                .projectId(1L)
                .build();
        when(todoService.createTodoBoard(todoBoardDto))
                .thenReturn(1L);
        mockMvc.perform(post("/project/todoBoard")
                .content(objectMapper.writeValueAsString(todoBoardDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void todoListCreateSuccess() throws Exception {
        TodoListDto todoListDto = TodoListDto.builder()
                .companyId(1L)
                .projectId(3L)
                .todoBoardId(3L)
                .todoListTitle("test_todo_list")
                .build();
        when(todoService.createTodoList(todoListDto))
                .thenReturn(1L);
        mockMvc.perform(post("/project/todoBoard/todoList")
                .content(objectMapper.writeValueAsString(todoListDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void todoListCreateFailWhenTitleIsNull() throws Exception {
        TodoListDto todoListDto = TodoListDto.builder()
                .companyId(1L)
                .projectId(2L)
                .todoBoardId(2L)
                .build();
        when(todoService.createTodoList(todoListDto))
                .thenThrow(new TodoBoardNotFoundException(""));
        mockMvc.perform(post("/project/todoBoard/todoList")
                .content(objectMapper.writeValueAsString(todoListDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void returnTodoListDtoLists() throws Exception {
        when(todoService.findTodoListsByProjectId(1L))
                .thenReturn(new ArrayList<>());
        mockMvc.perform(get("/project/todoBoard/todoLists/1"))
                .andExpect(status().isOk());
    }

}

