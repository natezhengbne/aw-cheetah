package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.models.Project;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    public Project mapProjectDtoToProject(ProjectDto projectDto) {
        return Project.builder()
                .name(projectDto.getName())
                .leaderId(projectDto.getOwnerId())
                .companyId(projectDto.getCompanyId())
                .projectUsers(new HashSet<>())
                .isDeleted(false)
                .isPrivate(false)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    public ProjectInfoDto mapProjectToProjectInfoDto(Project project) {
        return ProjectInfoDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .projectUserNames(project.getProjectUsers().stream()
                        .map(projectUser -> projectUser.getUserEntity().getName())
                        .collect(Collectors.toList()))
                .build();
    }
}
