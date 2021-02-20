package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldAddUserIntoSuccessfullyPropertyUserObject() {

        UserEntity mockUserEntity = UserEntity.builder()
                .id(1L)
                .email("KajjiXin@133.com")
                .name("KaiXnin")
                .title("dev")
                .password("$2y$10$XbhxiobJbdZ/vcJapMHU/.UK4PKStLEVpPM8eth6CYXd2hW99EWRO ")
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        UserEntity returnedUerEntity = userRepository.save(mockUserEntity);
        assertEquals(mockUserEntity.getName(), returnedUerEntity.getName());
        assertEquals(mockUserEntity.getEmail(), returnedUerEntity.getEmail());
    }
}
