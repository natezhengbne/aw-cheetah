package com.asyncworking.config;

import com.asyncworking.auth.AwcheetahGrantedAuthority;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

    @Bean
    @Primary
    public InMemoryUserDetailsManager userDetailsService(){
        GrantedAuthority companyManagerAuthority = new AwcheetahGrantedAuthority("Company Manager", 1L);
        GrantedAuthority projectManagerAuthority = new AwcheetahGrantedAuthority("Project Manager", 1L);
        UserDetails companyManager = new User("company manager", "pas123", Arrays.asList(companyManagerAuthority));
        UserDetails projectManager = new User("project manager", "pas123", Arrays.asList(projectManagerAuthority));
        return new InMemoryUserDetailsManager(Arrays.asList(companyManager, projectManager));
    }
}
