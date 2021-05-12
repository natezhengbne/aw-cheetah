package com.asyncworking.dtos.todoitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoItemPostDto {

    @NotNull(message = "TodoListId cannot be null")  // Necessary???
    private Long todoListId;

    @NotNull(message = "content cannot be null")
    private String content;

    private String eventDocUrl;

    private String description;

    private LocalDate dueDate;
}
