package com.asyncworking.services;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemPutDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.TodoItemNotFoundException;
import com.asyncworking.exceptions.TodoListNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.TodoListRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.TodoMapper;
import com.asyncworking.utility.mapper.TodoMapperImpl;
import com.asyncworking.utility.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoListRepository todoListRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TodoItemRepository todoItemRepository;

    @Mock
    private UserService userService;

    private TodoService todoService;

    @Mock
    private UserRepository userRepository;

    private TodoMapper todoMapper;

    private UserMapper userMapper;

    private TodoListDto mockTodoListDto;

    private UserEntity userEntity;

    private Project project;

    private TodoList todoList;

    private TodoItem todoItem1;

    private TodoItem todoItem2;

    @BeforeEach
    public void setup() {
        todoMapper = new TodoMapperImpl();
        todoService = new TodoService(
                todoListRepository,
                todoItemRepository,
                projectRepository,
                userService,
                todoMapper,
                userRepository,
                userMapper);

        mockTodoListDto = TodoListDto.builder()
                .projectId(1L)
                .todoListTitle("FirstTodoList")
                .build();

        project = Project.builder()
                .id(1L)
                .name("AWProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build();

        todoList = TodoList.builder()
                .id(1L)
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("todolist title")
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build();

        todoItem1 = buildTodoItem(todoList, "test1", "des1");
        todoItem2 = buildTodoItem(todoList, "test2", "des2");

    }

    @Test
    @Transactional
    public void createTodoListSuccess() {
        when(projectRepository.findByCompanyIdAndId(1L, 1L))
                .thenReturn(Optional.of(project));
        ArgumentCaptor<TodoList> todoListCaptor = ArgumentCaptor.forClass(TodoList.class);
        todoService.createTodoList(project.getCompanyId(), project.getId(), mockTodoListDto);
        verify(todoListRepository).save(todoListCaptor.capture());
        assertEquals(project, todoListCaptor.getValue().getProject());
    }

    @Test
    public void throwProjectNotFoundExceptionWhenProjectIdIsNotExist() {
        lenient().when(projectRepository.findById(2L))
                .thenThrow(new ProjectNotFoundException("Cannot find project by id:2"));
        assertThrows(ProjectNotFoundException.class, () -> todoService.createTodoList(1L, 2L, mockTodoListDto));
    }

    @Test
    public void returnRequiredQuantityOfTodoListDto() {
        Integer quantity = 3;
        List<TodoList> todoLists = new ArrayList<>();
        todoLists.add(TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("first")
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build());
        todoLists.add(TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("second")
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build());
        todoLists.add(TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("third")
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build());
        when(todoListRepository.findTodolistWithTodoItems(project.getCompanyId(),
                project.getId(), PageRequest.of(0, quantity)))
                .thenReturn(todoLists);
        List<TodoListDto> todoListDtos = todoService.findRequiredNumberTodoListsByCompanyIdAndProjectId(project.getCompanyId(),
                project.getId(), quantity);
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
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build();

        when(todoListRepository.findByCompanyIdAndProjectIdAndId(project.getCompanyId(), project.getId(),
                mockTodoList.getId())).thenReturn(Optional.of(mockTodoList));
        assertEquals(returnedMockTodoListDto.getTodoListTitle(), todoService.fetchSingleTodoList(project.getCompanyId(), project.getId(),
                mockTodoList.getId()).getTodoListTitle());
    }

    @Test
    public void throwTodoListNotFoundExceptionWhenIdIsNotExist() {
        lenient().when(todoListRepository.findById(2L))
                .thenThrow(new TodoListNotFoundException("Cannot find todoList by id: 2"));
        assertThrows(TodoListNotFoundException.class, () -> todoService.fetchSingleTodoList(1L, 1L, 2L));
    }

    @Test
    public void givenTodoListId_shouldReturnListOfTodoItems_thenOk() {
        List<TodoItem> todoItems = new ArrayList<>();
        todoItems.add(todoItem1);
        todoItems.add(todoItem2);
        when(todoItemRepository.findByCompanyIdAndProjectIdAndTodoListIdOrderByCreatedTimeDesc(any(), any(), any()))
                .thenReturn(todoItems);
        List<TodoItemGetDto> todoItemGetDtos = todoService.findByCompanyIdAndProjectIdAndTodoListIdOrderByCreatedTimeDesc
                (todoList.getCompanyId(), todoList.getProject().getId(), todoList.getId());
        assertEquals(2, todoItemGetDtos.size());
        assertEquals("test1", todoItemGetDtos.get(0).getNotes());
        assertEquals("des1", todoItemGetDtos.get(0).getDescription());
        assertEquals("test2", todoItemGetDtos.get(1).getNotes());
        assertEquals("des2", todoItemGetDtos.get(1).getDescription());
    }

    @Test
    public void shouldReturnTodoItemPageDtoByGivenTodoitemId() {
        when(todoItemRepository.findByCompanyIdAndProjectIdAndId(any(), any(), any()))
                .thenReturn(Optional.of(todoItem1));
        when(projectRepository.findByCompanyIdAndId(any(), any()))
                .thenReturn(Optional.of(project));
        when(userService.findUserById(any()))
                .thenReturn(buildUser());
        when(userService.findUserById(any()))
                .thenReturn(UserEntity.builder()
                        .name("lalal")
                        .build());
        TodoItemPageDto returnedTodoItemPageDto = todoService.
                fetchTodoItemPageInfoByIds(project.getCompanyId(), project.getId(), todoItem1.getId());
        assertEquals(project.getName(), returnedTodoItemPageDto.getProjectName());
    }

    @Test
    public void throwTodoItemNotFoundExceptionWhenTodoitemIdIsNotExist() {
        when(todoItemRepository.findByCompanyIdAndProjectIdAndId(2L, 2L, 2L))
                .thenReturn(Optional.empty());
        Exception exception = assertThrows(TodoItemNotFoundException.class,
                () -> todoService.fetchTodoItemPageInfoByIds(2L, 2L, 2L));

        String expectedMessage = "Cannot find TodoItem by id: 2";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void throwTodoItemNotFoundExceptionWhenTodoItemNotExist() {
        TodoItemPutDto todoItemPut = TodoItemPutDto.builder()
                .description("title")
                .notes("notes/n")
                .originNotes("<div>notes</div>")
                .dueDate(OffsetDateTime.now(UTC))
                .subscribersIds("1,2,3")
                .build();
        when(todoItemRepository.updateTodoItem(1L, todoItemPut.getDescription(), todoItemPut.getNotes(),
                todoItemPut.getOriginNotes(), todoItemPut.getDueDate(), 1L, 1L,
                todoItemPut.getSubscribersIds())).thenReturn(0);
        assertThrows(TodoItemNotFoundException.class, () -> todoService.updateTodoItemDetails(1L, 1L, 1L, todoItemPut));

    }

    private TodoItem buildTodoItem(TodoList todoList, String notes, String description) {
        return TodoItem.builder()
                .todoList(todoList)
                .companyId(todoList.getCompanyId())
                .projectId(todoList.getProject().getId())
                .notes(notes)
                .description(description)
                .completed(Boolean.FALSE)
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build();
    }

    private TodoItem buildTodoItem(TodoList todoList, String notes, String description, String subscribersIds) {
        return TodoItem.builder()
                .todoList(todoList)
                .companyId(todoList.getCompanyId())
                .projectId(todoList.getProject().getId())
                .notes(notes)
                .description(description)
                .completed(Boolean.FALSE)
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .subscribersIds(subscribersIds)
                .build();
    }

    private UserEntity buildUser() {
        return UserEntity.builder()
                .name("test user")
                .build();
    }

}
