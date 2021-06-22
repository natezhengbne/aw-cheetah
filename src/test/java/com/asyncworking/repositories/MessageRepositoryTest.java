package com.asyncworking.repositories;

import com.asyncworking.models.Category;
import com.asyncworking.models.Message;
import com.asyncworking.models.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ActiveProfiles("test")
public class MessageRepositoryTest extends DBHelper{

    private Project mockProject;

    private Message mockFirstMessage;

    private Message mockSecondMessage;


    @BeforeEach
    public void createMockData() {
        clearDb();
        mockProject = Project.builder()
                .name("AWProject")
                .isDeleted(false)
                .isPrivate(false)
                .leaderId(1L)
                .companyId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        mockFirstMessage = Message.builder()
                .project(mockProject)
                .companyId(1L)
                .posterUserId(3L)
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .createdTime(OffsetDateTime.now(UTC))
                .postTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        mockSecondMessage = Message.builder()
                .project(mockProject)
                .companyId(1L)
                .posterUserId(3L)
                .messageTitle("second message")
                .content("second message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(OffsetDateTime.now(UTC))
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        projectRepository.save(mockProject);


    }

    @Test
    public void shouldReturnMessageListGivenMessagesHasBeenInserted() {
        Message savedMockFirstMessage = messageRepository.save(mockFirstMessage);
        Message savedMockSecondMessage = messageRepository.save(mockSecondMessage);
        List<Message> messageList = messageRepository.findAll();
        assertEquals(2, messageList.size());

    }

    @Test
    public void shouldReturnMessageListGivenAnProjectId() {
        messageRepository.save(mockFirstMessage);
        messageRepository.save(mockSecondMessage);
        List<Message> messageList = messageRepository.findByProjectId(mockProject.getId());
        assertEquals(2, messageList.size());
    }

    @Test
    public void shouldReturnMessageGivenAnMessageId() {
        Message savedMockFirstMessage = messageRepository.save(mockFirstMessage);
        Message savedMockSecondMessage = messageRepository.save(mockSecondMessage);
        assertEquals(savedMockFirstMessage.getMessageTitle(),
                messageRepository.findById(savedMockFirstMessage.getId()).get().getMessageTitle());
    }


}
