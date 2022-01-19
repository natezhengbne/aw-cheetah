package com.asyncworking.controllers;

import com.asyncworking.dtos.EventGetDto;
import com.asyncworking.dtos.EventPostDto;
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
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/events")
    public ResponseEntity<Long> createEvent(@PathVariable Long userId,
                                   @Valid @RequestBody EventPostDto eventPostDto){
        return ResponseEntity.ok(eventService.createEvent(userId, eventPostDto));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventGetDto>> getAllEventForUserByDate(
            @PathVariable Long userId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") OffsetDateTime date){
        if(date == null){
            return ResponseEntity.ok(eventService.getAllEventForUser(userId));
        }
        else{
            return ResponseEntity.ok(eventService.getAllEventForUserByDate(userId, date));
        }

    }
}
