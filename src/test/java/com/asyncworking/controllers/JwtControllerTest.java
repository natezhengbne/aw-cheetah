package com.asyncworking.controllers;

import com.asyncworking.jwt.JwtController;
import com.asyncworking.jwt.JwtDto;
import com.asyncworking.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JwtControllerTest extends ControllerHelper {

    @Mock
    private JwtService jwtService;

    private JwtController jwtController;

    private JwtDto jwtDto;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        jwtDto = JwtDto.builder().build();
        jwtController = new JwtController(jwtService);
        mockMvc = MockMvcBuilders.standaloneSetup(jwtController).build();
    }

    @Test
    public void shouldReturnJwtDto() throws Exception {
        when(jwtService.refreshJwtToken("auth")).thenReturn(jwtDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/refresh")
                        .header("Authorization", "auth"))
                .andExpect(status().isOk());
    }
}
