package com.asyncworking.repositories;

import com.asyncworking.models.MessageCategory;
import com.asyncworking.models.Project;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
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
//                .id(1L)
                .categoryName("firstCategory")
                .emoji("👋")
                .build();

        mockSecondMessageCategory = MessageCategory.builder()
                .project(mockProject)
//                .id(2L)
                .categoryName("secondCategory")
                .emoji("😊")
                .build();

        projectRepository.save(mockProject);
    }

    @Test
    public void shouldReturnMessageCategoryListGivenAnProjectId() {
        messageCategoryRepository.save(mockFirstMessageCategory);
        messageCategoryRepository.save(mockSecondMessageCategory);
        List<MessageCategory> messageCategoryList = messageCategoryRepository.findByCompanyIdAndProjectId
                (mockProject.getCompanyId(), mockProject.getId());
        assertEquals(2, messageCategoryList.size());
    }

    @Test
    public void shouldReturn1BecauseOfSuccessfulModification() {
        MessageCategory savedMessageCategory =
                messageCategoryRepository.save(mockFirstMessageCategory);
        int messageCount = messageCategoryRepository.editMessage
                (savedMessageCategory.getId(), "first test", "😊");
        assertEquals(1, messageCount);
    }
}
