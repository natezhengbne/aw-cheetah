package com.asyncworking.services;

import com.asyncworking.dtos.MessageCategoryGetDto;
import com.asyncworking.dtos.MessageCategoryPostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.MessageCategory;
import com.asyncworking.models.Project;
import com.asyncworking.repositories.MessageCategoryRepository;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.utility.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageCategoryServiceTest {
    @Mock
    private MessageCategoryRepository messageCategoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    private MessageCategoryService messageCategoryService;

    private Project mockProject;

    private MessageCategoryPostDto messageCategoryPostDto;

    @Spy
    private MessageMapper messageMapper;


    private MessageCategory mockMessageCategory1;

    private MessageCategory mockMessageCategory2;

    @BeforeEach
    public void setUp() {
        messageCategoryService = new MessageCategoryService(
                messageMapper,
                projectRepository,
                messageCategoryRepository
        );

        mockProject = Project.builder()
                .id(1L)
                .name("project1")
                .build();

        messageCategoryPostDto = MessageCategoryPostDto.builder()
                .projectId(1L)
                .categoryName("category1")
                .emoji("👋")
                .build();
    }

    public void mockMessageCategory() {

        mockMessageCategory1 = MessageCategory.builder()
                .id(1L)
                .project(mockProject)
                .categoryName("category1")
                .emoji("👋")
                .build();

        mockMessageCategory2 = MessageCategory.builder()
                .id(1L)
                .project(mockProject)
                .categoryName("category2")
                .emoji("💗")
                .build();
    }

    @Test
    public void shouldReturnMessageCategoryGetDtoGivenMessageCategoryPostDto() {
        MessageCategory mockReturnMessageCategory = MessageCategory.builder()
                .id(1L)
                .project(mockProject)
                .categoryName("category1")
                .emoji("👋")
                .build();

        MessageCategoryGetDto mockMessageCategoryGetDto = MessageCategoryGetDto.builder()
                .messageCategoryId(1L)
                .projectId(1L)
                .categoryName("category1")
                .emoji("👋")
                .build();

        when(messageCategoryRepository.save(any())).thenReturn(mockReturnMessageCategory);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

        assertEquals(mockMessageCategoryGetDto, messageCategoryService.createMessageCategory(messageCategoryPostDto));
    }

    @Test
    @Transactional
    public void shouldEditMessageCategorySuccess() {
        MessageCategoryGetDto mockMessageCategoryGetDto = MessageCategoryGetDto.builder()
                .messageCategoryId(1L)
                .categoryName("first test")
                .messageCategoryId(1L)
                .emoji("\uD83D\uDE00")
                .build();
        messageCategoryService.editMessageCategory(1L, mockMessageCategoryGetDto);
        verify(messageCategoryRepository).editMessage(any(), any(), any());
    }

    @Test
    public void shouldThrowProjectNotFoundExceptionWhenGivenMessageCategoryPostDtoWhichProjectIdIsNotExist() {
        when(projectRepository.findById(1L)).thenThrow(new ProjectNotFoundException("Cannot find project by id:1"));
        assertThrows(ProjectNotFoundException.class,
                () -> messageCategoryService.createMessageCategory(messageCategoryPostDto));
    }

    @Test
    public void shouldReturnListOfMessageCategoryGetDtoWhenGivenProjectId() {
        this.mockMessageCategory();
        when(messageCategoryRepository.findByCompanyIdAndProjectId(1L, 1L)).thenReturn(List.of(mockMessageCategory1,
                mockMessageCategory2));
        List<MessageCategoryGetDto> mockMessageCategoryGetDtoList =
                messageCategoryService.findMessageCategoryListByCompanyIdAndProjectId(1L, 1L);
        assertNotNull(mockMessageCategoryGetDtoList);
        assertEquals(2, mockMessageCategoryGetDtoList.size());
    }

    @Test
    public void shouldReturnMessageCategoryWhenGivenDetails() {
        MessageCategory mockReturnMessageCategory = MessageCategory.builder()
                .id(1L)
                .project(mockProject)
                .categoryName("category1")
                .emoji("👋")
                .build();

        MessageCategoryGetDto mockMessageCategoryGetDto = MessageCategoryGetDto.builder()
                .messageCategoryId(1L)
                .projectId(1L)
                .categoryName("category1")
                .emoji("👋")
                .build();

        when(messageCategoryRepository.save(any())).thenReturn(mockReturnMessageCategory);

        assertEquals(mockMessageCategoryGetDto, messageCategoryService.createDefaultMessageCategory(mockProject, "category1", "👋"));
    }
}
