package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.services.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import static java.time.ZoneOffset.UTC;
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

    @Mock
    private Project mockProject;


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

//    @Test
//    public void createTodoItemSuccess() throws Exception {
//        TodoListDto todoListDto = TodoListDto.builder()
//                .id(1L)
//                .projectId(1L)
//                .todoListTitle("test_todo_list")
//                .build();
//        when(todoService.createTodoList(todoListDto))
//                .thenReturn(1L);
//        TodoItemPostDto todoItemPostDto = TodoItemPostDto.builder()
//                .todoListId(1L)
//                .content("todo item content test")
//                .build();
//        when(todoService.createTodoItem(todoItemPostDto))
//                .thenReturn(1L);
//        mockMvc.perform(post("/todolist/1/todoitem")
//                .content(objectMapper.writeValueAsString(todoItemPostDto))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

}

