package com.asyncworking.dtos.todoitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemPostDto {
    @NotNull(message = "TodoListId cannot be null.")
    private Long todolistId;

    @NotBlank(message = "TodoItem description can not be blank.")
    @Size(max = 512, message = "Description can not be more than 512 characters.")
    private String description;

    @NotNull(message = "content cannot be null")
    private String notes;

    private String originNotes;

    @NotNull(message = "created user id is required")
    private Long createdUserId;

    private LocalDate dueDate;
}
