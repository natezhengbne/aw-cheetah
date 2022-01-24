package com.asyncworking.services;

import com.asyncworking.dtos.ContributionActivitiesDto;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoItem;
import com.asyncworking.models.TodoList;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.utility.mapper.TodoItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContributionServiceTest {
    @Mock
    private TodoItemRepository todoItemRepository;

    private ContributionService contributionService;

    private TodoItemMapper todoItemMapper;

    @BeforeEach()
    public void setup() {
        todoItemMapper = new TodoItemMapper();
        contributionService = new ContributionService(
                todoItemRepository,
                todoItemMapper
        );
    }

    @Test
    public void shouldReturnOneWeekCompletedTodoItemsCounts() {
        when(todoItemRepository.countByCompanyIdAndSubscribersIdsIsContainingAndCompletedTimeBetween
                (eq(1L), eq("1"), any(OffsetDateTime.class), any(OffsetDateTime.class))).thenReturn(6);

        Map<DayOfWeek, Integer> oneWeekCompletedTodoItemsCounts = contributionService.findOneWeekCompletedTodoItemsCounts(1L, 1L);

        assertEquals(7, oneWeekCompletedTodoItemsCounts.size());
        assertEquals(6, oneWeekCompletedTodoItemsCounts.get(DayOfWeek.MONDAY));
    }

    @Test
    public void shouldReturnOneWeekCompletedTodoItemsList() {
        Project mockProject = Project.builder().name("title").build();
        TodoList mockTodoList = TodoList.builder().project(mockProject).build();
        OffsetDateTime testDate = OffsetDateTime.of(2022, 1, 24, 6, 30, 40, 50000, ZoneOffset.UTC);
        TodoItem todoItem = TodoItem.builder()
                .id(1L)
                .description("desc")
                .todoList(mockTodoList)
                .priority("Low")
                .subscribersIds("1,3,4,9,10")
                .completedTime(testDate)
                .dueDate(testDate).build();
        when(todoItemRepository.findByCompanyIdAndSubscribersIdsIsContainingAndCompletedTimeBetween(
                eq(1L), eq("1"), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(Arrays.asList(todoItem));
        Map<DayOfWeek, List<ContributionActivitiesDto>> oneWeekCompletedTodoItemsList = contributionService
                .findOneWeekCompletedTodoItemsList(1L, 1L);

        assertEquals(7, oneWeekCompletedTodoItemsList.size());
        assertEquals(Arrays.asList(new ContributionActivitiesDto("desc", testDate)),
                oneWeekCompletedTodoItemsList.get(testDate.getDayOfWeek()));
    }
}
