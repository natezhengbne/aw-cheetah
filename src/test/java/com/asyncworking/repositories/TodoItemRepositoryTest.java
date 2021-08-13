package com.asyncworking.repositories;

import com.asyncworking.models.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class TodoItemRepositoryTest extends DBHelper {

    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        clearDb();
    }

    @Test
    @Rollback
    public void giveTodoItemRepository_whenSavedAndRetrievesTodoItem_thenOk() {
        Project savedProject = projectRepository.save(buildProject("AWProject"));
        TodoList savedTodoList = todoListRepository.save(buildTodoList(savedProject, "first"));
        TodoItem savedTodoItem = todoItemRepository.save(buildTodoItem(savedTodoList, "test notes",
                "test description"));

        Assertions.assertNotNull(savedTodoItem);
        Assertions.assertNotNull(savedTodoItem.getId());
        Assertions.assertEquals("test notes", savedTodoItem.getNotes());
        Assertions.assertEquals("test description", savedTodoItem.getDescription());
    }

    private Project buildProject(String name) {
        return Project.builder()
                .name(name)
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build();
    }

    private TodoList buildTodoList(Project project, String title) {
        return TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle(title)
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
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
                .createdUserId(1L)
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build();
    }

    private TodoItem buildTodoItem(TodoList todoList, String notes, String description, String subscribersIds) {
        return TodoItem.builder()
                .createdUserId(1L)
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

    @Test
    public void findAssignedPeopleTest() {

        String subscribersIds = "1,2";
        Project project = buildProject("haha");
        projectRepository.save(project);
        TodoList todoList = buildTodoList(project, "hahaha");
        todoListRepository.save(todoList);
        TodoItem todoItem = buildTodoItem(todoList, "nihao", "heihei", subscribersIds);
        todoItemRepository.save(todoItem);

        assertEquals(subscribersIds, todoItemRepository.findSubscribersIdsByProjectIdAndId(project.getCompanyId(),
                project.getId(), todoItem.getId()));

    }
}
