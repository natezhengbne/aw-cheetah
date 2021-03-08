package com.asyncworking.controllers;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.services.CompanyService;
import com.asyncworking.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@AutoConfigureMockMvc
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testCreateCompany() throws Exception {
        UserInfoDto userPostInfoDto = UserInfoDto.builder()
                .email("aaa@qq.com")
                .company("AW")
                .title("VI")
                .build();
        doNothing().when(companyService).createCompanyAndEmployee(userPostInfoDto);
        mockMvc.perform(post("/create_company")
                .content(objectMapper.writeValueAsString(userPostInfoDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
