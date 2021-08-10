package com.asyncworking.repositories;

import com.asyncworking.config.TestConfig;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

@Slf4j
@SpringBootTest
@Import(TestConfig.class)
class TodoItemRepositoryTest extends DBHelper {

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
}
