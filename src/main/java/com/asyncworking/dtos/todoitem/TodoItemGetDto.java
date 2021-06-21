package com.asyncworking.dtos.todoitem;


import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemGetDto {

    private Long todoItemId;

    private String description;

    private String notes;

    private Long projectId;

    private Boolean completed;

    private OffsetDateTime createdTime;
}
