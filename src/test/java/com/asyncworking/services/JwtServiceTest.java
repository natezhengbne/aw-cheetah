package com.asyncworking.services;

import com.asyncworking.auth.ApplicationUserDetails;
import com.asyncworking.auth.ApplicationUserService;
import com.asyncworking.jwt.JwtDto;
import com.asyncworking.jwt.JwtService;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.ProjectUserRepository;
import com.asyncworking.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import static com.asyncworking.jwt.JwtClaims.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @Mock
    private ApplicationUserService applicationUserService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private UserRepository userRepository;

    private JwtService jwtService;

    private UserDetails mockUserDetails;

    private UserEntity mockUser;

    private Set<? extends GrantedAuthority> mockAuthorities;

    private Set<Long> companyIds;

    private Set<Long> projectIds;

    private String secretKey = "ssssssssssssssssssssssssssssssss";

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @BeforeEach
    public void setUp() {

        jwtService = new JwtService(secretKey(),
                applicationUserService,
                employeeRepository,
                projectUserRepository,
                userRepository
        );

        mockUser = UserEntity.builder()
                .id(1L)
                .email("a@asyncworking.com")
                .build();

        mockAuthorities = new HashSet<>();

        mockUserDetails = new ApplicationUserDetails("a@asyncworking.com",
                "password",
                mockAuthorities,
                true,
                true,
                true,
                true
        );

        companyIds = new HashSet<>();
        projectIds = new HashSet<>();
    }

    @Test
    public void shouldGenerateJwtTokenGivenEmail() {
        String email = "a@asyncworking.com";
        when(applicationUserService.loadUserByUsername(email)).thenReturn(mockUserDetails);
        when(userRepository.findUserEntityByEmail(email)).thenReturn(java.util.Optional.ofNullable(mockUser));
        when(employeeRepository.findCompanyIdByUserId(1L)).thenReturn(companyIds);
        when(projectUserRepository.findProjectIdByUserId(1L)).thenReturn(projectIds);

        String accessToken = jwtService.creatJwtToken("a@asyncworking.com");
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey())
                .build()
                .parseClaimsJws(accessToken);
        assertEquals(claimsJws.getBody().getSubject(), email);
    }

    @Test
    public void shouldGenerateJwtTokenGivenUserAndAuthorities() {
        String email = "a@asyncworking.com";
        when(employeeRepository.findCompanyIdByUserId(1L)).thenReturn(companyIds);
        when(projectUserRepository.findProjectIdByUserId(1L)).thenReturn(projectIds);

        String accessToken = jwtService.creatJwtToken(mockUser, mockAuthorities);
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey())
                .build()
                .parseClaimsJws(accessToken);
        assertEquals(claimsJws.getBody().getSubject(), email);
    }

    @Test
    public void shouldNotRefreshJwtToken() {
        String email = "a@asyncworking.com";
        when(employeeRepository.findCompanyIdByUserId(1L)).thenReturn(companyIds);
        when(projectUserRepository.findProjectIdByUserId(1L)).thenReturn(projectIds);
        when(applicationUserService.loadUserByUsername(email)).thenReturn(mockUserDetails);

        String accessToken = jwtService.creatJwtToken(mockUser, mockAuthorities);
        String auth = AUTHORIZATION_TYPE.value() + accessToken;
        JwtDto jwtDto = jwtService.refreshJwtToken(auth);
        assertEquals(jwtDto.getAccessToken(), accessToken);
    }

    @Test
    public void shouldRefreshJwtToken() {
        String email = "a@asyncworking.com";
        Set<SimpleGrantedAuthority> mockAuthorities = new HashSet<>();
        mockAuthorities.add(new SimpleGrantedAuthority("Company Manager"));
        when(userRepository.findUserEntityByEmail(email)).thenReturn(java.util.Optional.ofNullable(mockUser));
        when(employeeRepository.findCompanyIdByUserId(1L)).thenReturn(companyIds);
        when(projectUserRepository.findProjectIdByUserId(1L)).thenReturn(projectIds);
        when(applicationUserService.loadUserByUsername(email)).thenReturn(mockUserDetails);

        String accessToken = jwtService.creatJwtToken(mockUser, mockAuthorities);
        String auth = AUTHORIZATION_TYPE.value() + accessToken;
        JwtDto jwtDto = jwtService.refreshJwtToken(auth);
        assertEquals(jwtDto.getMessage(), "JwtToken has already refreshed.");
    }
}
