package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import com.asyncworking.models.TodoList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class TodoListRepositoryTest extends DBHelper {

    private Project project;

    private TodoList todoList1;

    private TodoList todoList2;

    private TodoList todoList3;

    @BeforeEach
    public void clearDB() {
        clearDb();
    }

    @Test
    @Transactional
    public void shouldReturnTodoListsByProjectIdWithCorrectQuantity() {
        saveMockData();
        List<TodoList> lists = todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(project.getId(), 2);
        assertEquals(2, lists.size());
    }

    @Test
    @Transactional
    public void shouldReturnEmptyWhenProjectIdNotExist() {
        saveMockData();
        List<TodoList> lists = todoListRepository.findTodoListsByProjectIdOrderByCreatedTime(Long.MAX_VALUE, 5);
        assertTrue(lists.isEmpty());
    }

    @Test
    @Transactional
    public void shouldGet1AndUpdateTodolistSuccessfully() {
        saveMockData();
        int count = todoListRepository.updateTodoListTitle(todoList1.getId(),
                project.getCompanyId(),
                project.getId(),
                "New Title xxx",
                OffsetDateTime.now(UTC));
        assertEquals(1, count);
    }

    @Test
    @Transactional
    public void shouldReturnTodoListCompanyIdAndProjectIdAndTodoListTitle() {
        saveMockData();
        Optional<TodoList> todoList = todoListRepository.findTodoListByCompanyIdAndProjectIdAndTodoListTitle(todoList1.getCompanyId(),
                todoList1.getProject().getId(), todoList1.getTodoListTitle());
        assertEquals(todoList.get(), todoList1);
    }

    void saveMockData() {
        project = Project.builder()
                .name("AWProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        todoList1 = TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("first")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        todoList2 = TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("second")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        todoList3 = TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("third")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        projectRepository.save(project);
        todoListRepository.save(todoList1);
        todoListRepository.save(todoList2);
        todoListRepository.save(todoList3);
    }
}

