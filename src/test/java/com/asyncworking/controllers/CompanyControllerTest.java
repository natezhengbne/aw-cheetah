package com.asyncworking.controllers;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.*;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.EmployeeNotFoundException;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfGetCompanyInfoSuccessful() throws Exception {
        String email = "kkk@gmail.com";
        List<String> colleagueList = Arrays.asList("+", "-", "*");
        CompanyColleagueDto companyInfoDto = CompanyColleagueDto.builder()
                .companyId(1L)
                .name("+company")
                .description("description for + company")
                .colleague(colleagueList)
                .build();
        when(companyService.getCompanyInfoDto(email)).thenReturn(companyInfoDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/companyinfo")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    public void throwBadRequestWhenCompanyNameIsNull() throws Exception {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("aaa@qq.com")
                .name("")
                .build();

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
                .adminEmail("aaa@qq.com")
                .name("aw")
                .description("desc")
                .build();

        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetCompanyInfoDtoGivenId() throws Exception {

        CompanyInfoDto companyInfoDto = CompanyInfoDto.builder()
                .name("Apple")
                .description("hello world")
                .build();
        when(companyService.findCompanyById(1L)).thenReturn(companyInfoDto);

        mockMvc.perform(get("/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.description").value("hello world"));
    }

    @Test
    public void shouldReturnNotFoundIfCompanyNotExist() throws Exception {
        CompanyNotFoundException error =  new CompanyNotFoundException("Can not found company by id: 1");
        when(companyService.findCompanyById(1L)).thenThrow(error);

        String errorMsg = mockMvc.perform(get("/company/1"))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException().getMessage();

        assertEquals("Can not found company by id: 1", errorMsg);

    }

    @Test
    public void shouldGetEmployeeGivenCompanyId() throws Exception{
        EmployeeGetDto mockEmployee1 = EmployeeGetDto.builder()
                .email("xxx@gmail.com")
                .name("lydia")
                .title("frontend developer")
                .build();

        EmployeeGetDto mockEmployee2 = EmployeeGetDto.builder()
                .email("yyy@gmail.com")
                .name("leo")
                .title("backend developer")
                .build();

        List<EmployeeGetDto> employees = new ArrayList<>();

        employees.add(mockEmployee1);
        employees.add(mockEmployee2);

        when(companyService.findAllEmployeeByCompanyId(1L)).thenReturn(employees);

        mockMvc.perform(get("/employee/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundIfNoEmployees() throws Exception {
        EmployeeNotFoundException error =  new EmployeeNotFoundException("Can not found employee by company id: 1");
        when(companyService.findAllEmployeeByCompanyId(1L)).thenThrow(error);

        String errorMsg = mockMvc.perform(get("/employee/1"))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException().getMessage();

        assertEquals("Can not found employee by company id: 1", errorMsg);
    }
}
