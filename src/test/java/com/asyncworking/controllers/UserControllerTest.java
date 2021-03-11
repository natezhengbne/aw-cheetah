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

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        String inputJson = "{\"email\": \"lengary@asyncworking.com\", \"password\":\"len123\"}";
        MvcResult mvcResult = mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson)).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void shouldReturnErrorIfEmailExists() throws Exception {
        String email = "a@gmail.com";
        when(userService.ifEmailExists(email)).thenReturn(true);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/signup")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturnOkIfEmailNotExist() throws Exception {
        String email = "a@gmail.com";
        when(userService.ifEmailExists(email)).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/signup")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateUserAndGenerateLinkSuccessful() throws Exception {
        UserInfoDto userPostInfoDto = UserInfoDto.builder()
                .name("aaa")
                .email("aaa@qq.com")
                .password("aaaaaaaa1")
                .build();

        HttpServletRequest  mockedRequest = Mockito.mock(HttpServletRequest.class);

        doNothing().when(userService).createUserAndGenerateVerifyLink(userPostInfoDto, "http://localhost");
        mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(userPostInfoDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldVerifyAccountAndActiveUserSuccessful() throws Exception {
        String code = "xxxxxxx";
        doNothing().when(userService).verifyAccountAndActiveUser(code);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/verify")
                        .param("code", code)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfCompanyExists() throws Exception {
        String email = "kkk@gmail.com";
        when(userService.ifCompanyExits(email)).thenReturn(true);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/company")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnErrorIfCompanyNotExist() throws Exception {
        String email = "a@gmail.com";
        when(userService.ifCompanyExits(email)).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/company")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }
}

