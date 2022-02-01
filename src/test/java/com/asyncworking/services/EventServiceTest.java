package com.asyncworking.services;

import com.asyncworking.dtos.EventGetDto;
import com.asyncworking.dtos.EventPostDto;
import com.asyncworking.models.Event;
import com.asyncworking.repositories.EventRepository;
import com.asyncworking.utility.DateTimeUtility;
import com.asyncworking.utility.mapper.EventMapper;
import com.asyncworking.utility.mapper.EventMapperImpl;
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
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private DateTimeUtility dateTimeUtility;

    private EventService eventService;
    private EventMapper eventMapper;

    @BeforeEach
    public void setup() {
        eventMapper = new EventMapperImpl();
        eventService = new EventService(eventRepository, eventMapper, dateTimeUtility);
    }

    @Test
    public void testCreateEventWhenGivenProperArgumentsThenSucceed() {
        when(eventRepository.save(any(Event.class))).thenReturn(any());

        Long eventId = eventService.createEvent(1L, 1L, 1L, new EventPostDto());

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void testGetOwnedEventsByDateWhenMatchedEventsFoundThenReturnList() {
        OffsetDateTime dayStartAt = OffsetDateTime.now();
        when(eventRepository.findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of(new Event()));
        when(dateTimeUtility.isPeriodOverlapADay(any(), any(), any()))
                .thenReturn(true);

        List<EventGetDto> eventGetDtoList = eventService.getOwnedEventsByDate(dayStartAt, anyLong(), anyLong(), anyLong());

        verify(eventRepository, times(1)).findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong());
        verify(dateTimeUtility, times(1)).isPeriodOverlapADay(any(), any(), any());
        assertEquals(1, eventGetDtoList.size());
    }

    @Test
    public void testGetOwnedEventsWhenNoMatchedEventFoundThenReturnEmptyList1() {
        OffsetDateTime dayStartAt = OffsetDateTime.now();
        when(eventRepository.findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of(new Event()));
        when(dateTimeUtility.isPeriodOverlapADay(any(), any(), any()))
                .thenReturn(false);

        List<EventGetDto> eventGetDtoList = eventService.getOwnedEventsByDate(dayStartAt, anyLong(), anyLong(), anyLong());

        verify(eventRepository, times(1)).findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong());
        verify(dateTimeUtility, times(1)).isPeriodOverlapADay(any(), any(), any());
        assertEquals(0, eventGetDtoList.size());
    }

    @Test
    public void testGetOwnedEventsWhenNoMatchedEventFoundThenReturnEmptyList2() {
        when(eventRepository.findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of());

        List<EventGetDto> eventGetDtoList = eventService.getOwnedEventsByDate(OffsetDateTime.now(), anyLong(), anyLong(), anyLong());

        verify(eventRepository, times(1)).findByCompanyIdAndProjectIdAndOwnerId(anyLong(), anyLong(), anyLong());
        assertEquals(0, eventGetDtoList.size());
    }
}
