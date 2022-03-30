package com.asyncworking.services;

import com.asyncworking.dtos.ScheduleEventGetDto;
import com.asyncworking.dtos.ScheduleEventPostDto;
import com.asyncworking.models.ScheduleEvent;
import com.asyncworking.repositories.ScheduleEventRepository;
import com.asyncworking.utility.DateTimeUtility;
import com.asyncworking.utility.mapper.ScheduleEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleEventService {

    private final ScheduleEventRepository scheduleEventRepository;

    private final ScheduleEventMapper scheduleEventMapper;

    private final DateTimeUtility dateTimeUtility;

    public Long createEvent(Long companyId, Long projectId, Long ownerId, ScheduleEventPostDto scheduleEventPostDto) {
        ScheduleEvent scheduleEvent = scheduleEventMapper.eventPostDtoToEvent(companyId, projectId, ownerId, scheduleEventPostDto);
        scheduleEventRepository.save(scheduleEvent);
        return scheduleEvent.getId();
    }

    public List<ScheduleEventGetDto> getOwnedEventsByDate(OffsetDateTime dayStartTime, Long userId, Long projectId, Long companyId) {
        return scheduleEventRepository.findByCompanyIdAndProjectIdAndOwnerId(companyId, projectId, userId).stream()
                .filter(e -> dateTimeUtility.isPeriodOverlapADay(e.getStartTime(), e.getEndTime(), dayStartTime))
                .sorted(Comparator.comparing(ScheduleEvent::getStartTime))
                .map(scheduleEventMapper::eventToEventGetDto)
                .collect(Collectors.toList());
    }
}
