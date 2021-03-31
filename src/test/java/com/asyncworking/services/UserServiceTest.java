package com.asyncworking.services;

import com.asyncworking.dtos.AccountDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;

    private UserService userService;

    @BeforeEach()
    void setup() {
        userService = new UserService(userRepository, authenticationManager, userMapper);
        ReflectionTestUtils.setField(userService, "jwtSecret", "securesecuresecuresecuresecuresecuresecure");
    }

    @Test
    public void shouldFindEmailExistSuccessful() {
        AccountDto accountDto = AccountDto.builder()
                .email("a@qq.com")
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder().email("a@gmail.com").build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockReturnedUserEntity));

        String email = accountDto.getEmail();
        assertTrue(userService.ifEmailExists(email));
    }

    @Test
    public void shouldFindUnverifiedUserSuccessful() {
        String email = "a@qq.com";

        UserEntity mockReturnedUserEntity = UserEntity.builder().email("a@gmail.com").status(Status.UNVERIFIED).build();
        when(userRepository.findUnverifiedStatusByEmail(any())).thenReturn(Optional.of(mockReturnedUserEntity));

        assertTrue(userService.ifUnverified(email));
    }

    @Test
    public void shouldFindUnverifiedUserUnsuccessfully() {
        String email = "a@qq.com";

        when(userRepository.findUnverifiedStatusByEmail(any())).thenReturn(Optional.empty());

        assertFalse(userService.ifUnverified(email));
    }

    @Test
    public void shouldLoginSuccessfulAndReturnDto() {
        AccountDto accountDto = AccountDto.builder()
                .email("plus@gmail.com")
                .name("aName")
                .password("password")
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("plus@gmail.com")
                .name("aName")
                .build();

        when(userRepository.findUserEntityByEmail(any())).thenReturn(Optional.of(mockReturnedUserEntity));

        AccountDto returnedUserInfoPostDto = userService.login(
                accountDto.getEmail(), accountDto.getPassword());
        String testName = returnedUserInfoPostDto.getName();

        assertEquals(testName, mockReturnedUserEntity.getName());
    }

    @Test
    public void shouldThrowExceptionWhenUserIsNotExist() {
        AccountDto accountDto = AccountDto.builder()
                .email("plus@gmail.com")
                .name("aName")
                .password("password")
                .build();

        String expectedMessage = "user not found";

        when(userRepository.findUserEntityByEmail(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
            () -> userService.login(accountDto.getEmail(), accountDto.getPassword()));

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void shouldGenerateActivationLinkGivenUserDtoAndHttpServletRequest() {
        AccountDto accountDto = AccountDto.builder()
                .email("user@gmail.com")
                .password("len123")
                .name("user")
                .build();
        String siteUrl = "http://localhost";
        String verifyLink = userService.generateVerifyLink(accountDto.getEmail(), siteUrl);

        assertEquals(
                "http://localhost/verify?code="
                        .concat("eyJhbGciOiJIUzI1NiJ9.")
                        .concat("eyJzdWIiOiJzaWduVXAiLCJlbWFpbCI6InVzZXJAZ21haWwuY29tIn0.")
                        .concat("tC8BAIWlF8U5z5Ue-SPBZBxMUBqLwGeKbbLVCtMTmhw"),
                verifyLink
        );
    }

    @Test
    public void shouldCreateUserAndGenerateActivationLinkGivenProperUserDto() {
        AccountDto accountDto = AccountDto.builder()
                .email("user@gmail.com")
                .password("len123")
                .name("user")
                .build();

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        userService.createUserAndGenerateVerifyLink(accountDto, "http://localhost");

        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();
        assertEquals("user@gmail.com", savedUser.getEmail());
        assertEquals("user", savedUser.getName());
    }

    @Test
    public void shouldReturnTrueIfAccountActivated() {
        String code = "eyJhbGciOiJIUzI1NiJ9."
                .concat("eyJzdWIiOiJzaWduVXAiLCJlbWFpbCI6InVzZXJAZ21haWwuY29tIn0.")
                .concat("tC8BAIWlF8U5z5Ue-SPBZBxMUBqLwGeKbbLVCtMTmhw");

        when(userRepository.updateStatusByEmail("user@gmail.com", Status.ACTIVATED)).thenReturn(1);

        assertTrue(userService.isAccountActivated(code));
    }

    @Test
    public void throwExceptionWhenUserDatabaseWrong() {
        String expectedMessage = "database wrong";

        AccountDto accountDto = AccountDto.builder()
                .email("lengary@asyncworking.com")
                .title("VI")
                .build();

        when(userRepository.findByEmail(accountDto.getEmail()))
                .thenThrow(new RuntimeException(expectedMessage));

        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.ifEmailExists(accountDto.getEmail()));

        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldFindEmploymentExistSuccessful() {
        String email = "a@gmail.com";
        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("a@gmail.com").build();
        when(userRepository.findEmploymentByEmail(anyString()))
                .thenReturn(Optional.of(mockReturnedUserEntity));
        boolean testEmail = userService.ifCompanyExits(email);
        assertTrue(testEmail);
    }
}
