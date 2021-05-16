package com.asyncworking.repositories;

import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
class TodoItemRepositoryTest extends DBHelper {

	private Project mockProject;

	private TodoList mockTodoList;

	private TodoItem mockTodoItem;

	@BeforeEach
	void setUp() {
		clearDb();
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
		mockProject = Project.builder()
				.name("AWProject")
				.isDeleted(false)
				.isPrivate(false)
				.leaderId(1L)
				.companyId(1L)
				.createdTime(OffsetDateTime.now(UTC))
				.updatedTime(OffsetDateTime.now(UTC))
				.build();

		mockTodoList = TodoList.builder()
				.companyId(mockProject.getCompanyId())
				.project(mockProject)
				.todoListTitle("first")
				.createdTime(OffsetDateTime.now(UTC))
				.updatedTime(OffsetDateTime.now(UTC))
				.build();

		mockTodoItem = TodoItem.builder()
				.todoList(mockTodoList)
				.companyId(mockTodoList.getCompanyId())
				.projectId(mockTodoList.getProject().getId())
				.completed(Boolean.FALSE)
				.createdTime(OffsetDateTime.now(UTC))
				.updatedTime(OffsetDateTime.now(UTC))
				.build();

		projectRepository.save(mockProject);
		todoListRepository.save(mockTodoList);
		todoItemRepository.save(mockTodoItem);
	}
}
