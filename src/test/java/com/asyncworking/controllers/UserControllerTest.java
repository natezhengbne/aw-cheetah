package com.asyncworking.controllers;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldLoginSuccessful() throws Exception {
        Authentication mocked = Mockito.mock(Authentication.class);
        when(mocked.isAuthenticated()).thenReturn(true);
        when(userService.login(anyString(), anyString())).thenReturn(mocked);

        String inputJson = "{\"email\": \"lengary@asyncworking.com\", \"password\":\"len123\"}";
        MvcResult mvcResult = mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson)).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void shouldValidEmailExist () throws Exception {
        UserInfoDto userFE = UserInfoDto.builder()
                .email("a@gmail.com")
                .build();

        when(userService.isEmailExist(userFE.getEmail())).thenReturn(true);

        String inputJson = "{\"email\": \"a@gmail.com\"}";
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson)).andReturn();

        assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    public void shouldValidEmailNoExist () throws Exception {
        UserInfoDto userFE = UserInfoDto.builder()
                .email("a@gmail.com")
                .build();

        when(userService.isEmailExist(userFE.getEmail())).thenReturn(false);

        String inputJson = "{\"email\": \"b@gmail.com\"}";
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson)).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}

