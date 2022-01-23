package com.asyncworking.controllers;

import com.asyncworking.dtos.ContributionActivitiesDto;
import com.asyncworking.services.ContributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/contribution")
@RequiredArgsConstructor
public class ContributionController {
    private final ContributionService contributionService;

    @GetMapping("/company/{companyId}/contributions")
    public ResponseEntity getContributionsTodoItemsCounts(@PathVariable Long companyId, @RequestParam("userId") @NotNull Long userId) {
        log.info("get contributions counts of the current week for Company ID: {}, user ID: {}", companyId, userId);
        Map<DayOfWeek, Integer> oneWeekCompletedTodoItemsCounts = contributionService
                .findOneWeekCompletedTodoItemsCounts(companyId, userId);
        return ResponseEntity.ok(oneWeekCompletedTodoItemsCounts);
    }

    @GetMapping("/company/{companyId}/activities")
    public ResponseEntity getContributionActivitiesTodoItemList(@PathVariable Long companyId,
                                                                @RequestParam("userId") @NotNull Long userId) {
        log.info("get completed tasks of the current week for Company ID: {}, user ID: {}", companyId, userId);
        Map<DayOfWeek, List<ContributionActivitiesDto>> oneWeekCompletedTodoItemsList = contributionService
                .findOneWeekCompletedTodoItemsList(companyId, userId);
        log.info("GetMapping: activities: " + Arrays.asList(oneWeekCompletedTodoItemsList));
        return ResponseEntity.ok(oneWeekCompletedTodoItemsList);
    }
}
