package com.asyncworking.repositories;

import com.asyncworking.constants.Status;
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
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProjectRepositoryTest extends DBHelper {

    Project mockProject;

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
                .defaultView("Board")
                .description("This is a project")
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        Project returnedProject = projectRepository.save(mockProject);
        assertEquals(mockProject.getName(), returnedProject.getName());
    }

    @Test
    public void shouldGetProjectSuccessfullyGivenProjectId() {
        saveMockData();
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
        Optional<Project> returnedIProjectInfo = projectRepository.findById(mockIDProject.getId());
        assertEquals(mockIDProject.getName(), returnedIProjectInfo.get().getName());
    }

    @Test
    public void shouldGetProjectSuccessfullyGivenCompanyId() {
        saveMockData();
        List<Project> returnedProjectIds = projectRepository.findByCompanyId(1L);
        assertNotNull(returnedProjectIds);
    }

    @Test
    public void shouldReturnEmptyDueToGivenCompanyIdWithoutProjects() {
        saveMockData();
        List<Project> returnedProjects = projectRepository.findByCompanyId(0L);
        assertTrue(returnedProjects.isEmpty());
    }

    @Test
    public void shouldReturnEmptyDueToGivenProjectIdWithoutIProjectInfo() {
        saveMockData();
        Optional<Project> returnedIProjectInfo  = projectRepository.findById(0L);
        assertTrue(returnedIProjectInfo.isEmpty());
    }

    @Test
    public void shouldGet1AndModifyProjectInfoSuccessfully() {
        saveMockData();
        int count = projectRepository.updateProjectInfo(mockProject.getId(),
                "AW-new-project",
                "Gaming",
                OffsetDateTime.now(UTC),
                mockProject.getCompanyId());
        assertEquals(1, count);
    }

    @Test
    public void shouldReturnProjectSuccessfullyGivenProjectId() {
        saveMockData();
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
        Optional<Project> project = projectRepository.findById(mockProject.getId());
        assertEquals("AWProject", project.get().getName());
    }

    @Test
    public void shouldReturnEmptyDueToWrongProjectId() {
        saveMockData();
        Optional<Project> project = projectRepository.findById(0L);
        assertTrue(project.isEmpty());
    }

    private void saveMockData() {
        mockProject = Project.builder()
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
