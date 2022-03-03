package com.asyncworking.jwt;

import com.asyncworking.auth.ApplicationUserService;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.ProjectUserRepository;
import com.asyncworking.repositories.UserRepository;
import com.google.gson.internal.LinkedTreeMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.asyncworking.jwt.JwtClaims.*;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecretKey secretKey;
    private final ApplicationUserService applicationUserService;

    private final EmployeeRepository employeeRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;

    public String createJwtToken(String email) {
        UserDetails user = applicationUserService.loadUserByUsername(email);
        UserEntity userEntity = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with email: " + email));
        Set<Long> companyIds = employeeRepository.findCompanyIdByUserId(userEntity.getId());
        Set<Long> projectIds = projectUserRepository.findProjectIdByUserId(userEntity.getId());
        return Jwts.builder()
                .setSubject(email)
                .claim(AUTHORITIES.value(), user.getAuthorities())
                .claim(COMPANY_IDS.value(), companyIds)
                .claim(PROJECT_IDS.value(), projectIds)
                .claim(USER_ID.value(), userEntity.getId())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .signWith(secretKey)
                .compact();
    }

    public String createJwtToken(UserEntity userEntity, Collection<? extends GrantedAuthority> authorities) {
        Set<Long> companyIds = employeeRepository.findCompanyIdByUserId(userEntity.getId());
        Set<Long> projectIds = projectUserRepository.findProjectIdByUserId(userEntity.getId());
        return Jwts.builder()
                .setSubject(userEntity.getEmail())
                .claim(AUTHORITIES.value(), authorities)
                .claim(COMPANY_IDS.value(), companyIds)
                .claim(PROJECT_IDS.value(), projectIds)
                .claim(USER_ID.value(), userEntity.getId())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .signWith(secretKey)
                .compact();
    }

    public JwtDto refreshJwtToken(String auth) {
        String oldToken = auth.replace(AUTHORIZATION_TYPE.value(), "");

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(oldToken);
        Claims body = claimsJws.getBody();
        String email = body.getSubject();

        var authorities = (List<LinkedTreeMap<String, Object>>) body.get(AUTHORITIES.value());
        UserEntity userEntity = userRepository.findUserEntityByEmail(email).get();
        Set<Long> companyIds = employeeRepository.findCompanyIdByUserId(userEntity.getId());
        Set<Long> projectIds = projectUserRepository.findProjectIdByUserId(userEntity.getId());
        UserDetails user = applicationUserService.loadUserByUsername(email);
        if (authorities.size() == user.getAuthorities().size()
                && getIdSetFromJwtBody(body, COMPANY_IDS.value()).equals(companyIds)
                && getIdSetFromJwtBody(body, PROJECT_IDS.value()).equals(projectIds)) {
            return JwtDto.builder()
                    .accessToken(oldToken)
                    .message("No need to refresh the jwtToken.")
                    .build();
        }

        String newToken = createJwtToken(email);
        return JwtDto.builder()
                .accessToken(newToken)
                .message("JwtToken has already refreshed.")
                .build();
    }

    private Set<Long> getIdSetFromJwtBody(Claims body, String idType) {
        var doubleIdSet = (List<Double>) body.get(idType);
        return doubleIdSet.stream().
                map(Double::longValue)
                .collect(Collectors.toSet());
    }


    public long getUserIdFromJwt(String auth) {
        String oldToken = auth.replace(AUTHORIZATION_TYPE.value(), "");

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(oldToken);
        Claims body = claimsJws.getBody();
        Double userId = (Double) body.get(USER_ID.value());
        return userId.longValue();
    }
}
