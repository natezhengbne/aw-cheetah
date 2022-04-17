package com.asyncworking.dtos.todoitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoItemMoveDto {

    private Long todoItemId;

    private Long companyId;

    private boolean completed;

    private String description;

    private String notes;

    private String originNotes;

    private String priority;

    private OffsetDateTime dueDate;

    private String subscribersIds;

    private OffsetDateTime completedTime;

    private OffsetDateTime createdTime;


}

