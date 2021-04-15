package com.asyncworking.services;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.CompanyColleagueDto;
import com.asyncworking.dtos.ProjectDto;
import com.asyncworking.dtos.ProjectInfoDto;
import com.asyncworking.exceptions.CompanyNotFoundException;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.ProjectUserRepository;
import com.asyncworking.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@AutoConfigureMockMvc
public class ProjectServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    @Transactional
    public void createProjectAndProjectUserGivenProperProjectDto() {
        ProjectDto projectDto = ProjectDto.builder()
                .name("kkk")
                .ownerId(1L)
                .companyId(1L)
                .build();
        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .id(1L)
                .email("lengarykkk@asyncworking.com")
                .name("kkk").build();
        when(userRepository.findUserEntityById(projectDto.getOwnerId()))
                .thenReturn(Optional.of(mockReturnedUserEntity));

        ArgumentCaptor<ProjectUser> projectUserCaptor = ArgumentCaptor.forClass(ProjectUser.class);
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        projectService.createProjectAndProjectUser(projectDto);
        verify(projectRepository).save(projectCaptor.capture());
        verify(projectUserRepository).save(projectUserCaptor.capture());
        ProjectUser savedProjectUser = projectUserCaptor.getValue();
        Project savedProject = projectCaptor.getValue();

        assertEquals(true, savedProjectUser.getAttended());
        assertEquals(1L, savedProject.getCompanyId());
    }

    @Test
    public void shouldReturnProjectInfoDtoListGivenCompanyId() {
        Long id = 4L;
        IProjectInfoImpl mockProjectInfo = IProjectInfoImpl.builder()
                .projectId(2L)
                .name("newProject")
                .build();
        List<Long> mockIds = List.of(2L);
        when(projectRepository.findProjectIdsByCompanyId(id)).thenReturn(mockIds);
        when(projectRepository.findProjectInfoByProjectId(2L)).thenReturn(Optional.of(mockProjectInfo));
        List<ProjectInfoDto> projectInfoDtoList = projectService.fetchProjectInfoListByCompanyId(id);
        assertEquals("newProject", projectInfoDtoList.get(0).getName());

    }

    @Test
    public void throwNotFoundExceptionWhenProjectNotExistGivenCompanyId() {
        when(projectRepository.findById(2L))
                .thenReturn(Optional.empty());
        Exception exception = assertThrows(ProjectNotFoundException.class,
                () -> projectService.fetchProjectInfoListByCompanyId(2L));

        String expectedMessage = "Can not found project by companyId:2";

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
