package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.models.Category;
import com.asyncworking.models.Message;
import com.asyncworking.models.Project;
import com.asyncworking.repositories.MessageRepository;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.utility.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import java.util.Optional;

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

    private MessageService messageService;

    @Autowired
    private MessageMapper messageMapper;

    @BeforeEach
    public void setUp() {
        messageService = new MessageService(
                messageMapper,
                projectRepository,
                messageRepository
        );
    }

    @Test
    @Transactional
    public void createMessageSuccess() {
        Project mockProject = Project.builder()
                .id(2L)
                .name("omega")
                .build();

        MessagePostDto messagePostDto = MessagePostDto.builder()
                .companyId(1L)
                .projectId(2L)
                .projectUserId(3L)
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .build();

        Message mockReturnMessage = Message.builder()
                .id(5L)
                .project(mockProject)
                .companyId(1L)
                .projectUserId(3L)
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .build();

        when(messageRepository.save(any())).thenReturn(mockReturnMessage);
        when(projectRepository.findById(2L)).thenReturn(Optional.of(mockProject));
        MessageGetDto messageGetDto = messageService.createMessage(messagePostDto);

        assertEquals(messageGetDto.getMessageTitle(), "first message");
        assertEquals(messageGetDto.getContent(), "first message content");
        assertEquals(messageGetDto.getCategory(), Category.ANNOUNCEMENT);
        assertEquals(messageGetDto.getId(), 5L);

    }

}
