package com.asyncworking.controllers;

import com.asyncworking.dtos.*;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.EmployeeNotFoundException;
import com.asyncworking.services.CompanyService;
import com.asyncworking.services.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ActiveProfiles("test")

public class CompanyControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompanyService companyService;
    @MockBean
    private ProjectService projectService;

    @Test
    public void testCompanyCreateSuccess() throws Exception {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("aaa@qq.com")
                .name("AW")
                .userTitle("VI")
                .build();
        mockMvc.perform(post("/companies")
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
                MockMvcRequestBuilders.get("/companies/company-info")
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

        mockMvc.perform(post("/companies")
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
        mockMvc.perform(get("/companies/1/profile"))
                .andExpect(status().isOk());
    }

    @Test
    void updateCompanyDescription() throws Exception {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .adminEmail("aaa@qq.com")
                .name("aw")
                .description("desc")
                .build();

        mockMvc.perform(get("/companies/1/profile")
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

        mockMvc.perform(get("/companies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.description").value("hello world"));
    }

    @Test
    public void shouldReturnNotFoundIfCompanyNotExist() throws Exception {
        CompanyNotFoundException error = new CompanyNotFoundException("Can not found company by id: 1");
        when(companyService.findCompanyById(1L)).thenThrow(error);

        String errorMsg = mockMvc.perform(get("/companies/1"))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException().getMessage();

        assertEquals("Can not found company by id: 1", errorMsg);

    }

    @Test
    public void shouldGetEmployeeGivenCompanyId() throws Exception {
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

        mockMvc.perform(get("/companies/1/employees"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundIfNoEmployees() throws Exception {
        EmployeeNotFoundException error = new EmployeeNotFoundException("Can not found employee by company id: 1");
        when(companyService.findAllEmployeeByCompanyId(1L)).thenThrow(error);

        String errorMsg = mockMvc.perform(get("/companies/1/employees"))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException().getMessage();

        assertEquals("Can not found employee by company id: 1", errorMsg);
    }

    @Test
    public void shouldReturnBadRequestWhenDescriptionOverSize() throws Exception {
        String text = "This sentence has 32 characters.";
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .companyId(1L)
                .adminEmail("aaa@qq.com")
                .name("AW")
                .userTitle("VI")
                .description(text.repeat(33))
                .build();
        mockMvc.perform(put("/companies/1/profile")
                .content(objectMapper.writeValueAsString(companyModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        CompanyModificationDto companyModificationDto = CompanyModificationDto.builder()
                .companyId(1L)
                .adminEmail("aaa@qq.com")
                .name("")
                .userTitle("VI")
                .description("aa")
                .build();
        mockMvc.perform(put("/companies/1/profile")
                .content(objectMapper.writeValueAsString(companyModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnEmployeesByCompanyIdAndProjectId() throws Exception {
        mockMvc.perform(get("/companies/1/available-employees")
                .param("projectId", String.valueOf(1L)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfGetProjectInfoListSuccessful() throws Exception {
        Long companyId = 1L;
        Long userId = 1L;
        List<String> projectUserNames = Arrays.asList("+", "-", "*");
        ProjectInfoDto projectInfoDto = ProjectInfoDto.builder()
                .id(1L)
                .name("SSS")
                .projectUserNames(projectUserNames)
                .build();
        when(projectService.fetchAvailableProjectInfoList(companyId, userId)).thenReturn(Arrays.asList(projectInfoDto));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/companies/1/projects")
                        .param("userId", String.valueOf(1L))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }
}
