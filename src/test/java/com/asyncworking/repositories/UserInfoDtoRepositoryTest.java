package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
public class UserInfoDtoRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private TestEntityManager entityManager;

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

    @Test
    public void shouldFindUserByEmail() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("skykk0128@gmail.com");
        UserEntity returnedUserEntity = entityManager.persistAndFlush(userEntity);
        Assertions.assertEquals(userRepository.findByEmail(userEntity.getEmail()).get(), returnedUserEntity);
    }
}
