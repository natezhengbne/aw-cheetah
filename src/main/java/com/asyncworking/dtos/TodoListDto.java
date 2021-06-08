package com.asyncworking.dtos;


import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import com.asyncworking.models.TodoItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoListDto {

    private Long id;

    @NotNull(message = "projectId cannot be null")
    private Long projectId;

    @Size(max = 255, message = "Todo List Title can not be more than 255 characters! ")
    @NotNull(message = "todoList must have a title")
    private String todoListTitle;

    @Size(max = 2048, message = "Details can not be more than 2048 characters! ")
    private String details;

    private String docURL;

    List<TodoItemGetDto> todoItemGetDtos;
}