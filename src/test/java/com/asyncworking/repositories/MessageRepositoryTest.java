//package com.asyncworking.repositories;
//
//import com.asyncworking.models.Category;
//import com.asyncworking.models.Message;
//import com.asyncworking.models.Project;
//import com.asyncworking.models.TodoList;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.OffsetDateTime;
//import java.util.List;
//
//import static java.time.ZoneOffset.UTC;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
////@Transactional
//@ActiveProfiles("test")
//public class MessageRepositoryTest extends DBHelper{
//
//    private Project mockProject;
//
//    private Message mockFirstMessage;
//
//    private Message mockSecondMessage;
//
//
//   @BeforeEach
//    public void createMockData() {
//       clearDb();
//       mockProject = Project.builder()
//                .name("AWProject")
//                .isDeleted(false)
//                .isPrivate(false)
//                .leaderId(1L)
//                .companyId(1L)
//                .createdTime(OffsetDateTime.now(UTC))
//                .updatedTime(OffsetDateTime.now(UTC))
//                .build();
//       mockFirstMessage = Message.builder()
//                .id(5L)
//                .project(mockProject)
//                .companyId(1L)
//                .posterUserId(3L)
//                .messageTitle("first message")
//                .content("first message content")
//                .category(Category.ANNOUNCEMENT)
//                .createdTime(OffsetDateTime.now(UTC))
//                .postTime(OffsetDateTime.now(UTC))
//                .updatedTime(OffsetDateTime.now(UTC))
//                .build();
//       mockSecondMessage = Message.builder()
//                .id(6L)
//                .project(mockProject)
//                .companyId(1L)
//                .posterUserId(3L)
//                .messageTitle("second message")
//                .content("second message content")
//                .category(Category.ANNOUNCEMENT)
//                .postTime(OffsetDateTime.now(UTC))
//                .createdTime(OffsetDateTime.now(UTC))
//                .updatedTime(OffsetDateTime.now(UTC))
//                .build();
//       projectRepository.save(mockProject);
//       messageRepository.save(mockFirstMessage);
//       messageRepository.save(mockSecondMessage);
//    }
//
//    @Test
//    @Transactional
//    public void returnMessageListByProjectId() {
//        List<Message> messageList = messageRepository.findMessageByProjectId(mockProject.getId());
//        assertEquals(2, messageList.size());
//    }
//
//
//}
