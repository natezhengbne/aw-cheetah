package com.asyncworking.services;

import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.dtos.ProjectModificationDto;
import com.asyncworking.exceptions.EmployeeNotFoundException;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.*;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final ProjectUserRepository projectUserRepository;

    private final ProjectMapper projectMapper;

    private final EmployeeMapper employeeMapper;

    public ProjectInfoDto fetchProjectInfoByProjectId(Long projectId) {
        if (projectRepository.findProjectByProjectId(projectId).isEmpty()) {
            throw new ProjectNotFoundException("Can not find project by projectId:" + projectId);
        } else {
            List<String> newProjectUserNames = projectRepository.findNamesByProjectId(projectId);
            IProjectInfo iProjectInfo = projectRepository.findProjectInfoByProjectId(projectId).get();
            ProjectInfoDto projectInfoDto = mapProjectInfoToProjectInfoDto(projectId,
                    iProjectInfo.getName(), iProjectInfo.getDescription(), newProjectUserNames);
            return projectInfoDto;
        }
    }

    public List<ProjectInfoDto> fetchProjectInfoListByCompanyId(Long companyId) {
        if (projectRepository.findProjectIdsByCompanyId(companyId).isEmpty()) {
            throw new ProjectNotFoundException("Can not find project by companyId:" + companyId);
        } else {
            List<Long> projectIdList = projectRepository.findProjectIdsByCompanyId(companyId);
            List<ProjectInfoDto> projectInfoDtoList = new LinkedList<>();
            projectIdList.forEach(projectId -> {
                List<String> newProjectUserNames = projectRepository.findNamesByProjectId(projectId);
                IProjectInfo iProjectInfo = projectRepository.findProjectInfoByProjectId(projectId).get();
                projectInfoDtoList.add(mapProjectInfoToProjectInfoDto(projectId,
                        iProjectInfo.getName(), iProjectInfo.getDescription(), newProjectUserNames));
            });
            return projectInfoDtoList;
        }
    }

    private ProjectInfoDto mapProjectInfoToProjectInfoDto(Long projectId,
                                                          String projectName,
                                                          String description,
                                                          List<String> projectUserNames) {
        return ProjectInfoDto.builder()
                .id(projectId)
                .name(projectName)
                .description(description)
                .projectUserNames(projectUserNames)
                .build();
    }

    @Transactional
    public Long createProjectAndProjectUser(ProjectDto projectDto) {

        UserEntity selectedUserEntity = fetchUserEntityById(projectDto.getOwnerId());
        log.info("selectedUser's id" + selectedUserEntity.getId());
        Project newProject = projectMapper.mapProjectDtoToProject(projectDto);

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
                .orElseThrow(() -> new UserNotFoundException("Can not find user by userId:" + userId));
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

    @Transactional
    public void updateProjectInfo(ProjectModificationDto projectModificationDto) {

        int res = projectRepository.updateProjectInfo(projectModificationDto.getProjectId(),
                projectModificationDto.getName(),
                projectModificationDto.getDescription(),
                OffsetDateTime.now(UTC));

        if (res == 0) {
            throw new ProjectNotFoundException("Can not find project with Id:" + projectModificationDto.getProjectId());
        }
    }

    public List<EmployeeGetDto> findAllMembersByProjectId(Long id) {
        log.info("Project ID: {}", id);
        List<IEmployeeInfo> members = userRepository.findAllMembersByProjectId(id);
        if (members.isEmpty()) {
            throw new EmployeeNotFoundException("Can not find member by project id:" + id);
        }
        List<EmployeeGetDto> employeeGetDtoList = new ArrayList<>();
        for (IEmployeeInfo iEmployeeInfo: members) {
            employeeGetDtoList.add(employeeMapper.mapEntityToDto(iEmployeeInfo));
        }
        return employeeGetDtoList;
    }

    private Project fetchProjectById(Long projectId) {
        //TODO orElseThrow
        return projectRepository.findProjectByProjectId(projectId).get();
    }
    public void createProjectUser(Long projectId, Long userId) {
        UserEntity projectUserEntity = fetchUserEntityById(userId);
        Project project = fetchProjectById(projectId);
        ProjectUserId projectUserId = new ProjectUserId(userId, projectId);
        ProjectUser newProjectUser = createProjectUser(projectUserId, projectUserEntity, project);
        projectUserRepository.save(newProjectUser);
    }
}
