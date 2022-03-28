package com.asyncworking.controllers;

import com.asyncworking.dtos.ScheduleEventPostDto;
import com.asyncworking.jwt.JwtService;
import com.asyncworking.services.ScheduleEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ScheduleEventControllerTest extends ControllerHelper {

    @Mock
    private JwtService jwtService;

    @Mock
    private ScheduleEventService scheduleEventService;

    @InjectMocks
    private ScheduleEventController eventController;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    public void testCreateEventWhenPostDtoGivenThenSucceed() throws Exception {
        ScheduleEventPostDto scheduleEventPostDto = ScheduleEventPostDto.builder().title("Test").build();
        Long ownId = 1L;

        when(jwtService.getUserIdFromJwt(anyString())).thenReturn(ownId);
        when(scheduleEventService.createEvent(1L, 1L, ownId, scheduleEventPostDto)).thenReturn(1L);

        mockMvc.perform(post("/companies/1/projects/1/events")
                .header("Authorization", "auth")
                .content(objectMapper.writeValueAsString(scheduleEventPostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(jwtService, times(1)).getUserIdFromJwt(anyString());
        verify(scheduleEventService, times(1)).createEvent(1L, 1L, ownId, scheduleEventPostDto);
    }

    @Test
    public void testGetEventsForUserByDateWhenPostDtoGivenThenSucceed() throws Exception {
        Long ownId = 1L;
        OffsetDateTime dayStartsAt = OffsetDateTime.now();

        when(jwtService.getUserIdFromJwt(anyString())).thenReturn(ownId);
        when(scheduleEventService.getOwnedEventsByDate(dayStartsAt, ownId, 1L, 1L))
                .thenReturn(anyList());

        mockMvc.perform(get("/companies/1/projects/1/events")
                .header("Authorization", "auth")
                .param("dayStartsAt", dayStartsAt.toString()))
                .andExpect(status().isOk());

        verify(jwtService, times(1)).getUserIdFromJwt(anyString());
        verify(scheduleEventService, times(1)).getOwnedEventsByDate(dayStartsAt, ownId, 1L, 1L);
    }
}
