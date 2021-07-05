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

    private OffsetDateTime dueDate;
}
