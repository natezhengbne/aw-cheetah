package com.asyncworking.auth;

import java.util.ArrayList;
import java.util.List;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationUserService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity foundUser = mapToUserDetails(email);

        log.info(foundUser.toString());

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("edit message");
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);

        log.info(foundUser.getPassword());
        return new User(foundUser.getEmail(),
                foundUser.getPassword().replaceAll("\\s+", ""),
                authorities);
    }

    private UserEntity mapToUserDetails (String email) {
        return userRepository.findUserEntityByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Username %s not found", email)));
    }
}

