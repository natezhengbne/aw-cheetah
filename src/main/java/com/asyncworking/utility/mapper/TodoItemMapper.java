package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.ContributionActivitiesDto;
import com.asyncworking.models.TodoItem;
import org.springframework.stereotype.Component;

@Component
public class TodoItemMapper {
    public ContributionActivitiesDto mapContributionActivitiesDto(TodoItem todoItem) {
        return ContributionActivitiesDto.builder()
                .taskName(todoItem.getDescription())
                .dueDate(todoItem.getDueDate())
                .build();
    }
}
