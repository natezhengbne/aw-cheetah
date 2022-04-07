package com.asyncworking.dtos.todolist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveTodoListDto {
   TodoListPutDto[] todoLists;
}
