package com.asyncworking.controller;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testCreateUser() throws Exception {

        UserInfoDto userPostInfoDto = UserInfoDto.builder()
                .name("aaa")
                .email("aaa@qq.com")
                .password("aaaaaaaa1")
                .build();
        UserInfoDto userGetInfoDto = UserInfoDto.builder()
                .name("aaa")
                .email("aaa@qq.com")
                .build();

        BDDMockito.given(userService.createUser(userPostInfoDto)).willReturn(userGetInfoDto);
        mockMvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(userPostInfoDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").value("aaa"))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.email").value("aaa@qq.com"));
    }
}
