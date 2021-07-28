package com.asyncworking.services;

import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.dtos.ProjectModificationDto;
import com.asyncworking.exceptions.EmployeeNotFoundException;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.ProjectUserRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.EmployeeMapper;
import com.asyncworking.utility.mapper.ProjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProjectServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    private ProjectService projectService;

    @Mock
    private MessageCategoryService messageCategoryService;

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private CompanyService companyService;

    private Project mockProject;

    private ProjectInfoDto projectInfoDto;

    private Set<Long> projectId;

    @BeforeEach()
    public void setup() {
        projectService = new ProjectService(
                        userRepository,
                        projectRepository,
                        projectUserRepository,
                        projectMapper,
                        employeeMapper,
                        userService,
                        roleService,
                        companyService,
                        messageCategoryService
                );


        mockProject = Project.builder()
                .id(2L)
                .companyId(2L)
                .name("AW")
                .description("Async working application")
                .build();

        projectInfoDto = ProjectInfoDto.builder()
                .id(2L)
                .name("AW")
                .description("Async working application")
                .build();

        projectId = new HashSet<>();
        projectId.add(2L);
    }

    @Test
    public void shouldReturnProjectInfoDtoByProjectId() {
        when(projectRepository.findById(any())).thenReturn(Optional.of(mockProject));
        when(projectMapper.mapProjectToProjectInfoDto(any())).thenReturn(projectInfoDto);
        assertEquals(projectInfoDto.getName(),
                projectService.fetchProjectInfoByProjectId(mockProject.getId()).getName());
    }

    @Test
    public void shouldThrowProjectNotFoundExceptionWhenProjectIdIsNotExist() {
        when(projectRepository.findById(1L)).thenThrow(
                new ProjectNotFoundException("Can not find project by projectId: 1L"));
        assertThrows(ProjectNotFoundException.class, () -> projectService.fetchProjectInfoByProjectId(1L));
    }

    @Test
    public void shouldReturnProjectInfoListByCompanyId() {
        when(projectRepository.findProjectsByCompanyId(any())).thenReturn(List.of(mockProject));
        when(projectMapper.mapProjectToProjectInfoDto(any())).thenReturn(projectInfoDto);
        assertEquals(projectInfoDto.getName(),
                projectService.fetchProjectInfoListByCompanyId(mockProject.getCompanyId()).get(0).getName());
    }

    @Test
    public void shouldReturnAvailableProjectInfoList() {
        Company mockReturnedCompany = Company.builder()
                .adminId(2L)
                .build();
        when(projectRepository.findById(any())).thenReturn(Optional.of(mockProject));
        when(projectRepository.findProjectsByCompanyId(any())).thenReturn(List.of(mockProject));
        when(projectMapper.mapProjectToProjectInfoDto(any())).thenReturn(projectInfoDto);
        when(projectUserRepository.findProjectIdByUserId(any())).thenReturn(projectId);
        when(companyService.fetchCompanyById(any())).thenReturn(mockReturnedCompany);
        assertEquals(projectInfoDto.getName(),
                projectService.fetchAvailableProjectInfoList(mockProject.getCompanyId(), 2L).get(0).getName());
        assertEquals(projectInfoDto.getName(),
                projectService.fetchAvailableProjectInfoList(mockProject.getCompanyId(), 1L).get(0).getName());
    }

    @Test
    public void shouldThrowProjectNotFoundExceptionWhenCompanyDoNotHaveAnyProject() {
        when(projectRepository.findProjectsByCompanyId(1L)).thenThrow(
                new ProjectNotFoundException("Can not find project by companyId: 1L"));
        assertThrows(ProjectNotFoundException.class, () -> projectService.fetchProjectInfoListByCompanyId(1L));
    }

    @Test
    @Transactional
    public void createProjectAndProjectUserGivenProperProjectDto() {
        ProjectDto projectDto = ProjectDto.builder()
                .name("AW")
                .ownerId(2L)
                .companyId(2L)
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .id(1L)
                .email("lengarykkk@asyncworking.com")
                .name("kkk").build();

        when(userService.findUserById(projectDto.getOwnerId()))
                .thenReturn(mockReturnedUserEntity);
        when(projectMapper.mapProjectDtoToProject(projectDto))
                .thenReturn(mockProject);

        ArgumentCaptor<ProjectUser> projectUserCaptor = ArgumentCaptor.forClass(ProjectUser.class);
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        projectService.createProjectAndProjectUser(projectDto);
        verify(projectRepository).save(projectCaptor.capture());
        verify(projectUserRepository).save(projectUserCaptor.capture());
        ProjectUser savedProjectUser = projectUserCaptor.getValue();
        Project savedProject = projectCaptor.getValue();

        assertEquals(true, savedProjectUser.getAttended());
        assertEquals(2L, savedProject.getCompanyId());
    }

    @Test
    public void shouldReturnAllMembersByProjectId() {
        IEmployeeInfo mockEmployee = IEmployeeInfoImpl.builder()
                .id(1L)
                .email("xxx@gmail.com")
                .name("name1")
                .title("dev")
                .build();
        EmployeeGetDto mockEmployeeGetDto = EmployeeGetDto.builder()
                .id(1L)
                .name("name1")
                .email("xxx@gmail.com")
                .title("dev")
                .build();

        when(userRepository.findAllMembersByProjectId(any())).thenReturn(List.of(mockEmployee));
        when(employeeMapper.mapEntityToDto(mockEmployee)).thenReturn(mockEmployeeGetDto);
        assertEquals(mockEmployee.getName(), projectService.findAllMembersByProjectId(1L).get(0).getName());
    }

    @Test
    public void shouldThrowEmployeeNotFondExceptionWhenProjectDoNotHaveAnyMember() {
        when(userRepository.findAllMembersByProjectId(1L)).thenThrow(
                new EmployeeNotFoundException("Can not find member by project id: 1L")
        );
        assertThrows(EmployeeNotFoundException.class, () -> projectService.findAllMembersByProjectId(1L));
    }

    @Test
    @Transactional
    public void shouldUpdateProjectInfoSuccess() {
        ProjectModificationDto mockProjectModificationDto = ProjectModificationDto.builder()
                .projectId(1L)
                .name("name1")
                .description("mock dto for test")
                .build();
        projectService.updateProjectInfo(mockProjectModificationDto);
        verify(projectRepository).updateProjectInfo(any(), any(), any(), any());
    }

    @Test
    @Transactional
    public void createProjectUsersSuccess() {
        UserEntity mockUserEntity1 = UserEntity.builder()
                .id(1L)
                .email("xxx1@gmail.com")
                .name("name1")
                .build();

        UserEntity mockUserEntity2 = UserEntity.builder()
                .id(2L)
                .email("xxx2@gmail.com")
                .name("name2")
                .build();

        List<Long> ids = Arrays.asList(mockUserEntity1.getId(), mockUserEntity2.getId());
        List<UserEntity> userEntities = Arrays.asList(mockUserEntity1, mockUserEntity2);
        when(projectRepository.findById(any())).thenReturn(Optional.of(mockProject));
        when(userRepository.findAllById(any())).thenReturn(userEntities);
        ArgumentCaptor<List<ProjectUser>> projectUsersCaptor = ArgumentCaptor.forClass(List.class);
        projectService.addProjectUsers(mockProject.getId(), ids);
        verify(projectUserRepository).saveAll(projectUsersCaptor.capture());
        assertEquals(mockUserEntity1.getId(), projectUsersCaptor.getValue().get(0).getUserEntity().getId());
    }
}
