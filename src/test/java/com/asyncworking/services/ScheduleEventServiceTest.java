package com.asyncworking.services;

import com.asyncworking.dtos.ScheduleEventGetDto;
import com.asyncworking.dtos.ScheduleEventPostDto;
import com.asyncworking.models.ScheduleEvent;
import com.asyncworking.repositories.ScheduleEventRepository;
import com.asyncworking.utility.DateTimeUtility;
import com.asyncworking.utility.mapper.ScheduleEventMapper;
import com.asyncworking.utility.mapper.ScheduleEventMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleEventServiceTest {

    @Mock
    private ScheduleEventRepository scheduleEventRepository;

    @Mock
    private DateTimeUtility dateTimeUtility;

    private ScheduleEventService scheduleEventService;
    private ScheduleEventMapper scheduleEventMapper;

    @BeforeEach
    public void setup() {
        scheduleEventMapper = new ScheduleEventMapperImpl();
        scheduleEventService = new ScheduleEventService(scheduleEventRepository, scheduleEventMapper, dateTimeUtility);
    }

    @Test
    public void testCreateEventWhenGivenProperArgumentsThenSucceed() {
        when(scheduleEventRepository.save(any(ScheduleEvent.class))).thenReturn(any());

        Long eventId = scheduleEventService.createEvent(1L, 1L, 1L, new ScheduleEventPostDto());

        verify(scheduleEventRepository, times(1)).save(any(ScheduleEvent.class));
    }

    @Test
    public void testGetOwnedEventsByDateWhenMatchedEventsFoundThenReturnList() {
        OffsetDateTime dayStartAt = OffsetDateTime.now();
        when(scheduleEventRepository.findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of(new ScheduleEvent()));
        when(dateTimeUtility.isPeriodOverlapADay(any(), any(), any()))
                .thenReturn(true);

        List<ScheduleEventGetDto> scheduleEventGetDtoList =
                scheduleEventService.getOwnedEventsByDate(dayStartAt, anyLong(), anyLong(), anyLong());

        verify(scheduleEventRepository, times(1))
                .findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong());
        verify(dateTimeUtility, times(1)).isPeriodOverlapADay(any(), any(), any());
        assertEquals(1, scheduleEventGetDtoList.size());
    }

    @Test
    public void testGetOwnedEventsWhenNoMatchedEventFoundThenReturnEmptyList1() {
        OffsetDateTime dayStartAt = OffsetDateTime.now();
        when(scheduleEventRepository.findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of(new ScheduleEvent()));
        when(dateTimeUtility.isPeriodOverlapADay(any(), any(), any()))
                .thenReturn(false);

        List<ScheduleEventGetDto> scheduleEventGetDtoList =
                scheduleEventService.getOwnedEventsByDate(dayStartAt, anyLong(), anyLong(), anyLong());

        verify(scheduleEventRepository, times(1))
                .findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong());
        verify(dateTimeUtility, times(1)).isPeriodOverlapADay(any(), any(), any());
        assertEquals(0, scheduleEventGetDtoList.size());
    }

    @Test
    public void testGetOwnedEventsWhenNoMatchedEventFoundThenReturnEmptyList2() {
        when(scheduleEventRepository.findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of());

        List<ScheduleEventGetDto> scheduleEventGetDtoList =
                scheduleEventService.getOwnedEventsByDate(OffsetDateTime.now(), anyLong(), anyLong(), anyLong());

        verify(scheduleEventRepository, times(1))
                .findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong());
        assertEquals(0, scheduleEventGetDtoList.size());
    }
}
