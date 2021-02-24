package com.asyncworking.auth;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
public class ApplicationUserServiceTest {

    @MockBean
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
                .status(Status.UNVERIFIED)
                .password(passwordEncoder.encode("len123"))
                .createdTime(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedTime(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        userRepository.saveAndFlush(mockUser);
    }

    @Test
    public void shouldThrowUsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class, () -> {
            applicationUserService.loadUserByUsername("b@asyncworking.com");
        });
    }

    @Test
    public void shouldReturnUserObjectAccordingToEmailGiven() {
        UserDetails userDetails = applicationUserService.loadUserByUsername("a@asyncworking.com");
        assertEquals("a@asyncworking.com", userDetails.getUsername());
        assertEquals("testpass", userDetails.getPassword());
        assertFalse(userDetails.getAuthorities().stream().findFirst().isEmpty());
    }

}
