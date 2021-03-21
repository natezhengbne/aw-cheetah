package com.asyncworking.controllers;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.AccountDto;
import com.asyncworking.services.CompanyService;
import com.asyncworking.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @MockBean
    private CompanyService companyService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldLoginSuccessful() throws Exception {
        Authentication mocked = Mockito.mock(Authentication.class);
        when(mocked.isAuthenticated()).thenReturn(true);

        String inputJson = "{\"email\": \"lengary@asyncworking.com\", \"password\":\"len1234567\"}";
        MvcResult mvcResult = mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson)).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void shouldReturnNonAuthoritativeInformationWhenUnverifiedLogin() throws Exception{
        String email = "a@gmail.com";
        when(userService.ifUnverified(email)).thenReturn(true);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/login")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNonAuthoritativeInformation());
    }

    @Test
    public void shouldReturnOkWhenEmailNotUnverifiedForLogin() throws Exception {
        String email = "a@gmail.com";
        when(userService.ifUnverified(email)).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/login")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    public void shouldReturnBadRequestWhenEmailNotValidForLogin() throws Exception {
        Authentication mocked = Mockito.mock(Authentication.class);
        when(mocked.isAuthenticated()).thenReturn(true);

        String inputJson = "{\"email\": \"lengaasyncworking.com\", \"password\":\"len1234567\"}";
        MvcResult mvcResult = mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson)).andReturn();

        assertEquals(400, mvcResult.getResponse().getStatus());
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
    public void shouldReturnBadRequestIfParamNotProvided() throws Exception {
        String email = "a@gmail.com";
        when(userService.ifEmailExists(email)).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateUserAndGenerateLinkSuccessful() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .name("aaa")
                .email("aaa@qq.com")
                .password("aaaaaaaa1")
                .build();

        doNothing().when(userService).createUserAndGenerateVerifyLink(accountDto, "http://localhost");
        mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestWhenEmailIsNotValidForSignup() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .name("aaa")
                .email("aaaqq.com")
                .password("aaaaaaaa1")
                .build();

        doNothing().when(userService).createUserAndGenerateVerifyLink(accountDto, "http://localhost");
        mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenPasswordIsNotValidForSignup() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .name("aaa")
                .email("aaa@qq.com")
                .password("aaaaaa")
                .build();

        doNothing().when(userService).createUserAndGenerateVerifyLink(accountDto, "http://localhost");
        mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldResendActivationLinkSuccessful() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .name("aaa")
                .email("aaa@qq.com")
                .password("aaaaaaaa1")
                .build();

        doNothing().when(userService).createUserAndGenerateVerifyLink(accountDto, "http://localhost");
        mockMvc.perform(post("/resend")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestWhenEmailIsNotValidForResend() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .name("aaa")
                .email("aaaqq.com")
                .password("aaaaaaaa1")
                .build();

        doNothing().when(userService).createUserAndGenerateVerifyLink(accountDto, "http://localhost");
        mockMvc.perform(post("/resend")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenPasswordIsNotValidForResend() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .name("aaa")
                .email("aaa@qq.com")
                .password("aaaaaa")
                .build();

        doNothing().when(userService).createUserAndGenerateVerifyLink(accountDto, "http://localhost");
        mockMvc.perform(post("/resend")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRedirectGivenVerifyAccountAndActiveUserSuccessful() throws Exception {
        String code = "xxxxxxx";
        when(userService.isAccountActivated(code)).thenReturn(true);
        URI redirectPage = new URI("http://localhost:3001?verify=true");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectPage);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/verify")
                        .param("code", code)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is3xxRedirection());
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
    public void shouldReturnNoContentIfCompanyNotExist() throws Exception {
        String email = "a@gmail.com";
        when(userService.ifCompanyExits(email)).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/company")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnBadRequestIfParamNotExist() throws Exception {
        String email = "a@gmail.com";
        when(userService.ifCompanyExits(email)).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/company")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

}

