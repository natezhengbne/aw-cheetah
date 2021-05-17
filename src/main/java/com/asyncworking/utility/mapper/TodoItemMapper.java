package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.models.TodoItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TodoItemMapper {

    TodoItem toEntity(TodoItemPostDto todoItemPostDto);

    TodoItemGetDto fromEntity(TodoItem todoItem);
}
