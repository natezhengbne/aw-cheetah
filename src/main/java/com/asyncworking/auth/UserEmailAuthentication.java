package com.asyncworking.auth;

import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEmailAuthentication implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<UserEntity> foundUser = userRepository.findUserEntityByEmailIgnoreCase(email);

        if (foundUser.isEmpty()) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }

        log.info(foundUser.get().toString());

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("role:fake");
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);

        log.info(foundUser.get().getPassword());
        return new User(foundUser.get().getEmail(),
                foundUser.get().getPassword().replaceAll("\\s+", ""),
                authorities);
    }
}
