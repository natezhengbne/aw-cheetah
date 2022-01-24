package com.asyncworking.controllers;

import com.asyncworking.dtos.ContributionActivitiesDto;
import com.asyncworking.services.ContributionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContributionControllerTest extends ControllerHelper {
    @Mock
    private ContributionService contributionService;

    private ContributionController contributionController;

    @BeforeEach
    public void setUp() {
        super.setUp();
        contributionController = new ContributionController(contributionService);
        mockMvc = MockMvcBuilders.standaloneSetup(
                controllerExceptionHandler,
                contributionController
        ).build();
    }

    @Test
    public void shouldGetContributionsTodoItemsCounts() throws Exception {
        Long userId = 1L;

        Map<DayOfWeek, Integer> oneWeekCompletedTodoItemsCounts = new LinkedHashMap<>();
        oneWeekCompletedTodoItemsCounts.put(DayOfWeek.SUNDAY, 2);
        oneWeekCompletedTodoItemsCounts.put(DayOfWeek.MONDAY, 3);
        oneWeekCompletedTodoItemsCounts.put(DayOfWeek.TUESDAY, 4);
        oneWeekCompletedTodoItemsCounts.put(DayOfWeek.WEDNESDAY, 5);
        oneWeekCompletedTodoItemsCounts.put(DayOfWeek.THURSDAY, 6);
        oneWeekCompletedTodoItemsCounts.put(DayOfWeek.FRIDAY, 7);
        oneWeekCompletedTodoItemsCounts.put(DayOfWeek.SATURDAY, 8);

        when(contributionService.findOneWeekCompletedTodoItemsCounts(1L, 1L)).thenReturn(oneWeekCompletedTodoItemsCounts);

        mockMvc.perform(get("/contribution/company/1/contributions")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("SUNDAY").value(2));
    }

    @Test
    public void shouldGetContributionsTodoItemsList() throws Exception {
        Long userId = 1L;
        Map<DayOfWeek, List<ContributionActivitiesDto>> oneWeekCompletedTodoItemsList = new LinkedHashMap<>();
        OffsetDateTime offsetDT = OffsetDateTime.now();
        ContributionActivitiesDto contributionActivitiesDto = ContributionActivitiesDto.builder()
                .taskName("Enhance a batter guide user experience for user")
                .dueDate(offsetDT)
                .build();
        List<ContributionActivitiesDto> contributionActivityList = new ArrayList<>();
        contributionActivityList.add(contributionActivitiesDto);
        oneWeekCompletedTodoItemsList.put(DayOfWeek.SUNDAY,
                contributionActivityList);
        oneWeekCompletedTodoItemsList.put(DayOfWeek.MONDAY,
                contributionActivityList);
        oneWeekCompletedTodoItemsList.put(DayOfWeek.TUESDAY,
                contributionActivityList);
        oneWeekCompletedTodoItemsList.put(DayOfWeek.WEDNESDAY,
                contributionActivityList);
        oneWeekCompletedTodoItemsList.put(DayOfWeek.THURSDAY,
                contributionActivityList);
        oneWeekCompletedTodoItemsList.put(DayOfWeek.FRIDAY,
                contributionActivityList);
        oneWeekCompletedTodoItemsList.put(DayOfWeek.SATURDAY,
                contributionActivityList);

        when(contributionService.findOneWeekCompletedTodoItemsList(1L, 1L)).thenReturn(oneWeekCompletedTodoItemsList);
        mockMvc.perform(get("/contribution/company/1/activities")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("SATURDAY").isNotEmpty());
    }
}
