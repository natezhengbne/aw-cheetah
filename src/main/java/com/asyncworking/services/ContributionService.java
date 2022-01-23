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
import java.util.ArrayList;
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
        OffsetDateTime endDate = start.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        List<TodoItem> completedTodoItems2 = todoItemRepository
                .findByCompanyIdAndSubscribersIdsIsContainingAndCompletedTimeBetween(companyId, userId.toString(), start, endDate);

        Map<DayOfWeek, List<ContributionActivitiesDto>> oneWeekCompletedTodoItemsMap = getDayTask(completedTodoItems2);
        return oneWeekCompletedTodoItemsMap;
    }
/*
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
*/

    private OffsetDateTime getStartDateTime() {
        OffsetDateTime today = OffsetDateTime.now().truncatedTo(ChronoUnit.HOURS);
        OffsetDateTime startDateOfWeek = today.minusDays
                (today.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : today.getDayOfWeek().getValue());
        OffsetDateTime start = startDateOfWeek.withHour(0).withMinute(0).withSecond(0);
        return start;
    }

    private Map<DayOfWeek, List<ContributionActivitiesDto>> getDayTask(List<TodoItem> completedTodoItems2) {
        List<ContributionActivitiesDto> mondayActivities = new ArrayList<>();
        List<ContributionActivitiesDto> tuesdayActivities = new ArrayList<>();
        List<ContributionActivitiesDto> wednesdayActivities = new ArrayList<>();
        List<ContributionActivitiesDto> thursdayActivities = new ArrayList<>();
        List<ContributionActivitiesDto> fridayActivities = new ArrayList<>();
        List<ContributionActivitiesDto> saturdayActivities = new ArrayList<>();
        List<ContributionActivitiesDto> sundayActivities = new ArrayList<>();
        Map<DayOfWeek, List<ContributionActivitiesDto>> oneWeekCompletedTodoItemsMap = new LinkedHashMap<>();
        sundayActivities = completedTodoItems2.stream().filter(completeItem -> completeItem.getCompletedTime().getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .map(sundayItem -> TodoMapper.mapContributionActivitiesDto(sundayItem)).collect(Collectors.toList());
        oneWeekCompletedTodoItemsMap.put(DayOfWeek.SUNDAY, sundayActivities);
        mondayActivities = completedTodoItems2.stream().filter(completeItem -> completeItem.getCompletedTime().getDayOfWeek().equals(DayOfWeek.MONDAY))
                .map(mondayItem -> TodoMapper.mapContributionActivitiesDto(mondayItem)).collect(Collectors.toList());
        oneWeekCompletedTodoItemsMap.put(DayOfWeek.MONDAY, mondayActivities);
        tuesdayActivities = completedTodoItems2.stream().filter(completeItem -> completeItem.getCompletedTime().getDayOfWeek().equals(DayOfWeek.TUESDAY))
                .map(tuesdayItem -> TodoMapper.mapContributionActivitiesDto(tuesdayItem)).collect(Collectors.toList());
        oneWeekCompletedTodoItemsMap.put(DayOfWeek.TUESDAY, tuesdayActivities);
        wednesdayActivities = completedTodoItems2.stream().filter(completeItem -> completeItem.getCompletedTime().getDayOfWeek().equals(DayOfWeek.WEDNESDAY))
                .map(wednesdayItem -> TodoMapper.mapContributionActivitiesDto(wednesdayItem)).collect(Collectors.toList());
        oneWeekCompletedTodoItemsMap.put(DayOfWeek.WEDNESDAY, wednesdayActivities);
        thursdayActivities = completedTodoItems2.stream().filter(completeItem -> completeItem.getCompletedTime().getDayOfWeek().equals(DayOfWeek.THURSDAY))
                .map(thursdayItem -> TodoMapper.mapContributionActivitiesDto(thursdayItem)).collect(Collectors.toList());
        oneWeekCompletedTodoItemsMap.put(DayOfWeek.THURSDAY, thursdayActivities);
        fridayActivities = completedTodoItems2.stream().filter(completeItem -> completeItem.getCompletedTime().getDayOfWeek().equals(DayOfWeek.FRIDAY))
                .map(fridayItem -> TodoMapper.mapContributionActivitiesDto(fridayItem)).collect(Collectors.toList());
        oneWeekCompletedTodoItemsMap.put(DayOfWeek.FRIDAY, fridayActivities);
        saturdayActivities = completedTodoItems2.stream().filter(completeItem -> completeItem.getCompletedTime().getDayOfWeek().equals(DayOfWeek.SATURDAY))
                .map(saturdayItem -> TodoMapper.mapContributionActivitiesDto(saturdayItem)).collect(Collectors.toList());
        oneWeekCompletedTodoItemsMap.put(DayOfWeek.SATURDAY, saturdayActivities);
        return oneWeekCompletedTodoItemsMap;
    }
}
