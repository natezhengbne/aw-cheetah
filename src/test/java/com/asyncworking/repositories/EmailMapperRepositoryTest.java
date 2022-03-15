package com.asyncworking.repositories;

import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class EmailMapperRepositoryTest extends DBHelper {

    private UserEntity mockUserEntity;

    private EmailSendRecord mockEmailSendRecord;

    @Test
    public void giveEmailInfo_whenSavedAndRetrievesRecord_thenOk() {
        EmailSendRecord savedEmailSendRecord = emailSendRepository.save(mockEmailSendRecord);
        Assertions.assertNotNull(savedEmailSendRecord);
        Assertions.assertEquals("test0@gmail.com", savedEmailSendRecord.getReceiver());
    }
}
