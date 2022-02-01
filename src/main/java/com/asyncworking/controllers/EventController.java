package com.asyncworking.controllers;

import com.asyncworking.dtos.EventGetDto;
import com.asyncworking.dtos.EventPostDto;
import com.asyncworking.jwt.JwtService;
import com.asyncworking.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("companies/{companyId}/projects/{projectId}")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final JwtService jwtService;

    @PostMapping("/events")
    public ResponseEntity<Long> createEvent(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long companyId,
            @PathVariable Long projectId,
            @Valid @RequestBody EventPostDto eventPostDto) {
        Long userId = jwtService.getUserIdFromToken(auth);
        log.debug("Create Event for user(userId = {}) about project(companyId = {}, projectId = {})", userId, companyId, projectId);
        return ResponseEntity.ok(eventService.createEvent(companyId, projectId, userId, eventPostDto));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventGetDto>> getEventsForUserByDate(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long companyId,
            @PathVariable Long projectId,
            @RequestParam(name = "dayStartAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dayStartTime
    ) {
        Long userId = jwtService.getUserIdFromToken(auth);
        log.debug("Get Events for user(userId = {}) about project(companyId = {}, projectId = {}) on the day starts at {} ",
                userId, companyId, projectId, dayStartTime);
        return ResponseEntity.ok(eventService.getOwnedEventsByDate(dayStartTime, userId, projectId, companyId));
    }
}
