package com.asyncworking.dtos.todoitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemGetDto {

    private Long todoItemId;

    private String description;

    private String notes;

    private String originNotes;

    private Long projectId;

    private Boolean completed;

    private OffsetDateTime createdTime;

    private OffsetDateTime dueDate;

    private String subscribersIds;

}
