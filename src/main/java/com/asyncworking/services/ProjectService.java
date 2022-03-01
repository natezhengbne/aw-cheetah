package com.asyncworking.services;

import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.dtos.ProjectModificationDto;
import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.Project;
import com.asyncworking.models.ProjectUser;
import com.asyncworking.models.ProjectUserId;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.ProjectUserRepository;
import com.asyncworking.repositories.TodoItemRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.repositories.UserRoleRepository;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.asyncworking.models.RoleNames.COMPANY_MANAGER;
import static com.asyncworking.models.RoleNames.PROJECT_MANAGER;
import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    public static final String DEFAULT_LIST_NAME = "Done";

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final TodoItemRepository todoItemRepository;

    private final ProjectUserRepository projectUserRepository;

    private final UserRoleRepository userRoleRepository;

    private final ProjectMapper projectMapper;

    private final EmployeeMapper employeeMapper;

    private final UserService userService;

    private final RoleService roleService;

    private final MessageCategoryService messageCategoryService;

    private final TodoService todoService;

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
        return projectRepository.findByCompanyIdAndIsPrivate(companyId, false).stream()
                .map(projectMapper::mapProjectToProjectInfoDto)
                .collect(Collectors.toSet());
    }

    private Set<ProjectInfoDto> fetchProjectInfoListByUserId(Long userId) {
        return projectUserRepository.findProjectByUserId(userId).stream()
                .map(projectMapper::mapProjectToProjectInfoDto)
                .collect(Collectors.toSet());
    }

    public Collection<ProjectInfoDto> fetchAvailableProjectInfoList(Long companyId, Long userId) {
        Set<ProjectInfoDto> companyProjects = fetchProjectInfoListByCompanyId(companyId);
        Set<ProjectInfoDto> userProjects = fetchProjectInfoListByUserId(userId);
        Set<ProjectInfoDto> publicCompanyProjects = fetchPublicProjectInfoListByCompanyId(companyId);
        userProjects.retainAll(companyProjects);
        userProjects.addAll(publicCompanyProjects);
        Set<Long> adminId = userRoleRepository.findUserIdByRoleNameAndTargetId(COMPANY_MANAGER.value(), companyId);
        return adminId.contains(userId) ? addProgressInfo(companyProjects) : addProgressInfo(userProjects);
    }

    private Collection<ProjectInfoDto> addProgressInfo(Set<ProjectInfoDto> projectInfo) {
        Map<Long, ProjectInfoDto> projectInfoMap = projectInfo.stream()
                .collect(Collectors.toMap(ProjectInfoDto::getId, Function.identity()));

        todoItemRepository.findProgressInfoByProjectId(projectInfoMap.keySet()).forEach(
                info -> {
                    ProjectInfoDto projectInfoDto = projectInfoMap.get(info.getId());
                    projectInfoDto.setTodoNumByStatus(info.getTodoItemStatusNum(), info.getStatus());
                    projectInfoMap.put(info.getId(), projectInfoDto);
                }
        );
        return projectInfoMap.values();
    }

    @Transactional
    public Long createProjectAndProjectUser(Long companyId, ProjectDto projectDto) {

        UserEntity selectedUserEntity = userService.findUserById(projectDto.getOwnerId());

        Project newProject = projectMapper.mapProjectDtoToProject(companyId, projectDto);
        projectRepository.save(newProject);

        roleService.assignRole(selectedUserEntity, PROJECT_MANAGER, newProject.getId());

        createDefaultMessageCategories(newProject);

        todoService.createTodoList(
                companyId,
                newProject.getId(),
                TodoListDto.builder().todoListTitle(ProjectService.DEFAULT_LIST_NAME).build());

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
