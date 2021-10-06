package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.MessageNotFoundException;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.Message;
import com.asyncworking.models.MessageCategory;
import com.asyncworking.models.Project;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.*;
import com.asyncworking.utility.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageCategoryRepository messageCategoryRepository;

    @Spy
    private MessageMapper messageMapper;

    private MessageService messageService;

    private MessagePostDto messagePostDto;

    private Project mockProject;

    private OffsetDateTime currentTime;

    private UserEntity mockUserEntity1;

    private UserEntity mockUserEntity2;

    private Message mockMessage1;

    private Message mockMessage2;

    private Message mockMessage3;

    private MessageCategory mockFirstMessageCategory;

    private MessageCategory mockSecondMessageCategory;

    @BeforeEach
    public void setUp() {
        messageService = new MessageService(
                messageMapper,
                projectRepository,
                messageRepository,
                userRepository,
                messageCategoryRepository
        );
        mockProject = Project.builder()
                .companyId(1L)
                .id(1L)
                .name("project1")
                .build();

        mockFirstMessageCategory = MessageCategory.builder()
                .id(1L)
                .project(mockProject)
                .categoryName("firstCategory")
                .emoji("👋")
                .build();

        mockSecondMessageCategory = MessageCategory.builder()
                .id(2L)
                .project(mockProject)
                .categoryName("secondCategory")
                .emoji("😊")
                .build();

        messagePostDto = MessagePostDto.builder()
                .companyId(1L)
                .projectId(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .content("first message content")
                .messageCategoryId(1L)
                .originNotes("<p>list rich editor</p>")
                .docURL("https:www.adc.com")
                .build();

        currentTime = OffsetDateTime.now(UTC);

        mockUserEntity1 = UserEntity.builder()
                .id(1L)
                .name("testName1")
                .build();

        mockUserEntity2 = UserEntity.builder()
                .id(2L)
                .name("testName2")
                .build();
    }

    public void mockMessage() {
        mockMessage1 = Message.builder()
                .id(1L)
                .project(mockProject)
                .posterUserId(1L)
                .messageTitle("first message")
                .content("first message content")
                .messageCategory(mockFirstMessageCategory)
                .originNotes("<p>list rich editor</p>")
                .postTime(currentTime)
                .docURL("abc.com")
                .subscribersIds("1L,2L")
                .build();

        mockMessage2 = Message.builder()
                .id(2L)
                .project(mockProject)
                .posterUserId(1L)
                .messageTitle("second message")
                .content("second message content")
                .messageCategory(mockSecondMessageCategory)
                .originNotes("<p>list rich editor</p>")
                .postTime(currentTime)
                .docURL("abc.com")
                .subscribersIds("1L,2L")
                .build();

        mockMessage3 = Message.builder()
                .id(3L)
                .project(mockProject)
                .posterUserId(2L)
                .messageTitle("second message")
                .content("second message content")
                .messageCategory(null)
                .originNotes("<p>list rich editor</p>")
                .postTime(currentTime)
                .docURL("abc.com")
                .subscribersIds("1L,2L")
                .build();
    }

    @Test
    public void shouldReturnMessageGetDtoGivenCorrectMessagePostDto() {
        Message mockReturnMessage = Message.builder()
                .id(1L)
                .project(mockProject)
                .companyId(1L)
                .posterUserId(1L)
                .messageTitle("first message")
                .content("first message content")
                .messageCategory(mockFirstMessageCategory)
                .originNotes("<p>list rich editor</p>")
                .createdTime(currentTime)
                .postTime(currentTime)
                .updatedTime(currentTime)
                .subscribersIds("1L,2L")
                .docURL("https:www.abc.com")
                .build();

        MessageGetDto mockMessageGetDto = MessageGetDto.builder()
                .id(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("testName1")
                .content("first message content")
                .messageCategoryId(1L)
                .messageCategoryName("firstCategory")
                .messageCategoryEmoji("👋")
                .originNotes("<p>list rich editor</p>")
                .postTime(currentTime)
                .docURL("https:www.abc.com")
                .subscribersIds("1L,2L")
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(messageRepository.save(any())).thenReturn(mockReturnMessage);
        when(userRepository.findUserEntityById(1L)).thenReturn(Optional.of(mockUserEntity1));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(messageCategoryRepository.findById(1L)).thenReturn(Optional.of(mockFirstMessageCategory));

        assertEquals(mockMessageGetDto, messageService.createMessage(messagePostDto.getCompanyId(), messagePostDto.getProjectId(),
                messagePostDto));
    }

    @Test
    public void shouldReturnMessageGetDtoGivenCorrectMessagePostDtoWithNullCategoryId() {
        Message mockReturnMessage = Message.builder()
                .id(1L)
                .project(mockProject)
                .companyId(1L)
                .posterUserId(1L)
                .messageTitle("first message")
                .content("first message content")
                .messageCategory(null)
                .createdTime(currentTime)
                .postTime(currentTime)
                .updatedTime(currentTime)
                .docURL("https:www.abc.com")
                .subscribersIds("1L,2L")
                .build();

        MessageGetDto mockMessageGetDto = MessageGetDto.builder()
                .id(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("testName1")
                .content("first message content")
                .messageCategoryId(null)
                .messageCategoryName(null)
                .messageCategoryEmoji(null)
                .postTime(currentTime)
                .docURL("https:www.abc.com")
                .subscribersIds("1L,2L")
                .build();

        MessagePostDto messagePostDto1 = MessagePostDto.builder()
                .companyId(1L)
                .projectId(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .content("first message content")
                .messageCategoryId(null)
                .subscribersIds("1L,2L")
                .docURL("https:www.adc.com")
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(messageRepository.save(any())).thenReturn(mockReturnMessage);
        when(userRepository.findUserEntityById(1L)).thenReturn(Optional.of(mockUserEntity1));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

        assertEquals(mockMessageGetDto, messageService.createMessage(messagePostDto1.getCompanyId(), messagePostDto1.getProjectId(),
                messagePostDto1));
    }

    @Test
    public void shouldThrowProjectNotFoundExceptionWhenGivenMessagePostDtoWhichProjectIdIsNotExist() {
        lenient().when(userRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findById(1L))
                .thenThrow(new ProjectNotFoundException("Cannot find project by id:1"));

        assertThrows(ProjectNotFoundException.class, () -> messageService.createMessage(messagePostDto.getCompanyId(),
                messagePostDto.getProjectId(), messagePostDto));
    }

    @Test
    public void shouldThrowProjectNotFoundExceptionWhenGivenProjectIdNotBelongToTheGivenCompanyId() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        lenient().when(userRepository.existsById(1L)).thenReturn(true);
        assertThrows(ProjectNotFoundException.class, () -> messageService.createMessage(7777L,
                messagePostDto.getProjectId(), messagePostDto));
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenGivenMessagePostDtoWhichUserIdIsNotExist() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> messageService.createMessage(messagePostDto.getCompanyId(),
                messagePostDto.getProjectId(), messagePostDto));
    }

    @Test
    public void shouldReturnListOfUserEntityWhenGivenCorrectListOfMessage() {
        this.mockMessage();
        when(userRepository.findByIdIn(List.of(1L, 1L, 2L))).thenReturn(Optional.of(List.of(mockUserEntity1, mockUserEntity2)));
        List<UserEntity> userEntityList = messageService.findUserEntityByMessageList(List.of(mockMessage1, mockMessage2, mockMessage3));
        assertNotNull(userEntityList);
        assertEquals(2, userEntityList.size());
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenGivenInvalidListOfMessage() {
        this.mockMessage();
        when(userRepository.findByIdIn(List.of(1L, 1L, 2L))).thenThrow(new UserNotFoundException("cannot find user by id in [1, 1, 2]"));
        assertThrows(UserNotFoundException.class, () -> messageService.findUserEntityByMessageList(
                List.of(mockMessage1, mockMessage2, mockMessage3)));
    }

    @Test
    public void shouldReturnListOfMessageGetDtoListWhenGivenCorrectProjectId() {
        this.mockMessage();
        when(messageRepository.findByCompanyIdAndProjectId(1L, 1L)).thenReturn(List.of(mockMessage1, mockMessage2, mockMessage3));
        when(userRepository.findByIdIn(List.of(1L, 1L, 2L))).thenReturn(Optional.of(List.of(mockUserEntity1, mockUserEntity2)));
        List<MessageGetDto> mockMessageGetDtoList = messageService.findMessageListByCompanyIdAndProjectId(1L, 1L);
        assertNotNull(mockMessageGetDtoList);
        assertEquals(3, mockMessageGetDtoList.size());
    }

    @Test
    public void shouldReturnMessageGetDtoWhenGivenId() {
        this.mockMessage();
        MessageGetDto mockMessageGetDto = MessageGetDto.builder()
                .id(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("testName1")
                .content("first message content")
                .messageCategoryId(1L)
                .messageCategoryName("firstCategory")
                .messageCategoryEmoji("👋")
                .originNotes("<p>list rich editor</p>")
                .postTime(currentTime)
                .docURL("abc.com")
                .subscribersIds("1L,2L")
                .build();
        when(messageRepository.findByCompanyIdAndProjectIdAndId(1L, 1L, 1L)).thenReturn(Optional.of(mockMessage1));
        when(userRepository.findUserEntityById(1L)).thenReturn(Optional.of(mockUserEntity1));
        assertEquals(messageService.findMessageByCompanyIdAndProjectIdAndId(1L, 1L, 1L), mockMessageGetDto);
    }

    @Test
    public void shouldReturnMessageGetDtoWhenGivenIdWithCategoryIsNull() {
        this.mockMessage();
        Message mockMessage = Message.builder()
                .id(1L)
                .project(mockProject)
                .posterUserId(1L)
                .messageTitle("first message")
                .content("first message content")
                .messageCategory(null)
                .postTime(currentTime)
                .docURL("abc.com")
                .subscribersIds("1L,2L")
                .build();

        MessageGetDto mockMessageGetDto = MessageGetDto.builder()
                .id(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("testName1")
                .content("first message content")
                .messageCategoryId(null)
                .messageCategoryName(null)
                .messageCategoryEmoji(null)
                .postTime(currentTime)
                .docURL("abc.com")
                .subscribersIds("1L,2L")
                .build();
        when(messageRepository.findByCompanyIdAndProjectIdAndId(1L, 1L, 1L)).thenReturn(Optional.of(mockMessage));
        when(userRepository.findUserEntityById(1L)).thenReturn(Optional.of(mockUserEntity1));
        assertEquals(messageService.findMessageByCompanyIdAndProjectIdAndId(1L, 1L, 1L), mockMessageGetDto);
    }

    @Test
    public void shouldThrowMessageNotFoundExceptionWhenGivenIdNotExists() {
        lenient().when(messageRepository.findById(1L)).thenThrow(new MessageNotFoundException("cannot find message by id " + 1L));
        assertThrows(MessageNotFoundException.class, () -> messageService.findMessageByCompanyIdAndProjectIdAndId(1L, 1L, 1L));
    }

}
