package com.asyncworking.services;

import com.asyncworking.dtos.EventGetDto;
import com.asyncworking.dtos.EventPostDto;
import com.asyncworking.models.Event;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EventRepository;
import com.asyncworking.utility.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    private final UserService userService;

    private final EventMapper eventMapper;

    public Long createEvent(Long userId, EventPostDto eventPostDto) {
        UserEntity user = userService.findUserById(userId);
        Event event = eventMapper.eventPostDtoToEvent(eventPostDto, user);
        eventRepository.save(event);
        return event.getId();
    }

    public List<EventGetDto> getAllEventForUserByDate(Long userId, OffsetDateTime date) {
        UserEntity user = userService.findUserById(userId);
        return user.getEvents().stream()
                .filter(e -> e.isDateMatch(date))
                .map(eventMapper::eventToEventGetDto)
                .collect(Collectors.toList());
    }
}
