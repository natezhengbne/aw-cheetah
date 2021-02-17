package com.asyncworking.auth;

import java.util.ArrayList;
import java.util.List;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findUserEntityByName(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }

        System.out.println(userEntity);

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("role:fake");
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);

        System.out.println(userEntity.getPassword());
        return new User(userEntity.getName(),
                userEntity.getPassword().replaceAll("\\s+", ""),
                authorities);
    }
}
