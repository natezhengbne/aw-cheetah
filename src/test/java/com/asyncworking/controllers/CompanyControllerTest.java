package com.asyncworking.controllers;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.CompanyModificationDto;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("aaa@qq.com")
                .name("AW")
                .userTitle("VI")
                .build();
        doNothing().when(companyService).createCompanyAndEmployee(companyModificationDto);
        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void throwBadRequestWhenCompanyNameIsNull() throws Exception {

        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("aaa@qq.com")
                .name("")
                .build();
        doNothing().when(companyService).createCompanyAndEmployee(companyModificationDto);
        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void throwBadRequestWhenAdminEmailInvalid() throws Exception {

        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("aaaqq.com")
                .name("aw")
                .description("desc")
                .build();
        doNothing().when(companyService).createCompanyAndEmployee(companyModificationDto);
        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void prefillDescription() throws Exception {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .companyId(1L)
                .name("aw")
                .description("desc")
                .build();
        when(companyService.fetchCompanyProfileById(1L)).thenReturn(companyModificationDto);
        mockMvc.perform(get("/company/profile")
                .param("companyId", "1"))
                .andExpect(status().isOk());
    }


    @Test
    void updateCompanyDescription() throws Exception {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .companyId(1L)
                .name("aw")
                .description("desc")
                .build();
        doNothing().when(companyService).updateCompany(companyModificationDto);
        mockMvc.perform(put("/company/profile")

                .content(objectMapper.writeValueAsString(companyModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
