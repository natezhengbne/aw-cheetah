package com.asyncworking.auth;

import java.util.Set;
import java.util.stream.Collectors;

import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.UserEntity;
import com.asyncworking.models.UserRole;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationUserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity user = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with email: " + email));

        Set<UserRole> userRoles = userRoleRepository.findByUserEntity(user);
        Set<GrantedAuthority> grantedAuthorities = userRoles.stream()
                .map(userRole -> new AwcheetahGrantedAuthority(userRole.getRole().getName(), userRole.getId().getTargetId()))
                .collect(Collectors.toSet());

        return new ApplicationUserDetails(user.getEmail(),
                user.getPassword().replaceAll("\\s+", ""),
                grantedAuthorities,
                true,
                true,
                true,
                true);
    }

}

