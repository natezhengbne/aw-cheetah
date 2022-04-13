package com.asyncworking.dtos.todolist;

import com.asyncworking.dtos.todoitem.TodoItemMoveDto;
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
public class TodoListPutDto {
    private Long id;
    @NotNull(message = "todoItems should not empty")
    List<TodoItemMoveDto> todoItems;
    private Long prjectid;
    private String todoListTitle;
    private String details;
    private String docUrl;
}
