package com.asyncworking.dtos.todolist;

import com.asyncworking.dtos.todoitem.TodoItemMoveDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveTodoListDto {
   TodoListPutDto[] todoLists;
}
