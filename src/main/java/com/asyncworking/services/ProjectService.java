package com.asyncworking.services;

import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final ProjectUserRepository projectUserRepository;

    public List<ProjectInfoDto> fetchProjectInfoListByCompanyId(Long companyId) {
        if (projectRepository.findProjectIdsByCompanyId(companyId).isEmpty()) {
            throw new ProjectNotFoundException("Can not found project by companyId:" + companyId);
        } else {
            List<Long> projectIdList = projectRepository.findProjectIdsByCompanyId(companyId);
            List<ProjectInfoDto> projectInfoDtoList = new LinkedList<>();
            projectIdList.forEach(projectId -> {
                List<String> newProjectUserNames = projectRepository.findNamesByProjectId(projectId);
                IProjectInfo iProjectInfo = projectRepository.findProjectInfoByProjectId(projectId).get();
                projectInfoDtoList.add(mapProjectInfoToProjectInfoDto(projectId,
                        iProjectInfo.getName(), newProjectUserNames));
            });
            return projectInfoDtoList;
        }
    }
    private ProjectInfoDto mapProjectInfoToProjectInfoDto(Long projectId,
                                                          String projectName, List<String> projectUserNames) {
        return ProjectInfoDto.builder()
                .id(projectId)
                .name(projectName)
                .projectUserNames(projectUserNames)
                .build();
    }

    @Transactional
    public Long createProjectAndProjectUser(ProjectDto projectDto) {

        UserEntity selectedUserEntity = fetchUserEntityById(projectDto.getOwnerId());
        log.info("selectedUser's id" + selectedUserEntity.getId());
        Project newProject = createProject(projectDto);

        projectRepository.save(newProject);

        ProjectUser newProjectUser = createProjectUser
                (new ProjectUserId(selectedUserEntity.getId(), newProject.getId()),
                        selectedUserEntity,
                        newProject);
        projectUserRepository.save(newProjectUser);
        return newProject.getId();
    }

    private UserEntity fetchUserEntityById(Long userId) {
        return userRepository.findUserEntityById(userId)
                .orElseThrow(() -> new UserNotFoundException("Can not found user by userId:" + userId));
    }

    private Project createProject(ProjectDto projectDto) {
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

    private ProjectUser createProjectUser(ProjectUserId projectUserId, UserEntity userEntity, Project project) {
        return ProjectUser.builder()
                .id(projectUserId)
                .attended(true)
                .project(project)
                .userEntity(userEntity)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }
}
