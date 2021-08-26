package com.asyncworking.auth;

import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.constants.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationUserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationUserService applicationUserService;

    @BeforeEach
    public void insertMockEmp() {
        userRepository.deleteAll();
        when(passwordEncoder.encode("len123")).thenReturn("testpass");
        UserEntity mockUser = UserEntity.builder()
                .id(1L)
                .name("Lengary")
                .email("a@asyncworking.com")
                .title("Frontend Developer")
                .status(Status.ACTIVATED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();

        userRepository.saveAndFlush(mockUser);
    }

    @Test
    public void shouldThrowUsernameNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> applicationUserService.loadUserByUsername("b@asyncworking.com"));
    }

    @Test
    public void shouldReturnUserObjectAccordingToEmailGiven() {
        UserDetails userDetails = applicationUserService.loadUserByUsername("a@asyncworking.com");
        assertEquals("a@asyncworking.com", userDetails.getUsername());
        assertEquals("testpass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().findFirst().isEmpty());
    }
}
