package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
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
                .createdTime(now(UTC))
                .updatedTime(now(UTC))
                .build();
    }

    @Test
    public void testTodoList() {
        setUpVa();
        find();

    }

    @Transactional
    void find() {
        List<TodoList> todoLists = todoListRepository.findTodolistWithTodoItems(savedProject.getId(), PageRequest.of(0, 2));

        for (TodoList todoList : todoLists) {
            log.info("toList:" + todoList.getTodoListTitle());
        }

        for (TodoList todoList : todoLists) {
            List<TodoItem> todoItems = todoList.getTodoItems();
            for (TodoItem todoItem : todoItems) {
                log.info("todoItem desc:" + todoItem.getNotes());
            }
        }
    }

    private Project savedProject;

    void setUpVa() {
        setUp();
        savedProject = projectRepository.save(buildProject("AWProject"));
        TodoList savedTodoList = todoListRepository.save(buildTodoList(savedProject, "first"));
        TodoList savedTodoList2 = todoListRepository.save(buildTodoList(savedProject, "second"));
        TodoItem savedTodoItem = todoItemRepository.save(buildTodoItem(savedTodoList, "test notes1",
                "test description"));
        TodoItem savedTodoItem2 = todoItemRepository.save(buildTodoItem(savedTodoList, "test notes2",
                "test description"));
        TodoItem savedTodoItem3 = todoItemRepository.save(buildTodoItem(savedTodoList2, "test notes3",
                "test description"));
        TodoItem savedTodoItem4 = todoItemRepository.save(buildTodoItem(savedTodoList2, "test notes4",
                "test description"));
        TodoItem savedTodoItem5 = todoItemRepository.save(buildTodoItem(savedTodoList2, "test notes5",
                "test description"));
    }
}
