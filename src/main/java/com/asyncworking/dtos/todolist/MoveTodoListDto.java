package com.asyncworking.dtos.todolist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveTodoListDto {
//   TodoListPutDto[] todoLists;
   @Size(min = 1, message = "todoItems should not empty")
   List<TodoListPutDto> todoLists;
}
