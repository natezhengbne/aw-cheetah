package com.asyncworking.controllers;

import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.dtos.ProjectModificationDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectControllerTest extends ControllerHelper{

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
