package com.asyncworking.controllers;

import com.asyncworking.dtos.ContributionActivitiesDto;
import com.asyncworking.jwt.JwtService;
import com.asyncworking.services.ContributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/companies/{companyId}")
@RequiredArgsConstructor
public class ContributionController {
    private final ContributionService contributionService;
    private final JwtService jwtService;

    @GetMapping("/contributions/activities")
    public ResponseEntity<Map<DayOfWeek, List<ContributionActivitiesDto>>> getContributionActivitiesTodoItemList(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long companyId) {
        Long userId = jwtService.getUserIdFromToken(auth);
        log.info("get completed tasks of the current week for Company ID: {}, user ID: {}", companyId, userId);
        Map<DayOfWeek, List<ContributionActivitiesDto>> oneWeekCompletedTodoItemsList = contributionService
                .findOneWeekCompletedTodoItemsList(companyId, userId);
        log.debug("GetMapping: activities: {} ", Arrays.asList(oneWeekCompletedTodoItemsList));
        return ResponseEntity.ok(oneWeekCompletedTodoItemsList);
    }
}
