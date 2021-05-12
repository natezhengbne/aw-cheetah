package com.asyncworking.utility.mapper;


import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.dtos.todoitem.TodoItemPostDto;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Component
public class TodoMapper {

    public TodoListDto mapEntityToTodoListDto(TodoList todoList){
        return TodoListDto.builder()
                .id(todoList.getId())
                .todoListTitle(todoList.getTodoListTitle())
                .details(todoList.getDetails())
                .docURL(todoList.getDocURL())
                .build();
    }
}
