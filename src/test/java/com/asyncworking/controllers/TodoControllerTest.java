package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.dtos.todoitem.TodoItemPutDto;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.models.TodoItem;
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

import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TodoControllerTest {

    @MockBean
    TodoService todoService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void todoListCreateSuccess() throws Exception {
        TodoListDto todoListDto = TodoListDto.builder()
                .id(1L)
                .projectId(1L)
                .todoListTitle("test_todo_list")
                .build();
        when(todoService.createTodoList(todoListDto))
                .thenReturn(1L);
        mockMvc.perform(post("companies/1/projects/1/todolists")
                .content(objectMapper.writeValueAsString(todoListDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void todoListCreateFailWhenTitleIsNull() throws Exception {
        TodoListDto todoListDto = TodoListDto.builder()
                .projectId(2L)
                .build();
        mockMvc.perform(post("companies/1/projects/1/todolists")
                .content(objectMapper.writeValueAsString(todoListDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void returnTodoListDtoLists() throws Exception {
        when(todoService.findRequiredNumberTodoListsByCompanyIdAndProjectId(1L, 1L, 0))
                .thenReturn(new ArrayList<>());
        mockMvc.perform(get("companies/1/projects/1/todolists")
                .param("quantity", "0"))
                .andExpect(status().isOk());
    }

    @Test
    public void successfullyFindTodoListByTodoListId() throws Exception {
        TodoListDto mockTodoListDto = TodoListDto.builder()
                .projectId(1L)
                .todoListTitle("ha")
                .build();
        when(todoService.fetchSingleTodoList(1L, 1L, 1L))
                .thenReturn(mockTodoListDto);
        mockMvc.perform(get("companies/1/projects/1/todolists/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void throwNotFoundTodoListExceptionWhenTodoListIdIsNotExist() throws Exception {
        when(todoService.fetchSingleTodoList(1L, 1L, 2L))
                .thenThrow(new TodoListNotFoundException(""));
        mockMvc.perform(get("companies/1/projects/1/todolists/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void todoItemCreateSuccess() throws Exception {
        TodoItemPostDto todoItemPostDto = TodoItemPostDto.builder()
                .todolistId(1L)
                .notes("test1")
                .description("test des1")
                .createdUserId(1L)
                .dueDate(OffsetDateTime.now())
                .build();
        when(todoService.createTodoItem(todoItemPostDto))
                .thenReturn(1L);
        mockMvc.perform(post("companies/1/projects/1/todolists/1/todoitems")
                .content(objectMapper.writeValueAsString(todoItemPostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfGetTodoItemPageInfoSuccessful() throws Exception {
        TodoItemPageDto todoItemPageDto = TodoItemPageDto.builder().build();
        when(todoService.fetchTodoItemPageInfoByIds(1L))
                .thenReturn(todoItemPageDto);
        mockMvc.perform(
                MockMvcRequestBuilders.get("companies/1/projects/1/todoitems/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOppositeStatusAfterChangeTodoItemCompleted() throws Exception {
        TodoItem todoItem = TodoItem.builder()
                .id(1L)
                .projectId(1L)
                .completed(true)
                .build();
        when(todoService.changeTodoItemCompleted(todoItem.getId()))
                .thenReturn(!todoItem.getCompleted());
        mockMvc.perform(put("companies/1/projects/1/todoitems/1/completed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void updateTodoItemSuccess() throws Exception {
        TodoItemPutDto todoItemPut = TodoItemPutDto.builder()
                .description("title")
                .notes("notes/n")
                .originNotes("<div>notes</div>")
                .build();
        mockMvc.perform(put("companies/1/projects/1/todoitems/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoItemPut)))
                .andExpect(status().isOk());

    }
}

