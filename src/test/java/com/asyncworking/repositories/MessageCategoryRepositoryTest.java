package com.asyncworking.repositories;

import com.asyncworking.models.MessageCategory;
import com.asyncworking.models.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class MessageCategoryRepositoryTest extends DBHelper {

    private Project mockProject;

    private MessageCategory mockFirstMessageCategory;

    private MessageCategory mockSecondMessageCategory;

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

        mockFirstMessageCategory = MessageCategory.builder()
                .project(mockProject)
                .categoryName("firstCategory")
                .emoji("👋")
                .build();

        mockSecondMessageCategory = MessageCategory.builder()
                .project(mockProject)
                .categoryName("secondCategory")
                .emoji("😊")
                .build();

        projectRepository.save(mockProject);
    }

    @Test
    public void shouldReturnMessageCategoryListGivenAnProjectId() {
        messageCategoryRepository.save(mockFirstMessageCategory);
        messageCategoryRepository.save(mockSecondMessageCategory);
        List<MessageCategory> messageCategoryList = messageCategoryRepository.findByProjectId(mockProject.getId());
        assertEquals(2, messageCategoryList.size());
    }
}
