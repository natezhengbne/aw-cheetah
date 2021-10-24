package com.asyncworking.dtos.todoitem;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoItemPutDto {

    private String description;

    private String notes;

    private String originNotes;

    private String priority;

    private OffsetDateTime dueDate;

    private String subscribersIds;

    private OffsetDateTime completedTime;
}
