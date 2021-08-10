package com.asyncworking.services;

import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.dtos.ProjectModificationDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.ProjectUserRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    private final UserService userService;

    private final RoleService roleService;

    private final CompanyService companyService;

    private final MessageCategoryService messageCategoryService;

    public ProjectInfoDto fetchProjectInfoByProjectId(Long companyId, Long projectId) {
        return projectRepository.findByCompanyIdAndId(companyId, projectId)
                .map(projectMapper::mapProjectToProjectInfoDto)
                .orElseThrow(() -> new ProjectNotFoundException("Can not find project by projectId: " + projectId));
    }

    public List<ProjectInfoDto> fetchProjectInfoListByCompanyId(Long companyId) {
        return projectRepository.findProjectsByCompanyId(companyId).stream()
                .map(projectMapper::mapProjectToProjectInfoDto)
                .collect(Collectors.toList());
    }

    public List<ProjectInfoDto> fetchAvailableProjectInfoList(Long companyId, Long userId) {
        List<ProjectInfoDto> userProjects = projectUserRepository.findProjectIdByUserId(userId).stream()
                .map(projectId -> fetchProjectInfoByProjectId(companyId, projectId))
                .collect(Collectors.toList());
        List<ProjectInfoDto> companyProjects = fetchProjectInfoListByCompanyId(companyId);
        userProjects.retainAll(companyProjects);
        Long adminId = companyService.fetchCompanyById(companyId).getAdminId();
        if (adminId == userId) {
            return companyProjects;
        }
        return userProjects;
    }

    @Transactional
    public Long createProjectAndProjectUser(ProjectDto projectDto) {

        UserEntity selectedUserEntity = userService.findUserById(projectDto.getOwnerId());

        Project newProject = projectMapper.mapProjectDtoToProject(projectDto);
        projectRepository.save(newProject);

        roleService.assignRole(selectedUserEntity, RoleNames.PROJECT_MANAGER, newProject.getId());

        createDefaultMessageCategories(newProject);

        ProjectUser newProjectUser = addProjectUsers(selectedUserEntity, newProject);
        projectUserRepository.save(newProjectUser);

        return newProject.getId();
    }

    public void createDefaultMessageCategories(Project project) {
        messageCategoryService.createDefaultMessageCategory(project,
                "Announcement", "📢");
        messageCategoryService.createDefaultMessageCategory(project,
                "FYI", "✨");
        messageCategoryService.createDefaultMessageCategory(project,
                "Heartbeat", "❤️");
        messageCategoryService.createDefaultMessageCategory(project,
                "Pitch", "💡");
        messageCategoryService.createDefaultMessageCategory(project,
                "Question", "👋");
    }

    private ProjectUser addProjectUsers(UserEntity userEntity, Project project) {
        return ProjectUser.builder()
                .id(new ProjectUserId(userEntity.getId(), project.getId()))
                .attended(true)
                .project(project)
                .userEntity(userEntity)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    @Transactional
    public void updateProjectInfo(Long companyId, Long projectId, ProjectModificationDto projectModificationDto) {

        projectRepository.updateProjectInfo(projectId,
                projectModificationDto.getName(),
                projectModificationDto.getDescription(),
                OffsetDateTime.now(UTC),
                companyId);
    }

    public List<EmployeeGetDto> findAllMembersByCompanyIdAndProjectId(Long companyId, Long projectId) {
        return userRepository.findAllMembersByCompanyIdAndProjectId(companyId, projectId).stream()
                .map(employeeMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    public boolean ifProjectIsPublic(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Can not find project by projectId: " + projectId));
        return !project.getIsPrivate();
    }

    @Transactional
    public void addProjectUsers(Long companyId, Long projectId, List<Long> userIds) {
        Project project = projectRepository.findByCompanyIdAndId(companyId, projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Can not find project by projectId: " + projectId));
        List<ProjectUser> projectUsers = userRepository.findAllById(userIds).stream()
                .map(user -> addProjectUsers(user, project))
                .collect(Collectors.toList());
        projectUserRepository.saveAll(projectUsers);
    }
}
