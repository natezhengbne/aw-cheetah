package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.TodoListRepository;
import com.asyncworking.utility.mapper.TodoItemMapper;
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

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    private TodoService todoService;

    @Autowired
    private TodoMapper todoMapper;

    @Autowired
    private TodoItemMapper todoItemMapper;

    private TodoListDto mockTodoListDto;

    private Project project;

    private TodoList mockTodoList;

    private TodoItemPostDto mockTodoItemPostDto;



    @BeforeEach
    public void setup() {

        todoService = new TodoService(
                todoListRepository,
                todoItemRepository,
                projectRepository,
                todoMapper,
                todoItemMapper);

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

        mockTodoItemPostDto = TodoItemPostDto.builder()
                .todoListId(1L)
                .notes("todo item mock post dto test")
                .description("todo item post dto description test")
                .build();

        mockTodoList = TodoList.builder()
                .companyId(1L)
                .project(project)
                .todoListTitle("TestTodoList")
                .details("todo list details test")
                .docURL("https://www.entity.com")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
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
    @Transactional
    public void createTodoItemSuccess() {
        when(todoListRepository.findById(1L))
                .thenReturn(Optional.of(mockTodoList));
        ArgumentCaptor<TodoItem> todoItemCaptor = ArgumentCaptor.forClass(TodoItem.class);
        todoService.createTodoItem(mockTodoItemPostDto);
        verify(todoItemRepository).save(todoItemCaptor.capture());
        assertEquals(mockTodoList, todoItemCaptor.getValue().getTodoList());
    }

    @Test
    public void givenTodoListId_shouldReturnListOfTodoItems_thenOk() {
        Project savedProject = projectRepository.save(buildProject("AWProject"));
        TodoList savedTodoList = todoListRepository.save(buildTodoList(savedProject, "first"));
        TodoItem todoItem1 = todoItemRepository.save(buildTodoItem(savedTodoList, "test1", "des1"));
        TodoItem todoItem2 = todoItemRepository.save(buildTodoItem(savedTodoList, "test2", "des2"));

        List<TodoItemGetDto> todoItemGetDtos = todoService.findTodoItemsByTodoListIdOrderByCreatedTime(savedTodoList.getId());
        assertEquals(2, todoItemGetDtos.size());
        assertEquals("test1", todoItemGetDtos.get(0).getNotes());
        assertEquals("des1", todoItemGetDtos.get(0).getDescription());
        assertEquals("test2", todoItemGetDtos.get(1).getNotes());
        assertEquals("des2", todoItemGetDtos.get(1).getDescription());
    }


    private Project buildProject(String name) {
        return Project.builder()
                .name(name)
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    private TodoList buildTodoList(Project project, String title) {
        return TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle(title)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
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
