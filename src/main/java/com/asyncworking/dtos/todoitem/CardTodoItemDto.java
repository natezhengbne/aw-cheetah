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

    public int comparePriority(String first, String second) {
        int firstPriority = TodoItemOrder.priorityOrder.indexOf(first);
        int secondPriority = TodoItemOrder.priorityOrder.indexOf(second);

        if ((second == null && first == null) || firstPriority == secondPriority) {
            return 0;
        }
        else if ((second == null && first != null) || firstPriority > secondPriority) {
            return 1;
        }
        return -1;
    }

}

