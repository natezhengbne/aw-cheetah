package com.asyncworking.dtos.todoitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardTodoItemDto {
    private Long todoItemId;

    private String description;

    private String projectTitle;

    private String priority;

    private OffsetDateTime dueDate;
}
