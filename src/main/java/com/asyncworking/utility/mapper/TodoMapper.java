package com.asyncworking.utility.mapper;


import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.models.TodoList;
import org.springframework.stereotype.Component;


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
