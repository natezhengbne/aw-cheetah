package com.asyncworking.dtos.todoitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemGetDto implements Comparable<TodoItemGetDto> {

    private Long todoItemId;

    private String description;

    private String notes;

    private String originNotes;

    private Long projectId;

    private Long companyId;

    private Boolean completed;

    private String priority;

    private OffsetDateTime createdTime;

    private OffsetDateTime dueDate;

    private String subscribersIds;

    private OffsetDateTime completedTime;

    private Long order;

    @Override
    public int compareTo(@NotNull TodoItemGetDto todoItemGetDto) {
        if (getCompletedTime() == null || todoItemGetDto.getCompletedTime() == null) {
            return -1;
        }
        return getCompletedTime().compareTo(todoItemGetDto.getCompletedTime());
    }
}
