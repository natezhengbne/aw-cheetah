package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.dtos.todoitem.TodoItemPutDto;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.models.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TodoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", source = "project.companyId")
    @Mapping(target = "project", source = "project")
    @Mapping(target = "todoListTitle", source = "todoListDto.todoListTitle")
//    @Mapping(target = "details", source = "todoListDto.details")
    @Mapping(target = "docUrl", source = "todoListDto.docUrl")
    @Mapping(target = "createdTime", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedTime", expression = "java(getCurrentTime())")
    TodoList toTodoListEntity(TodoListDto todoListDto, Project project);

    @Mapping(target = "projectId", expression = "java(getProject(todoList).getId())")
    @Mapping(target = "todoItemGetDtos", source = "todoItemGetDtoList")
    TodoListDto fromTodoListEntity(TodoList todoList, List<TodoItemGetDto> todoItemGetDtoList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "todoList", source = "todoList")
    @Mapping(target = "companyId", source = "todoList.companyId")
    @Mapping(target = "projectId", expression = "java(getProject(todoList).getId())")
    @Mapping(target = "completed", expression = "java(Boolean.FALSE)")
    @Mapping(target = "createdTime", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedTime", expression = "java(getCurrentTime())")
    TodoItem toTodoItemEntity(TodoItemPostDto todoItemPostDto, TodoList todoList);

    @Mapping(target = "todoItemId", source = "id")
    TodoItemGetDto fromTodoItemEntity(TodoItem todoItem);

    @Mapping(target = "todoListId", expression = "java(getTodoList(todoItem).getId())")
    @Mapping(target = "todoListTitle", expression = "java(getTodoList(todoItem).getTodoListTitle())")
    @Mapping(target = "projectId", expression = "java(project.getId())")
    @Mapping(target = "projectName", expression = "java(project.getName())")
    @Mapping(target = "todoItemGetDto", expression = "java(fromTodoItemEntity(todoItem))")
    @Mapping(target = "createdUserName", expression = "java(userEntity.getName())")
    TodoItemPageDto fromTodoItemToTodoItemPageDto(TodoItem todoItem, Project project, UserEntity userEntity);

    List<TodoItemGetDto> todoItemsToTodoItemGetDtos(List<TodoItem> todoItems);

    default TodoList getTodoList(@NotNull TodoItem todoItem) {
        return todoItem.getTodoList();
    }

    default Project getProject(@NotNull TodoList todoList) {
        return todoList.getProject();
    }

    default OffsetDateTime getCurrentTime() {
        return OffsetDateTime.now(UTC);
    }
}

