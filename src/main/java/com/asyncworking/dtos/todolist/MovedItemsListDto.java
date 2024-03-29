package com.asyncworking.dtos.todolist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovedItemsListDto {
    @NotNull(message = "movedItemsList should not empty")
    TodoListPutDto movedItemsList;
}
