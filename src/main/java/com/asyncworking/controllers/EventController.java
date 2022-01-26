package com.asyncworking.controllers;

import com.asyncworking.auth.AwcheetahAuthenticationToken;
import com.asyncworking.dtos.EventGetDto;
import com.asyncworking.dtos.EventPostDto;
import com.asyncworking.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/events")
    public ResponseEntity<Long> createEvent(@Valid @RequestBody EventPostDto eventPostDto){
        return ResponseEntity.ok(eventService.createEvent(eventPostDto));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventGetDto>> getEventsForUserByDate(
            @PathVariable Long companyId,
            @PathVariable Long projectId,
            @RequestParam(name = "dayStartAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dayStartTime
    ){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        AwcheetahAuthenticationToken token = (AwcheetahAuthenticationToken)authentication;
        log.info("Current user name: {}, user Id: {}", token.getName(), token.getUserId());
        return ResponseEntity.ok(eventService.getEventsByDate(dayStartTime, token.getUserId(), companyId, projectId));
    }
}
