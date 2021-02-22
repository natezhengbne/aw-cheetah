package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
public class UserInfoDtoRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldAddUserEntityIntoDBSuccessfullyGivenProperUserEntity() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .createdTime(OffsetDateTime.now())
                .email("skykk0128@gmail.com")
                .name("Steven")
                .password("password")
                .status(Status.UNVERIFIED)
                .title("Developer")
                .updatedTime(OffsetDateTime.now()).build();
        UserEntity returnedUserEntity = userRepository.save(userEntity);
        Assertions.assertEquals("Steven", returnedUserEntity.getName());
        Assertions.assertEquals("skykk0128@gmail.com", returnedUserEntity.getEmail());
    }
}
