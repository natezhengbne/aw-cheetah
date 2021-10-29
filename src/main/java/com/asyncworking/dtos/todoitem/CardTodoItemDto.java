package com.asyncworking.dtos.todoitem;

import com.asyncworking.constants.TodoItemOrder;
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

    public static int comparePriority(String first, String second) {
        int firstIndex = TodoItemOrder.priorityOrder.indexOf(first);
        int secondIndex = TodoItemOrder.priorityOrder.indexOf(second);

        if ((second == null && first == null) || firstIndex == secondIndex) return 0;
        if ((second == null && first != null) || firstIndex > secondIndex) return 1;
        return -1;
    }

}

