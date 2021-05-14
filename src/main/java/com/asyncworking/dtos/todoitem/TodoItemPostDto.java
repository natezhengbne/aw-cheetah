package com.asyncworking.dtos.todoitem;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Builder
@Data
public class TodoItemPostDto {

    @NotNull(message = "TodoListId cannot be null")
    private Long todoListId;

    @NotNull(message = "content cannot be null")
    private String notes;

    @Size(max = 512, message = "description can not be more than 512 characters! ")
    private String description;

//    private String eventDocUrl;

//    private Boolean completed;

//    private LocalDate dueDate;
}

