package com.asyncworking.controllers;

import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.InvitedAccountPostDto;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.LinkGenerator;
import com.asyncworking.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.util.Date;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerHelper{
    @Mock
    private UserService userService;

    @Mock
    private LinkGenerator linkGenerator;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void shouldReturnNonAuthoritativeInformationWhenUnverifiedLogin() throws Exception {
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

        mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateNewUserViaInvitationsRegisterSuccessfully() throws Exception {
        InvitedAccountPostDto accountDto = InvitedAccountPostDto.builder()
                .name("Steven S Wang")
                .email("skykk0128@gmail.com")
                .password("password12345")
                .title("Dev")
                .companyId(1L)
                .build();

        mockMvc.perform(post("/invitations/register")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDecodeAndGetUserInfoSuccessfully() throws Exception {
        String code = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnZpdGF0aW9uIiwiY29tcGFue" +
                "UlkIjoxLCJlbWFpbCI6InVzZXIxQGdtYWlsLmNvbSIsIm5hbWUiOiJ1c2VyMSIsI" +
                "nRpdGxlIjoiZGV2ZWxvcGVyIn0.FsfFrxlLeCjcSBV1cWp6D_VstygnaSr9EWSqZKKX1dU";

        mockMvc.perform(get("/invitations/info")
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestWhenEmailIsNotValidForSignup() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .name("aaa")
                .email("aaaqq.com")
                .password("aaaaaaaa1")
                .build();

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

        mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldResendActivationLinkSuccessful() throws Exception {
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .name("aaa")
                .email("aaa@qq.com")
                .password("aaaaaaaa1")
                .build();

        mockMvc.perform(post("/resend")
                .content(objectMapper.writeValueAsString(userInfoDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestWhenEmailIsNotValidForResend() throws Exception {
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .name("aaa")
                .email("aaaqq.com")
                .password("aaaaaaaa1")
                .build();

        mockMvc.perform(post("/resend")
                .content(objectMapper.writeValueAsString(userInfoDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRedirectGivenVerifyAccountAndActiveUserSuccessful() throws Exception {
        String code = "xxxxxxx";
        when(userService.isAccountActivated(code)).thenReturn(true);
        URI redirectPage = new URI("http://localhost:3001/verifylink?verify=true");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectPage);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/verify")
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
        mockMvc.perform(
                MockMvcRequestBuilders.get("/company")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnOkIfEmailIsValid() throws Exception {
        String email = "test@gmail.com";

        mockMvc.perform(post("/password")
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOk() throws Exception {
        String email = "test@gmail.com";
        doNothing().when(userService).sendPasswordResetEmail(email);

        mockMvc.perform(post("/password")
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkWhenDecodeSuccessfully() throws Exception {
        String code = "code";
        when(userService.getResetterInfo(code)).thenReturn(null);

        mockMvc.perform(get("/password-reset/info")
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnSuccessfulWhenPasswordReset() throws Exception {
        UserInfoDto accountDto = UserInfoDto.builder()
                .id(1L)
                .name("aaa")
                .email("aaa@qq.com")
                .password("aaaaaa")
                .title("title")
                .accessToken("code")
                .build();

        mockMvc.perform(put("/password-reset")
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnSuccessWhenUserAcceptCompanyInvitation() throws Exception {
        String code = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjb21wYW55SW52aXRhdGlvbiIsImNvbX" +
                "BhbnlJZCI6MzcsImVtYWlsIjoiZXJpYzguMTVAaG90bWFpbC5jb20iLCJuYW1lIjoiZXJ" +
                "pYyIsInRpdGxlIjoiMjIyIiwiZGF0ZSI6Ik1hciA1LCAyMDIyLCA5OjAxOjQxIEFNIiwi" +
                "aWF0IjoxNjQ2Mzg0NTAxLCJleHAiOjE2NDY0NzA5MDF9.VMzs7iwY3KlYCnNGOT_5EY0o" +
                "seWFwzzS0l3s4SKSwdnMJ-G_LVL8jGY27tr5hpJvNodrcliqP3TpEBZQZDMNcg";

        when(userService.isCompanyInvitationSuccess(code)).thenReturn("1");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/accept-company-invitation")
                                .param("code", code)
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

    }

    @Test
    public void shouldCreateInvitationLinkSuccessful() throws Exception {
        Long companyId = 1L;
        String title = "developer";
        String name = "user1";
        String email = "user1@gmail.com";

        mockMvc.perform(get("/invitations/companies")
                .param("companyId", String.valueOf(companyId))
                .param("title", title)
                .param("name", name)
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }
}

