package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.models.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProjectMapper {

    public Project mapProjectDtoToProject(Long companyId, ProjectDto projectDto) {
        return Project.builder()
                .name(projectDto.getName())
                .leaderId(projectDto.getOwnerId())
                .companyId(companyId)
                .projectUsers(new HashSet<>())
                .isDeleted(false)
                .isPrivate(projectDto.isIfPrivate())
                .description(projectDto.getDescription())
                .defaultView(projectDto.getDefaultView())
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    public ProjectInfoDto mapProjectToProjectInfoDto(Project project) {
        return ProjectInfoDto.builder()
                .id(project.getId())
                .leaderId(project.getLeaderId())
                .name(project.getName())
                .description(project.getDescription())
                .projectUserNames(project.getProjectUsers().stream()
                        .map(projectUser -> projectUser.getUserEntity().getName())
                        .collect(Collectors.toList()))
                .doneListId(project.getDoneListId())
                .build();
    }
}
