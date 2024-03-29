package com.asyncworking.services;

import com.asyncworking.auth.ApplicationUserDetails;
import com.asyncworking.auth.ApplicationUserService;
import com.asyncworking.auth.AwcheetahGrantedAuthority;
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
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.HashSet;
import java.util.Set;

import static com.asyncworking.jwt.JwtClaims.AUTHORIZATION_TYPE;
import static com.asyncworking.models.RoleNames.COMPANY_MANAGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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

    private SecretKey secretKey() {
        String secretKey = "ssssssssssssssssssssssssssssssss";
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

        String accessToken = jwtService.createJwtToken(email);
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

        String accessToken = jwtService.createJwtToken(mockUser, mockAuthorities);
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey())
                .build()
                .parseClaimsJws(accessToken);

        assertEquals(claimsJws.getBody().getSubject(), email);
    }

    @Test
    public void shouldNotRefreshJwtTokenGivenAuthoritiesNotChange() {
        String email = "a@asyncworking.com";
        when(employeeRepository.findCompanyIdByUserId(1L)).thenReturn(companyIds);
        when(projectUserRepository.findProjectIdByUserId(1L)).thenReturn(projectIds);
        when(applicationUserService.loadUserByUsername(email)).thenReturn(mockUserDetails);

        String accessToken = jwtService.createJwtToken(mockUser, mockAuthorities);
        String auth = AUTHORIZATION_TYPE.value() + accessToken;
        JwtDto jwtDto = jwtService.refreshJwtToken(auth);

        assertEquals(jwtDto.getMessage(), "No need to refresh the jwtToken.");
    }

    @Test
    public void shouldRefreshJwtTokenGivenAuthoritiesChange() {
        String email = "a@asyncworking.com";
        Set<GrantedAuthority> mockAuthorities = new HashSet<>();
        mockAuthorities.add(new AwcheetahGrantedAuthority(COMPANY_MANAGER.value(), 1L));

        when(userRepository.findUserEntityByEmail(email)).thenReturn(java.util.Optional.ofNullable(mockUser));
        when(employeeRepository.findCompanyIdByUserId(1L)).thenReturn(companyIds);
        when(projectUserRepository.findProjectIdByUserId(1L)).thenReturn(projectIds);
        when(applicationUserService.loadUserByUsername(email)).thenReturn(mockUserDetails);

        String accessToken = jwtService.createJwtToken(mockUser, mockAuthorities);
        String auth = AUTHORIZATION_TYPE.value() + accessToken;
        JwtDto jwtDto = jwtService.refreshJwtToken(auth);

        assertEquals(jwtDto.getMessage(), "JwtToken has already refreshed.");
    }

    @Test
    public void testGetUserIdFromJwtShouldBeOK() {
        String accessToken = jwtService.createJwtToken(mockUser, mockAuthorities);
        String auth = AUTHORIZATION_TYPE.value() + accessToken;

        long userId = jwtService.getUserIdFromJwt(auth);

        assertEquals(mockUser.getId(), userId);
    }
}
