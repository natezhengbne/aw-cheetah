package com.asyncworking.dtos.todoitem;

import com.asyncworking.constants.TodoItemOrder;
import com.asyncworking.models.TodoItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        if ((first == null && second == null) || firstIndex == secondIndex) return 0;
        if ((first == null && second != null) || firstIndex < secondIndex) return 1;
        return -1;
    }
    public static boolean filterSubscriberId (TodoItem todoItem,Long userId){
        String subscribersIds = todoItem.getSubscribersIds().trim();
        if(subscribersIds.equals(""))return false;
        List<Long> subscriberID = Stream.of(subscribersIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
       return  subscriberID.contains(userId);
    }

}

