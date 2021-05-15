package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.Category;
import com.asyncworking.models.Message;
import com.asyncworking.models.Project;
import com.asyncworking.repositories.MessageRepository;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
public class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Autowired
    private MessageMapper messageMapper;

    private MessageService messageService;

    private MessagePostDto messagePostDto;

    @Mock
    private UserRepository userRepository;

    private Project mockProject;

    private OffsetDateTime currentTime;

    @BeforeEach
    public void setUp() {
        messageService = new MessageService(
                messageMapper,
                projectRepository,
                messageRepository,
                userRepository
        );

        messagePostDto = MessagePostDto.builder()
                .companyId(1L)
                .projectId(2L)
                .messageTitle("first message")
                .posterUserId(3L)
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .docURL("https:www.adc.com")
                .build();

        mockProject = Project.builder()
                .id(2L)
                .name("omega")
                .build();

        currentTime = OffsetDateTime.now(UTC);
    }

    @Test
    @Transactional
    public void returnCorrectMessageGetDto() {

        Message mockReturnMessage = Message.builder()
                .id(3L)
                .project(mockProject)
                .companyId(1L)
                .posterUserId(3L)
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .createdTime(currentTime)
                .postTime(currentTime)
                .updatedTime(currentTime)
                .docURL("https:www.adc.com")
                .build();

        MessageGetDto mockMessageGetDto = MessageGetDto.builder()
                .id(3L)
                .companyId(1L)
                .messageTitle("first message")
                .projectId(2L)
                .posterUserId(3L)
                .posterUser("FL")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(currentTime)
                .docURL("https:www.adc.com")
                .build();

        when(messageRepository.save(any())).thenReturn(mockReturnMessage);
        when(userRepository.findNameById(3L)).thenReturn(Optional.of("FL"));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(mockProject));


        assertEquals(mockMessageGetDto, messageService.createMessage(messagePostDto));
    }

    @Test
    public void throwProjectNotFoundExceptionWhenProjectIdIsNotExist() {
        when(projectRepository.findById(3L))
                .thenThrow(new ProjectNotFoundException("Cannot find project by id:2"));
        assertThrows(ProjectNotFoundException.class, () -> messageService.createMessage(messagePostDto));
    }

    @Test
    public void returnRequiredQuantityOfMessageGetDtoList () {
        List<Message> mockReturnMessageList = new ArrayList<>();
        mockReturnMessageList.add(Message.builder()
                .id(5L)
                .project(mockProject)
                .companyId(1L)
                .posterUserId(3L)
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .createdTime(currentTime)
                .updatedTime(currentTime)
                .postTime(currentTime)
                .build());
        mockReturnMessageList.add(Message.builder()
                .id(6L)
                .project(mockProject)
                .companyId(1L)
                .posterUserId(3L)
                .messageTitle("second message")
                .content("second message content")
                .category(Category.ANNOUNCEMENT)
                .createdTime(currentTime)
                .updatedTime(currentTime)
                .postTime(currentTime)
                .build());

        when(messageRepository.findMessageByProjectId(2L)).thenReturn(mockReturnMessageList);
        when(userRepository.findNameById(3L)).thenReturn(Optional.of("FL"));
        List<MessageGetDto> mockMessageGetDtoList = messageService.findMessageListByProjectId(2L);
        assertEquals(2, mockMessageGetDtoList.size());
    }



}
