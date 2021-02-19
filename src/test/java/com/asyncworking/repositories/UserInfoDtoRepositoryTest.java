package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
public class UserInfoDtoRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldAddUserEntityIntoDBSuccessfullyGivenProperUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("Steven");
        userEntity.setEmail("skykk0128@gmail.com");
        UserEntity returnedUserEntity = userRepository.save(userEntity);
        Assertions.assertEquals("Steven", returnedUserEntity.getName());
        Assertions.assertEquals("skykk0128@gmail.com", returnedUserEntity.getEmail());
    }
}
