package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.IMessageRepository;
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

    @Mock
    private IMessageRepository iMessageRepository;

    private Project mockProject;

    private OffsetDateTime currentTime;

    private UserEntity mockUserEntity;

    @BeforeEach
    public void setUp() {
        messageService = new MessageService(
                messageMapper,
                projectRepository,
                messageRepository,
                iMessageRepository,
                userRepository


        );

        messagePostDto = MessagePostDto.builder()
                .companyId(1L)
                .projectId(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .docURL("https:www.adc.com")
                .build();

        mockProject = Project.builder()
                .id(1L)
                .name("project1")
                .build();

        currentTime = OffsetDateTime.now(UTC);

        mockUserEntity = UserEntity.builder()
                .name("name")
                .id(1L)
                .build();

    }

    @Test
    @Transactional
    public void returnCorrectMessageGetDto() {

        Message mockReturnMessage = Message.builder()
                .id(1L)
                .project(mockProject)
                .companyId(1L)
                .posterUserId(1L)
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .createdTime(currentTime)
                .postTime(currentTime)
                .updatedTime(currentTime)
                .docURL("https:www.adc.com")
                .build();

        MessageGetDto mockMessageGetDto = MessageGetDto.builder()
                .id(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("name")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(currentTime)
                .docURL("https:www.adc.com")
                .build();

        when(messageRepository.save(any())).thenReturn(mockReturnMessage);
        when(userRepository.findUserEntityById(1L)).thenReturn(Optional.of(mockUserEntity));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));


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

        IMessage mockIMessage1 = IMessage.builder()
                .id(1L)
                .posterUserId(1L)
                .posterUser("name")
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(currentTime)
                .docURL("abc.com")
                .build();
        IMessage mockIMessage2 = IMessage.builder()
                .id(2L)
                .posterUserId(1L)
                .posterUser("name")
                .messageTitle("second message")
                .content("second message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(currentTime)
                .docURL("abc.com")
                .build();

        when(iMessageRepository.findMessageAndUserNameByProjectId(1L)).thenReturn(List.of(mockIMessage1, mockIMessage2));

        List<MessageGetDto> mockMessageGetDtoList = messageService.findMessageListByProjectId(1L);
        assertNotNull(mockMessageGetDtoList);
        assertEquals(2, mockMessageGetDtoList.size());
    }



}
