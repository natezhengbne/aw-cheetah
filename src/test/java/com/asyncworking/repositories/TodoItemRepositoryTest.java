package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Slf4j
@SpringBootTest
class TodoItemRepositoryTest extends DBHelper {

	private Project project;

	private TodoList todoList1;

	@BeforeEach
	void setUp() {
		clearDb();
	}

	@Test
	@Transactional
	public void testCreateTodoItem() {
		saveMockData();

		TodoItem todoItem = buildTodoItem(todoList1);
		todoItemRepository.save(todoItem);
	}

	private TodoItem buildTodoItem(TodoList todoList){
		return TodoItem.builder()
				.todoList(todoList)
				.companyId(todoList.getCompanyId())
				.projectId(todoList.getProject().getId())
				.completed(Boolean.FALSE)
				.createdTime(OffsetDateTime.now(UTC))
				.updatedTime(OffsetDateTime.now(UTC))
				.build();
	}

	private void saveMockData() {
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

		projectRepository.save(project);
		todoListRepository.save(todoList1);
	}
}
