package com.asyncworking.controllers;

import com.asyncworking.config.SpringSecurityWebAuxTestConfig;
import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.dtos.ProjectModificationDto;
import com.asyncworking.services.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityWebAuxTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ProjectControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    public void testProjectCreateSuccess() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .name("sss@qq.com")
                .ownerId(1L)
                .companyId(1L)
                .build();
        mockMvc.perform(post("/companies/1/projects")
                .content(objectMapper.writeValueAsString(projectDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void throwBadRequestWhenProjectNameIsEmpty() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .name("  ")
                .ownerId(1L)
                .companyId(1L)
                .build();
        mockMvc.perform(post("/companies/1/projects")
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
        mockMvc.perform(post("/companies/1/projects")
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
        mockMvc.perform(post("/companies/1/projects")
                .content(objectMapper.writeValueAsString(projectDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnOkIfDisplayProjectInfoSuccessful() throws Exception {
        Long companyId = 1L;
        List<String> projectUserNames = Arrays.asList("+", "-", "*");
        ProjectInfoDto projectInfoDto = ProjectInfoDto.builder()
                .id(1L)
                .name("SSS")
                .projectUserNames(projectUserNames)
                .build();
        when(projectService.fetchProjectInfoByProjectIdAndCompanyId(1L, 1L)).thenReturn(projectInfoDto);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/companies/1/projects/1/project-info")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("project manager")
    public void shouldReturnOkIfUpdateProjectInfoSuccessful() throws Exception {
        ProjectModificationDto projectModificationDto = ProjectModificationDto.builder()
                .projectId(1L)
                .name("aw-3")
                .description("desc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/project-info")
                .content(objectMapper.writeValueAsString(projectModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void throwBadRequestIfUpdateProjectNameIsEmpty() throws Exception {
        ProjectModificationDto projectModificationDto = ProjectModificationDto.builder()
                .projectId(1L)
                .name("   ")
                .description("desc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/companies/1/projects/1/project-info")
                .content(objectMapper.writeValueAsString(projectModificationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnOkIfGetMembersByProjectId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/companies/1/projects/1/members")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfCreateMembersByProjectIdAndUserId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/companies/1/projects/1/members")
                .param("userIds", "1,2"))
                .andExpect(status().isOk());
    }
}
