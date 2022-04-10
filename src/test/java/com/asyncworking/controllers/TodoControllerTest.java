package com.asyncworking.controllers;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.*;
import com.asyncworking.dtos.todolist.MoveTodoListDto;
import com.asyncworking.dtos.todolist.MovedItemsListDto;
import com.asyncworking.dtos.todolist.TodoListPutDto;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.services.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TodoControllerTest extends ControllerHelper {
    @Mock
    private TodoService todoService;
    private TodoController todoController;

    @BeforeEach
    public void setUp() {
        super.setUp();
        todoController = new TodoController(todoService);
        mockMvc = MockMvcBuilders.standaloneSetup(
                controllerExceptionHandler,
                todoController
        ).build();
    }

    @Test
    public void todoListCreateSuccess() throws Exception {
        TodoListDto todoListDto = TodoListDto.builder()
                .todoListTitle("test_todo_list")
                .build();
        when(todoService.createTodoList(1L, 1L, todoListDto))
                .thenReturn(1L);
        mockMvc.perform(post("/companies/1/projects/1/todolists")
                .content(objectMapper.writeValueAsString(todoListDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void todoListCreateFailWhenTitleIsNull() throws Exception {
        TodoListDto todoListDto = TodoListDto.builder()
                .projectId(2L)
                .build();
        mockMvc.perform(post("/companies/1/projects/1/todolists")
                .content(objectMapper.writeValueAsString(todoListDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void returnTodoListDtoLists() throws Exception {
        when(todoService.findRequiredNumberTodoListsByCompanyIdAndProjectId(1L, 1L, 0))
                .thenReturn(new ArrayList<>());
        mockMvc.perform(get("/companies/1/projects/1/todolists")
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
        mockMvc.perform(get("/companies/1/projects/1/todolists/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void throwNotFoundTodoListExceptionWhenTodoListIdIsNotExist() throws Exception {
        when(todoService.fetchSingleTodoList(1L, 1L, 2L))
                .thenThrow(new TodoListNotFoundException(""));
        mockMvc.perform(get("/companies/1/projects/1/todolists/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void todoItemCreateSuccess() throws Exception {
        TodoItemPostDto todoItemPostDto = TodoItemPostDto.builder()
                .todoListId(1L)
                .notes("test1")
                .description("test des1")
                .createdUserId(1L)
                .build();
        when(todoService.createTodoItem(1L, 1L, todoItemPostDto))
                .thenReturn(1L);
        mockMvc.perform(post("/companies/1/projects/1/todolists/1/todoitems")
                .content(objectMapper.writeValueAsString(todoItemPostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfGetTodoItemPageInfoSuccessful() throws Exception {
        TodoItemPageDto todoItemPageDto = TodoItemPageDto.builder().build();
        when(todoService.fetchTodoItemPageInfoByIds(1L, 1L, 1L))
                .thenReturn(todoItemPageDto);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/companies/1/projects/1/todoitems/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnCompletedStatusAfterChangeTodoItemCompleted() throws Exception {
        when(todoService.changeTodoItemCompleted(1L, 1L, 1L, false))
                .thenReturn(false);

        mockMvc.perform(put("/companies/1/projects/1/todoitems/1/completed")
                .param("completedStatus", "false"))
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
        mockMvc.perform(put("/companies/1/projects/1/todoitems/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoItemPut)))
                .andExpect(status().isOk());

    }

    @Test
    public void shouldReturnAssignedPeople() throws Exception {
        AssignedPeopleGetDto assignedPeopleGetDto = AssignedPeopleGetDto.builder()
                .name("fl")
                .id(1L)
                .build();
        AssignedPeopleGetDto assignedPeopleGetDto2 = AssignedPeopleGetDto.builder()
                .name("fll")
                .id(2L)
                .build();

        when(todoService.findAssignedPeople(1L, 1L, 1L))
                .thenReturn(List.of(assignedPeopleGetDto, assignedPeopleGetDto2));

        mockMvc.perform(get("/companies/1/projects/1/todoitems/1/assignees"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfUpdateTodolistTitleSuccessful() throws Exception {
        TodoListDto todolistDto = TodoListDto.builder()
                .todoListTitle("abc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/todolists/1")
                .content(objectMapper.writeValueAsString(todolistDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void throwBadRequestIfUpdateTodolistTitleIsEmpty() throws Exception {
        TodoListDto todolistDto = TodoListDto.builder()
                .todoListTitle("")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/todolists/1")
                        .content(objectMapper.writeValueAsString(todolistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void  shouldReturnOkIfMovedListsIsSuccessful() throws Exception {
        TodoItemMoveDto todoItemMoveDto = TodoItemMoveDto.builder().todoItemId(1L).build();
        List<TodoItemMoveDto> todoItemMoveDtos = new ArrayList<>();
        todoItemMoveDtos.add(todoItemMoveDto);
        TodoListPutDto todoListPutDto = TodoListPutDto.builder()
                                       .id(1L)
                                       .todoItems(todoItemMoveDtos)
                                       .todoListTitle("1")
                                       .build();

        List<TodoListPutDto> todoListPutDtos = new ArrayList<>();
        todoListPutDtos.add(todoListPutDto);

        MoveTodoListDto moveTodoListDto = MoveTodoListDto.builder()
                        .todoLists(todoListPutDtos)
                        .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/todoitems/update-todolists")
                        .content(objectMapper.writeValueAsString(moveTodoListDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void throwBadRequestIfMovedListsIsEmpty() throws Exception {
        List<TodoListPutDto> todoListPutDtos = new ArrayList<>();
        MoveTodoListDto moveTodoListDto = MoveTodoListDto.builder()
                .todoLists(todoListPutDtos)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/todoitems/update-todolists")
                        .content(objectMapper.writeValueAsString(moveTodoListDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void  shouldReturnOkIfReorderTodoItemsIsSuccessful() throws Exception {
        TodoItemMoveDto todoItemMoveDto = TodoItemMoveDto.builder().todoItemId(1L).build();
        List<TodoItemMoveDto> todoItemMoveDtos = new ArrayList<>();
        todoItemMoveDtos.add(todoItemMoveDto);
        TodoListPutDto todoListPutDto = TodoListPutDto.builder()
                .id(1L)
                .todoItems(todoItemMoveDtos)
                .todoListTitle("1")
                .build();
        MovedItemsListDto movedItemsList = MovedItemsListDto.builder().movedItemsList(todoListPutDto).build();
        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/todoitems/update-todoitems")
                        .content(objectMapper.writeValueAsString(movedItemsList))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void throwBadRequestIfReorderTodoItemsIsEmpty() throws Exception {
        TodoListPutDto todoListPutDto = TodoListPutDto.builder()
                .id(1L)
                .todoListTitle("1")
                .build();
        MovedItemsListDto movedItemsList = MovedItemsListDto.builder().movedItemsList(todoListPutDto).build();
        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/todoitems/update-todoitems")
                        .content(objectMapper.writeValueAsString(movedItemsList))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void  shouldReturnOkIfmoveTodoItemsIsSuccessful() throws Exception {
        TodoItemMoveDto todoItemMoveDto = TodoItemMoveDto.builder().todoItemId(1L).build();
        List<TodoItemMoveDto> todoItemMoveDtos = new ArrayList<>();
        todoItemMoveDtos.add(todoItemMoveDto);
        TodoListPutDto todoListPutDto = TodoListPutDto.builder()
                .id(1L)
                .todoItems(todoItemMoveDtos)
                .todoListTitle("1")
                .build();

        List<TodoListPutDto> todoListPutDtos = new ArrayList<>();
        todoListPutDtos.add(todoListPutDto);

        MoveTodoListDto moveTodoListDto = MoveTodoListDto.builder()
                .todoLists(todoListPutDtos)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/todoitems/update-two-todolists")
                        .content(objectMapper.writeValueAsString(moveTodoListDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void throwBadRequestIfmoveTodoItemsIsEmpty() throws Exception {
        List<TodoListPutDto> todoListPutDtos = new ArrayList<>();
        MoveTodoListDto moveTodoListDto = MoveTodoListDto.builder()
                .todoLists(todoListPutDtos)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/todoitems/update-two-todolists")
                        .content(objectMapper.writeValueAsString(moveTodoListDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

}

