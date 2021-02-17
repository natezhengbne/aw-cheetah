package com.asyncworking.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import javax.persistence.EntityManager;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
public class UserRepositoryTest {

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void insertMockEmp() {
        UserEntity mockUser = UserEntity.builder()
                .id(1)
                .name(passwordEncoder.encode("Lengary"))
                .email("lengary@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.Unverified)
                .password("len123")
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        entityManager.persist(mockUser);
        entityManager.flush();
    }

    @Test
    public void shouldFindUserByName() {
        UserEntity userEntity = userRepository.findUserEntityByName("Lengary");
        assertEquals("Frontend Developer", userEntity.getTitle());
    }
}
