package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class TodoItemRepositoryTest extends DBHelper {

    @Mock
    private PasswordEncoder passwordEncoder;

    private TodoItem todoItem1;

    private TodoItem todoItem2;

    private TodoItem todoItem3;
    private Project project;
    private TodoList todoList;

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
        projectRepository.save(project);
        todoList = TodoList.builder()
                .companyId(project.getCompanyId())
                .project(project)
                .todoListTitle("first")
                .updatedTime(OffsetDateTime.now(UTC))
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        todoListRepository.save(todoList);
        todoItem1 = TodoItem.builder()
                .companyId(project.getCompanyId())
                .todoList(todoList)
                .completed(false)
                .updatedTime(OffsetDateTime.now(UTC))
                .createdTime(OffsetDateTime.now(UTC))
                .createdUserId(1L)
                .dueDate(OffsetDateTime.now().minusDays(1))
                .projectId(project.getId())
                .notes("1")
                .originNotes("23")
                .description("123")
                .subscribersIds("1,2,3")
                .build();

        todoItem2 = TodoItem.builder()
                .companyId(project.getCompanyId())
                .todoList(todoList)
                .projectId(project.getId())
                .completed(false)
                .createdTime(OffsetDateTime.now(UTC))
                .createdUserId(1L)
                .updatedTime(OffsetDateTime.now(UTC))
                .dueDate(OffsetDateTime.now().plusDays(3))
                .projectId(project.getId())
                .notes("135df")
                .originNotes("23")
                .description("123")
                .subscribersIds("1,3")
                .build();
        todoItem3 = TodoItem.builder()
                .companyId(project.getCompanyId())
                .todoList(todoList)
                .completed(false)
                .updatedTime(OffsetDateTime.now(UTC))
                .projectId(project.getId())
                .projectId(project.getId())
                .createdTime(OffsetDateTime.now(UTC))
                .createdUserId(1L)
                .dueDate(OffsetDateTime.now().plusDays(5))
                .projectId(project.getId())
                .notes("1123")
                .originNotes("23")
                .description("123")
                .subscribersIds("1,6")
                .build();
        todoItemRepository.save(todoItem1);
        todoItemRepository.save(todoItem2);
        todoItemRepository.save(todoItem3);
    }

    @BeforeEach
    void setUp() {
        clearDb();
    }

    @Test
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
                .dueDate(OffsetDateTime.now().plusDays(3))
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
                .dueDate(OffsetDateTime.now().plusDays(1))
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

    @Test
    @Transactional
    public void findTodoItemListsByDueDate() {
        OffsetDateTime afterSevenDays = OffsetDateTime.now();
        saveMockData();
        List<TodoItem> byCompanyIdAndDueDate = todoItemRepository.
                findByCompanyIdAndDueDate(1L, afterSevenDays);
        assertEquals(1, byCompanyIdAndDueDate.size());

    }
}
