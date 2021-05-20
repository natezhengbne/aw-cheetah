package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.TodoListRepository;
import com.asyncworking.utility.mapper.TodoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class TodoServiceTest {

    @Mock
    private TodoListRepository todoListRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TodoItemRepository todoItemRepository;

    private TodoService todoService;

    @Autowired
    private TodoMapper todoMapper;

    private TodoListDto mockTodoListDto;

    private Project project;

    private TodoList todoList;

    private TodoItem todoItem1;

    private TodoItem todoItem2;

    @BeforeEach
    public void setup() {
        todoService = new TodoService(
                todoListRepository,
                todoItemRepository,
                projectRepository,
                todoMapper);

        mockTodoListDto = TodoListDto.builder()
                .projectId(1L)
                .todoListTitle("FirstTodoList")
                .build();

        project = Project.builder()
                .name("AWProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        todoList = TodoList.builder()
                .id(1L)
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("todolist title")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        todoItem1 = buildTodoItem(todoList, "test1", "des1");
        todoItem2 = buildTodoItem(todoList, "test2", "des2");

    }

    @Test
    @Transactional
    public void createTodoListSuccess() {
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        ArgumentCaptor<TodoList> todoListCaptor = ArgumentCaptor.forClass(TodoList.class);
        todoService.createTodoList(mockTodoListDto);
        verify(todoListRepository).save(todoListCaptor.capture());
        assertEquals(project, todoListCaptor.getValue().getProject());
    }

    @Test
    public void throwProjectNotFoundExceptionWhenProjectIdIsNotExist() {
        when(projectRepository.findById(2L))
                .thenThrow(new ProjectNotFoundException("Cannot find project by id:2"));
        assertThrows(ProjectNotFoundException.class, () -> todoService.createTodoList(mockTodoListDto));
    }

    @Test
    public void returnRequiredQuantityOfTodoListDto() {
        Integer quantity = 3;
        List<TodoList> todoLists = new ArrayList<>();
        todoLists.add(TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("first")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build());
        todoLists.add(TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("second")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build());
        todoLists.add(TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("third")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build());
        when(todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(any(), any()))
                .thenReturn(todoLists);
        List<TodoListDto> todoListDtos = todoService.findRequiredNumberTodoListsByProjectId(project.getId(), quantity);
        assertEquals(quantity, todoListDtos.size());
    }

    @Test
    public void returnTodoListDtoWhenPassCorrectId() {
        TodoListDto returnedMockTodoListDto = TodoListDto.builder()
                .todoListTitle("FirstTodoList")
                .build();

        TodoList mockTodoList = TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("FirstTodoList")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        when(todoListRepository.findById(any())).thenReturn(Optional.of(mockTodoList));
        assertEquals(returnedMockTodoListDto.getTodoListTitle(), todoService.findTodoListById(mockTodoList.getId()).getTodoListTitle());
    }

    @Test
    public void throwTodoListNotFoundExceptionWhenIdIsNotExist() {
        when(todoListRepository.findById(2L))
                .thenThrow(new TodoListNotFoundException("Cannot find todoList by id: 2"));
        assertThrows(TodoListNotFoundException.class, () -> todoService.findTodoListById(2L));
    }

    @Test
    public void givenTodoListId_shouldReturnListOfTodoItems_thenOk() {
        List<TodoItem> todoItems = new ArrayList<>();
        todoItems.add(buildTodoItem(todoList, "test1", "des1"));
        todoItems.add(buildTodoItem(todoList, "test2", "des2"));
        when(todoItemRepository.findTodoItemListByTodoListIdOrderByCreatedTime(any()))
                .thenReturn(todoItems);

        List<TodoItemGetDto> todoItemGetDtos = todoService.findTodoItemsByTodoListIdOrderByCreatedTime(todoList.getId());
        assertEquals(2, todoItemGetDtos.size());
        assertEquals("test1", todoItemGetDtos.get(0).getNotes());
        assertEquals("des1", todoItemGetDtos.get(0).getDescription());
        assertEquals("test2", todoItemGetDtos.get(1).getNotes());
        assertEquals("des2", todoItemGetDtos.get(1).getDescription());
    }

    private TodoItem buildTodoItem(TodoList todoList, String notes, String description) {
        return TodoItem.builder()
                .todoList(todoList)
                .companyId(todoList.getCompanyId())
                .projectId(todoList.getProject().getId())
                .notes(notes)
                .description(description)
                .completed(Boolean.FALSE)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }
}
