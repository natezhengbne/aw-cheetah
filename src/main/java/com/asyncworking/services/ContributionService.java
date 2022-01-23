package com.asyncworking.services;

import com.asyncworking.dtos.ContributionActivitiesDto;
import com.asyncworking.models.TodoItem;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.utility.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {
    private final TodoItemRepository todoItemRepository;

    public Map<DayOfWeek, Integer> findOneWeekCompletedTodoItemsCounts(Long companyId, Long userId) {
        OffsetDateTime start = getStartDateTime();
        Map<DayOfWeek, Integer> oneWeekCompletedTodoItemsCounts = new LinkedHashMap<>();

        for (int i = 0; i < DayOfWeek.values().length; i++) {
            OffsetDateTime end = start.withHour(23).withMinute(59).withSecond(59);
            int completedTodoItemsCount = todoItemRepository
                    .countByCompanyIdAndSubscribersIdsIsContainingAndCompletedTimeBetween(companyId, userId.toString(), start, end);
            oneWeekCompletedTodoItemsCounts.put(start.getDayOfWeek(), completedTodoItemsCount);
            start = start.plusDays(1);
        }
        return oneWeekCompletedTodoItemsCounts;
    }

    public Map<DayOfWeek, List<ContributionActivitiesDto>> findOneWeekCompletedTodoItemsList(Long companyId, Long userId) {
        OffsetDateTime start = getStartDateTime();
        Map<DayOfWeek, List<ContributionActivitiesDto>> oneWeekCompletedTodoItemsList = new LinkedHashMap<>();
        for (int i = 0; i < DayOfWeek.values().length; i++) {
            OffsetDateTime end = start.withHour(23).withMinute(59).withSecond(59);
            List<TodoItem> completedTodoItems = todoItemRepository
                    .findByCompanyIdAndSubscribersIdsIsContainingAndCompletedTimeBetween(companyId, userId.toString(), start, end);
            List<ContributionActivitiesDto> contributionActivitiesDtos = completedTodoItems.stream()
                    .map(TodoMapper::mapContributionActivitiesDto)
                    .collect(Collectors.toList());
            oneWeekCompletedTodoItemsList.put(start.getDayOfWeek(), contributionActivitiesDtos);
            start = start.plusDays(1);
        }
        return oneWeekCompletedTodoItemsList;
    }

    private OffsetDateTime getStartDateTime() {
        OffsetDateTime today = OffsetDateTime.now().truncatedTo(ChronoUnit.HOURS);
        OffsetDateTime startDateOfWeek = today.minusDays
                (today.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : today.getDayOfWeek().getValue());
        return startDateOfWeek.withHour(0).withMinute(0).withSecond(0);
    }
}
