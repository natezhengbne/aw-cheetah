package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.ContributionActivitiesDto;
import com.asyncworking.models.TodoItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TodoItemMapper {
    public ContributionActivitiesDto mapContributionActivitiesDto(TodoItem todoItem) {
        return ContributionActivitiesDto.builder()
                .taskName(todoItem.getDescription())
                .dueDate(todoItem.getDueDate())
                .build();
    }
}
