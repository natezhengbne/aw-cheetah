package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

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
	public void giveTodoItemRepository_whenSavedAndRetrievesTodoItem_thenOk() {
		Project savedProject = projectRepository.save(buildProject("AWProject"));
		TodoList savedTodoList = todoListRepository.save(buildTodoList(savedProject, "first"));
		TodoItem savedTodoItem = todoItemRepository.save(buildTodoItem(savedTodoList, "testnotes",
				"test description"));

		Assertions.assertNotNull(savedTodoItem);
		Assertions.assertNotNull(savedTodoItem.getId());
		Assertions.assertEquals("testnotes", savedTodoItem.getNotes());
		Assertions.assertEquals("test description", savedTodoItem.getDescription());
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
