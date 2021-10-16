package com.asyncworking.dtos;


import com.asyncworking.dtos.todoitem.TodoItemGetDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoListDto {

    private Long id;

    private Long projectId;

    @NotBlank(message = "todoList must have a title.")
    @Size(max = 255, message = "Todo List Title can not be more than 255 characters.")
    private String todoListTitle;

    @Size(max = 2048, message = "Details can not be more than 2048 characters.")
    private String details;

    private String originDetails;

    private String docUrl;

    List<TodoItemGetDto> todoItemsGetDto;
}
