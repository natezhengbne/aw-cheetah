package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
public class UserRepositoryTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void insertMockEmp() throws InterruptedException {
        userRepository.deleteAll();

        when(passwordEncoder.encode("len123")).thenReturn("testpass");

        UserEntity activatedMockUser = UserEntity.builder()
                .name("Lengary")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("len123"))
                .build();

        UserEntity unverifiedMockUser = UserEntity.builder()
                .name("Plus")
                .email("p@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.UNVERIFIED)
                .password(passwordEncoder.encode("len123"))
                .build();

        userRepository.save(activatedMockUser);
        userRepository.save(unverifiedMockUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldAddUserEntityIntoDBSuccessfullyGivenProperUserEntity() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("skykk0128@gmail.com")
                .name("Steven")
                .password("password")
                .status(Status.UNVERIFIED)
                .title("Developer")
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
                .build();
        Company mockCompany = Company.builder()
                .id(1L)
                .name("AW")
                .adminId(1L)
                .build();
        Employee mockEmployee = Employee.builder()
                .id(new EmployeeId(mockUserEntity.getId(), mockCompany.getId()))
                .company(mockCompany)
                .userEntity(mockUserEntity)
                .title("kkk")
                .build();
        employeeRepository.save(mockEmployee);
        Optional<UserEntity> userEntity = userRepository.findEmploymentByEmail("lengary@qq.com");
        assertEquals("testpass", userEntity.get().getPassword().trim());
    }
}
