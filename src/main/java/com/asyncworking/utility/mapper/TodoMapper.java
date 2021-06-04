package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPageDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TodoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", source = "project.companyId")
    @Mapping(target = "project", source = "project")
    @Mapping(target = "todoListTitle", source = "todoListDto.todoListTitle")
    @Mapping(target = "details", source = "todoListDto.details")
    @Mapping(target = "docUrl", source = "todoListDto.docUrl")
    @Mapping(target = "createdTime", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedTime", expression = "java(getCurrentTime())")
    TodoList toTodoListEntity(TodoListDto todoListDto, Project project);

    @Mapping(target = "projectId", expression = "java(getProjectId(todoList))")
    @Mapping(target = "todoItemGetDtos", source = "todoItemGetDtoList")
    TodoListDto fromTodoListEntity(TodoList todoList, List<TodoItemGetDto> todoItemGetDtoList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "todoList", source = "todoList")
    @Mapping(target = "companyId", source = "todoList.companyId")
    @Mapping(target = "projectId", expression = "java(getProjectId(todoList))")
    @Mapping(target = "completed", expression = "java(Boolean.FALSE)")
    @Mapping(target = "createdTime", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedTime", expression = "java(getCurrentTime())")
    TodoItem toTodoItemEntity(TodoItemPostDto todoItemPostDto, TodoList todoList);

    TodoItemGetDto fromTodoItemEntity(TodoItem todoItem);

    default Long getTodoListId(TodoItem todoItem) {
        return todoItem.getTodoList().getId();
    }

    default Long getProjectId(TodoList todoList) {
        return todoList.getProject().getId();
    }

    default OffsetDateTime getCurrentTime() {
        return OffsetDateTime.now();
    }
}

