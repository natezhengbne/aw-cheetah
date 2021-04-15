package com.asyncworking.controllers;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.services.ProjectService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testProjectCreateSuccess() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .name("sss@qq.com")
                .ownerId(1L)
                .companyId(1L)
                .build();
        mockMvc.perform(post("/project")
                .content(objectMapper.writeValueAsString(projectDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void throwBadRequestWhenProjectNameIsNull() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .name("")
                .ownerId(1L)
                .companyId(1L)
                .build();
        mockMvc.perform(post("/project")
                .content(objectMapper.writeValueAsString(projectDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void throwBadRequestWhenCompanyIdIsEmpty() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .name("")
                .ownerId(1L)
                .build();
        mockMvc.perform(post("/project")
                .content(objectMapper.writeValueAsString(projectDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void throwBadRequestWhenOwnerIdIsEmpty() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .name("")
                .companyId(1L)
                .build();
        mockMvc.perform(post("/project")
                .content(objectMapper.writeValueAsString(projectDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnOkIfGetProjectInfoListSuccessful() throws Exception {
        Long companyId = 1L;
        List<String> projectUserNames = Arrays.asList("+", "-", "*");
        ProjectInfoDto projectInfoDto = ProjectInfoDto.builder()
                .id(1L)
                .name("SSS")
                .projectUserNames(projectUserNames)
                .build();
        when(projectService.fetchProjectInfoListByCompanyId(companyId)).thenReturn(Arrays.asList(projectInfoDto));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/project")
                        .param("companyId", String.valueOf(companyId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }
}
