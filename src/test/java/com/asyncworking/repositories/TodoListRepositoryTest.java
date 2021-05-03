package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import com.asyncworking.models.TodoBoard;
import com.asyncworking.models.TodoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TodoListRepositoryTest extends DBHelper {

    @BeforeEach
    public void clearDB() {
        clearDb();
    }

    @Test
    public void shouldReturnTodoListsByProjectId() {
        saveMockData();
        List<TodoList> lists = todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(1L);
        assertEquals(3, lists.size());
    }

    @Test
    public void shouldReturnEmptyWhenProjectIdNotExist(){
        saveMockData();
        List<TodoList> lists = todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(2L);
        assertTrue(lists.isEmpty());
    }

    private void saveMockData() {
        Project project = Project.builder()
                .id(1L)
                .name("AWProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        TodoBoard todoBoard = TodoBoard.builder()
                .id(1L)
                .project(project)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        TodoList todoList1 = TodoList.builder()
                .id(1L)
                .companyId(1L)
                .projectId(project.getId())
                .todoBoard(todoBoard)
                .todoListTitle("first")
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        TodoList todoList2 = TodoList.builder()
                .id(2L)
                .companyId(1L)
                .projectId(project.getId())
                .todoBoard(todoBoard)
                .todoListTitle("second")
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        TodoList todoList3 = TodoList.builder()
                .id(3L)
                .companyId(1L)
                .projectId(project.getId())
                .todoBoard(todoBoard)
                .todoListTitle("third")
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        projectRepository.save(project);
        todoBoardRepository.save(todoBoard);
        todoListRepository.save(todoList1);
        todoListRepository.save(todoList2);
        todoListRepository.save(todoList3);
    }
}
