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

    private final EventMapper eventMapper;

    public Long createEvent(EventPostDto eventPostDto) {
        Event event = eventMapper.eventPostDtoToEvent(eventPostDto);
        eventRepository.save(event);
        return event.getId();
    }

    public List<EventGetDto> getEventsByDate(OffsetDateTime dayStartTime, Long userId, Long companyId, Long projectId) {
        return eventRepository.findByCompanyIdAndProjectId(companyId, projectId).stream()
                .filter(e -> e.isWithinDay(dayStartTime))
                .filter(e -> e.getParticipants().stream()
                        .map(UserEntity::getId)
                        .anyMatch(userId::equals))
                .map(eventMapper::eventToEventGetDto)
                .collect(Collectors.toList());
    }
}
