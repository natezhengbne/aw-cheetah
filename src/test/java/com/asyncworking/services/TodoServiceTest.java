package com.asyncworking.services;

import com.asyncworking.dtos.TodoBoardDto;
import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoBoard;
import com.asyncworking.models.TodoList;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.TodoBoardRepository;
import com.asyncworking.repositories.TodoListRepository;
import com.asyncworking.utility.mapper.TodoMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class TodoServiceTest {

    @Mock
    private TodoListRepository todoListRepository;

    @Mock
    private TodoBoardRepository todoBoardRepository;

    @Mock
    private ProjectRepository projectRepository;


    private TodoService todoService;

    @Autowired
    private TodoMapper todoMapper;

    @BeforeEach
    public void setUp(){
        todoService = new TodoService(
                todoListRepository,
                todoBoardRepository,
                projectRepository,
                todoMapper
        );
    }
    @Test
    @Transactional
    public void createTodoBoardSuccess() {
        TodoBoardDto todoBoardDto = TodoBoardDto.builder()
                .projectId(1L)
                .build();
        Project mockProject = Project.builder()
                .id(1L)
                .name("a")
                .build();
        when(projectRepository.findById(todoBoardDto.getProjectId()))
                .thenReturn(Optional.of(mockProject));
        ArgumentCaptor<TodoBoard> todoBoardCaptor = ArgumentCaptor.forClass(TodoBoard.class);
        todoService.createTodoBoard(todoBoardDto);
        verify(todoBoardRepository).save(todoBoardCaptor.capture());
        Assertions.assertEquals(mockProject, todoBoardCaptor.getValue().getProject());
    }

    @Test
    @Transactional
    public void createTodoListSuccess() {
        Project mockProject = Project.builder()
                .id(1L)
                .name("a")
                .build();
        TodoBoard todoBoard = TodoBoard.builder()
                .id(1L)
                .project(mockProject)
                .build();
        TodoListDto mockTodoListDto = TodoListDto.builder()
                .companyId(1L)
                .todoBoardId(1L)
                .companyId(1L)
                .projectId(1L)
                .todoListTitle("FirstTodoList")
                .build();
        when(todoBoardRepository.findById(1L))
                .thenReturn(Optional.of(todoBoard));
        ArgumentCaptor<TodoList> todoListCaptor = ArgumentCaptor.forClass(TodoList.class);
        todoService.createTodoList(mockTodoListDto);
        verify(todoListRepository).save(todoListCaptor.capture());
        Assertions.assertEquals(mockTodoListDto.getCompanyId(), todoListCaptor.getValue().getCompanyId());
    }

    @Test
    public void returnProjectIdCorrespondingTodoLists() {
        Project mockProject = Project.builder()
                .id(1L)
                .name("a")
                .build();
        TodoBoard todoBoard = TodoBoard.builder()
                .id(1L)
                .project(mockProject)
                .build();
        TodoList mockTodoList1 = TodoList.builder()
                .companyId(1L)
                .todoBoard(todoBoard)
                .companyId(1L)
                .projectId(1L)
                .todoListTitle("FirstTodoList")
                .build();
        TodoList mockTodoList2 = TodoList.builder()
                .companyId(2L)
                .todoBoard(todoBoard)
                .companyId(1L)
                .projectId(1L)
                .todoListTitle("SecondTodoList")
                .build();
        List<TodoList> lists = new ArrayList<>();
        lists.add(mockTodoList1);
        lists.add(mockTodoList2);
        when(todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(1L))
                .thenReturn(lists);
        ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
        List<TodoListDto> ret = todoService.findTodoListsByProjectId(1L);
        verify(todoListRepository).findTodoListsByProjectIdOrderByCreatedTime(projectIdCaptor.capture());
        Assertions.assertEquals(1L, projectIdCaptor.getValue());
        Assertions.assertEquals(ret.size(), lists.size());
    }

    @Test
    public void returnEmptyListWhenProjectIdNotExist(){
        List<TodoList> mockEmptyLists = new ArrayList<>();
        when(todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(2L))
                .thenReturn(mockEmptyLists);
        List<TodoListDto> ret = todoService.findTodoListsByProjectId(2L);
        Assertions.assertTrue(ret.isEmpty());
    }

}



