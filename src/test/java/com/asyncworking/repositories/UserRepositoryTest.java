package com.asyncworking.repositories;

import com.asyncworking.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest extends DBHelper {

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void insertMockEmp() {
        clearDb();

        when(passwordEncoder.encode("len123")).thenReturn("testpass");

        UserEntity activatedMockUser = UserEntity.builder()
                .name("Lengary")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        UserEntity unverifiedMockUser = UserEntity.builder()
                .name("Plus")
                .email("p@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.UNVERIFIED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        userRepository.save(activatedMockUser);
        userRepository.save(unverifiedMockUser);
    }

    @Test
    public void shouldAddUserEntityIntoDBSuccessfullyGivenProperUserEntity() {
        UserEntity userEntity = UserEntity.builder()
                .id(3L)
                .email("skykk0128@gmail.com")
                .name("Steven")
                .password("password")
                .status(Status.UNVERIFIED)
                .title("Developer")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        UserEntity returnedUserEntity = userRepository.save(userEntity);
        Assertions.assertEquals("Steven", returnedUserEntity.getName());
        Assertions.assertEquals("skykk0128@gmail.com", returnedUserEntity.getEmail());
    }

    @Test
    public void shouldFindUserExistByEmail() {
        Optional<UserEntity> returnedActivatedUserEntity = userRepository
                .findByEmail("a@asyncworking.com");
        Optional<UserEntity> returnedUnverifiedUserEntity = userRepository.findByEmail("p@asyncworking.com");
        assertEquals("testpass", returnedActivatedUserEntity.get().getPassword().trim());
    }

    @Test
    public void shouldFindUserByEmail() {
        Optional<UserEntity> returnedActivatedUserEntity = userRepository
                .findByEmail("a@asyncworking.com");
        Optional<UserEntity> returnedUnverifiedUserEntity = userRepository.findByEmail("p@asyncworking.com");
        assertEquals("testpass", returnedActivatedUserEntity.get().getPassword().trim());
    }

    @Test
    public void shouldFindUnverifiedUserExistByEmail() {
        Optional<UserEntity> activatedUserEntity = userRepository
                .findUnverifiedStatusByEmail("p@asyncworking.com");
        assertEquals("testpass", activatedUserEntity.get().getPassword().trim());
    }

    @Test
    public void shouldAddUserIntoSuccessfullyPropertyUserObject() {
        UserEntity mockUserEntity = UserEntity.builder()
                .email("KajjiXin@133.com")
                .name("KaiXnin")
                .title("dev")
                .password("$2y$10$XbhxiobJbdZ/vcJapMHU/.UK4PKStLEVpPM8eth6CYXd2hW99EWRO")
                .status(Status.UNVERIFIED)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        UserEntity returnedUerEntity = userRepository.save(mockUserEntity);
        assertEquals(mockUserEntity.getName(), returnedUerEntity.getName());
        assertEquals(mockUserEntity.getEmail(), returnedUerEntity.getEmail());
    }

    @Test
    public void shouldReturnEmptyDueToWrongEmail() {
        Optional<UserEntity> userEntity = userRepository.findUserEntityByEmail("b@asyncworking.com");
        assertTrue(userEntity.isEmpty());
    }

    @Test
    @Transactional
    public void shouldUpdateStatusToActivatedByEmail() {
        int number = userRepository.updateStatusByEmail("a@asyncworking.com", Status.ACTIVATED);
        assertEquals(1, number);
    }

    @Test void shouldReturnEmptyDueToNoCompanyUserEmail() {
        Optional<UserEntity> userEntity = userRepository.findEmploymentByEmail("b@asyncworking.com");
        assertTrue(userEntity.isEmpty());
    }

    @Test void shouldFindUserDueToEmployeeEmail() {
        UserEntity mockUserEntity = UserEntity.builder()
                .id(1L)
                .name("Lengary")
                .email("lengary@qq.com")
                .title("Frontend Developer")
                .status(Status.UNVERIFIED)
                .password("testpass")
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        Company mockCompany = Company.builder()
                .id(1L)
                .name("AW")
                .adminId(1L)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        Employee mockEmployee = Employee.builder()
                .id(new EmployeeId(mockUserEntity.getId(), mockCompany.getId()))
                .company(mockCompany)
                .userEntity(mockUserEntity)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .title("kkk")
                .build();
        employeeRepository.save(mockEmployee);
        Optional<UserEntity> userEntity = userRepository.findEmploymentByEmail("lengary@qq.com");
        assertEquals("testpass", userEntity.get().getPassword().trim());
    }

    @Test
    public void shouldReturnEmptyDueToWrongUserId() {
        Optional<UserEntity> returnedUserEntity = userRepository.findUserEntityById(300L);
        assertTrue(returnedUserEntity.isEmpty());
    }

    @Test
    public void shouldFindUserByUserId() {
        UserEntity mockUser = UserEntity.builder()
                .name("new")
                .email("new@asyncworking.com")
                .title("BA")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
        userRepository.save(mockUser);
        Optional<UserEntity> returnedUserEntity = userRepository.findUserEntityById(mockUser.getId());
        assertEquals("testpass", returnedUserEntity.get().getPassword().trim());
    }
}
