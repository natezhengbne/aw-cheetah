package com.asyncworking.services;

import com.asyncworking.dtos.*;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.ProjectUser;
import com.asyncworking.models.ProjectUserId;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.ProjectUserRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.asyncworking.models.RoleNames.PROJECT_MANAGER;
import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final TodoItemRepository todoItemRepository;

    private final ProjectUserRepository projectUserRepository;

    private final ProjectMapper projectMapper;

    private final EmployeeMapper employeeMapper;

    private final UserService userService;

    private final RoleService roleService;

    private final CompanyService companyService;

    private final MessageCategoryService messageCategoryService;

    public ProjectInfoDto fetchProjectInfoByProjectIdAndCompanyId(Long companyId, Long projectId) {
        return projectRepository.findByCompanyIdAndId(companyId, projectId)
                .map(projectMapper::mapProjectToProjectInfoDto)
                .orElseThrow(() -> new ProjectNotFoundException("Can not find project by projectId: " + projectId));
    }

    public ProjectInfoDto fetchProjectInfoByProjectId(Long projectId) {
        return projectRepository.findById(projectId)
                .map(projectMapper::mapProjectToProjectInfoDto)
                .orElseThrow(() -> new ProjectNotFoundException("Can not find project by projectId: " + projectId));
    }

    private Set<ProjectInfoDto> fetchProjectInfoListByCompanyId(Long companyId) {
        return projectRepository.findByCompanyId(companyId).stream()
                .map(projectMapper::mapProjectToProjectInfoDto)
                .collect(Collectors.toSet());
    }

    private Set<ProjectInfoDto> fetchPublicProjectInfoListByCompanyId(Long companyId) {
        return projectRepository.findPublicProjectsByCompanyId(companyId).stream()
                .map(projectMapper::mapProjectToProjectInfoDto)
                .collect(Collectors.toSet());
    }

    public Set<ProjectInfoDto> fetchAvailableProjectInfoList(Long companyId, Long userId) {
        Set<ProjectInfoDto> userProjects = projectUserRepository.findProjectIdByUserId(userId).stream()
                .map(this::fetchProjectInfoByProjectId)
                .collect(Collectors.toSet());
        Set<ProjectInfoDto> companyProjects = fetchProjectInfoListByCompanyId(companyId);
        Set<ProjectInfoDto> publicCompanyProjects = fetchPublicProjectInfoListByCompanyId(companyId);
        userProjects.retainAll(companyProjects);
        userProjects.addAll(publicCompanyProjects);
        Long adminId = companyService.fetchCompanyById(companyId).getAdminId();
        if (adminId.equals(userId)) {
            return addProgressInfo(companyProjects);
        }
        return addProgressInfo(userProjects);
    }

    private Set<ProjectInfoDto> addProgressInfo(Set<ProjectInfoDto> projectInfo) {
        List<Long> projectIdList = projectInfo.stream().map(ProjectInfoDto::getId).collect(Collectors.toList());

        List<ProjectProgressTotal> todoItemTotalNum = todoItemRepository.findAllByProjectId(projectIdList).stream()
                .map(projectMapper::mapIProjectTotalToProjectDto)
                .collect(Collectors.toList());
        Map<Long, Integer> totalNumMap = todoItemTotalNum.stream()
                .collect(Collectors.toMap(ProjectProgressTotal::getId, ProjectProgressTotal::getTodoItemTotalNum));

        List<ProjectProgressCompleted> todoItemComNum =
                todoItemRepository.findAllCompletedByProjectId(projectIdList).stream()
                        .map(projectMapper::mapIProjectToProjectDto)
                        .collect(Collectors.toList());
        Map<Long, Integer> comNumMap = todoItemComNum.stream()
                .collect(Collectors.toMap(ProjectProgressCompleted::getId, ProjectProgressCompleted::getTodoItemCompleteNum));

        for (ProjectInfoDto value : projectInfo) {
            if (totalNumMap.containsKey(value.getId())) {
                value.setTodoItemTotalNum(totalNumMap.get(value.getId()));
            } else {
                value.setTodoItemTotalNum(0);
            }
        }
        for (ProjectInfoDto value : projectInfo) {
            if (comNumMap.containsKey(value.getId())) {
                value.setTodoItemCompleteNum(comNumMap.get(value.getId()));
            } else {
                value.setTodoItemCompleteNum(0);
            }
        }
        return projectInfo;
    }

    @Transactional
    public Long createProjectAndProjectUser(Long companyId, ProjectDto projectDto) {

        UserEntity selectedUserEntity = userService.findUserById(projectDto.getOwnerId());

        Project newProject = projectMapper.mapProjectDtoToProject(companyId, projectDto);
        projectRepository.save(newProject);

        roleService.assignRole(selectedUserEntity, PROJECT_MANAGER, newProject.getId());

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

    public List<EmployeeGetDto> findAllMembersByCompanyIdAndProjectIdAscByName(Long companyId, Long projectId) {
        return userRepository.findAllMembersByCompanyIdAndProjectIdAscByName(companyId, projectId).stream()
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
