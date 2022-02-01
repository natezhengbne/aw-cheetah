package com.asyncworking.controllers;

import com.asyncworking.dtos.EventPostDto;
import com.asyncworking.jwt.JwtService;
import com.asyncworking.services.EventService;
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

public class EventControllerTest extends ControllerHelper {

    @Mock
    private JwtService jwtService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    public void testCreateEventWhenPostDtoGivenThenSucceed() throws Exception {
        EventPostDto eventPostDto = EventPostDto.builder().title("Test").build();
        Long ownId = 1L;

        when(jwtService.getUserIdFromToken(anyString())).thenReturn(ownId);
        when(eventService.createEvent(1L, 1L, ownId, eventPostDto)).thenReturn(1L);

        mockMvc.perform(post("/companies/1/projects/1/events")
                .header("Authorization", "auth")
                .content(objectMapper.writeValueAsString(eventPostDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(jwtService, times(1)).getUserIdFromToken(anyString());
        verify(eventService, times(1)).createEvent(1L, 1L, ownId, eventPostDto);
    }

    @Test
    public void testGetEventsForUserByDateWhenPostDtoGivenThenSucceed() throws Exception {
        Long ownId = 1L;
        OffsetDateTime dayStartsAt = OffsetDateTime.now();

        when(jwtService.getUserIdFromToken(anyString())).thenReturn(ownId);
        when(eventService.getOwnedEventsByDate(dayStartsAt, ownId, 1L, 1L))
                .thenReturn(anyList());

        mockMvc.perform(get("/companies/1/projects/1/events")
                .header("Authorization", "auth")
                .param("dayStartsAt", dayStartsAt.toString()))
                .andExpect(status().isOk());

        verify(jwtService, times(1)).getUserIdFromToken(anyString());
        verify(eventService, times(1)).getOwnedEventsByDate(dayStartsAt, ownId, 1L, 1L);
    }
}
