package com.asyncworking.controllers;

import com.asyncworking.dtos.EventGetDto;
import com.asyncworking.dtos.EventPostDto;
import com.asyncworking.jwt.JwtService;
import com.asyncworking.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Long> createEvent(@Valid @RequestBody EventPostDto eventPostDto){
        return ResponseEntity.ok(eventService.createEvent(eventPostDto));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventGetDto>> getEventsForUserByDate(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long companyId,
            @PathVariable Long projectId,
            @RequestParam(name = "dayStartAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dayStartTime
    ){
        Long userId = jwtService.getUserIdFromToken(auth);
        log.debug("Get Events for user(userId = {}) about project(companyId = {}, projectId = {}) on the day starts at {} ",
                userId, companyId, projectId, dayStartTime);
        return ResponseEntity.ok(eventService.getEventsByDate(dayStartTime, userId, companyId, projectId));
    }
}
