package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.models.Project;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;

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
}
