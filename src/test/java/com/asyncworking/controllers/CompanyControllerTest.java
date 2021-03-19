package com.asyncworking.controllers;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.services.CompanyService;
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
    public void testCompanyCreateSuccess() throws Exception {

        CompanyInfoDto companyInfoDto = CompanyInfoDto.builder()
                .companyId(1L)
                .adminEmail("aaa@qq.com")
                .name("AW")
                .userTitle("VI")
                .build();
        doNothing().when(companyService).createCompanyAndEmployee(companyInfoDto);
        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyInfoDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void throwBadRequestWhenCompanyNameIsNull() throws Exception {

        CompanyInfoDto companyInfoDto = CompanyInfoDto.builder()
                .adminEmail("aaa@qq.com")
                .name("")
                .build();
        doNothing().when(companyService).createCompanyAndEmployee(companyInfoDto);
        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyInfoDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void throwBadRequestWhenAdminEmailIsNull() throws Exception {

        CompanyInfoDto companyInfoDto = CompanyInfoDto.builder()
                .adminEmail("")
                .name("AW")
                .build();
        doNothing().when(companyService).createCompanyAndEmployee(companyInfoDto);
        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyInfoDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void throwBadRequestWhenAdminEmailInvalid() throws Exception {

        CompanyInfoDto companyInfoDto = CompanyInfoDto.builder()
                .adminEmail("aaaqq.com")
                .name("aw")
                .build();
        doNothing().when(companyService).createCompanyAndEmployee(companyInfoDto);
        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyInfoDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
