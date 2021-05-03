package com.asyncworking.repositories;

import com.asyncworking.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProjectRepositoryTest extends DBHelper {
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void insertMockEmp() {
        clearDb();
        when(passwordEncoder.encode("len123")).thenReturn("testpass");
    }

    @Test
    public void shouldAddProjectIntoDBSuccessfullyGivenProperProject() {
        Project mockProject = Project.builder()
                .name("newProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        Project returnedProject = projectRepository.save(mockProject);
        assertEquals(mockProject.getName(), returnedProject.getName());
    }

    @Test
    public void shouldGetIProjectInfoSuccessfullyGivenProjectId() {
        Project mockIDProject = Project.builder()
                .name("IDProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        projectRepository.save(mockIDProject);
        Optional<IProjectInfo> returnedIProjectInfo = projectRepository.findProjectInfoByProjectId(mockIDProject.getId());
        assertEquals(mockIDProject.getName(), returnedIProjectInfo.get().getName());
    }

    @Test
    public void shouldGetProjectIdsSuccessfullyGivenCompanyId() {
        saveMockData();
        List<Long> returnedProjectIds = projectRepository.findProjectIdsByCompanyId(1L);
        assertNotNull(returnedProjectIds);
    }

    @Test
    public void shouldReturnEmptyDueToGivenCompanyIdWithoutProjects() {
        saveMockData();
        List<Long> returnedProjectIds = projectRepository.findProjectIdsByCompanyId(0L);
        assertTrue(returnedProjectIds.isEmpty());
    }

    @Test
    public void shouldReturnEmptyDueToGivenProjectIdWithoutIProjectInfo() {
        saveMockData();
        Optional<IProjectInfo> returnedIProjectInfo  = projectRepository.findProjectInfoByProjectId(0L);
        assertTrue(returnedIProjectInfo.isEmpty());
    }

    @Test
    public void shouldFindNamesByProjectId() {
        saveMockData();
        List<String> returnedNames = projectRepository.findNamesByProjectId(1L);
        assertNotNull(returnedNames);
    }

    @Test
    public void shouldReturnEmptyDueToProjectIdWithoutProjectUserNames() {
        saveMockData();
        List<String> returnedNames = projectRepository.findNamesByProjectId(0L);
        assertTrue(returnedNames.isEmpty());
    }

    @Test
    public void shouldGet1AndModifyProjectInfoSuccessfully() {
        Project mockProject = Project.builder()
                .name("AWProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        projectRepository.save(mockProject);
        int count = projectRepository.updateProjectInfo(mockProject.getId(),
                "AW-new-project",
                "Gaming",
                OffsetDateTime.now(UTC));
        assertEquals(1, count);
    }

    @Test
    public void shouldReturnProjectSuccessfullyGivenProjectId() {
        Project mockProject = Project.builder()
                .name("AWProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        projectRepository.save(mockProject);
        Optional<Project> project = projectRepository.findProjectByProjectId(mockProject.getId());
        assertEquals("AWProject", project.get().getName());
    }

    @Test
    public void shouldReturnEmptyDueToWrongProjectId() {
        Optional<Project> project = projectRepository.findProjectByProjectId(0L);
        assertTrue(project.isEmpty());
    }

    private void saveMockData() {
        Project mockProject = Project.builder()
                .name("AWProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        UserEntity mockUser = UserEntity.builder()
                .name("Lengary")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        ProjectUserId mockProjectUserId = ProjectUserId.builder()
                .userId(mockUser.getId())
                .projectId(mockProject.getId())
                .build();

        ProjectUser mockProjectUser = ProjectUser.builder()
                .attended(true)
                .project(mockProject)
                .userEntity(mockUser)
                .id(mockProjectUserId)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();


        userRepository.save(mockUser);
        projectRepository.save(mockProject);
        projectUserRepository.save(mockProjectUser);

    }
}
