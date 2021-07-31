package com.asyncworking.config;

import com.asyncworking.auth.ApplicationUserService;
import com.asyncworking.auth.AuthEntryPoint;
import com.asyncworking.jwt.JwtTokenVerifier;
import com.asyncworking.jwt.JwtUsernameAndPasswordAuthFilter;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.ProjectUserRepository;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.SecretKey;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApplicationUserService myUserDetailsService;

    private final SecretKey secretKey;

    private final UserRepository userRepository;

    private final EmployeeRepository employeeRepository;

    private final ProjectUserRepository projectUserRepository;

    @SneakyThrows
    protected void configure(HttpSecurity http) {
        http
                .csrf().disable()
                .cors().configurationSource(request -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOrigins(List.of("http://localhost:3000", "http://www.asyncworking.com", "https://www.asyncworking.com"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        })
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthFilter(authenticationManager(), secretKey,
                        userRepository, employeeRepository, projectUserRepository))
                .addFilterAfter(new JwtTokenVerifier(secretKey), JwtUsernameAndPasswordAuthFilter.class)
                .authorizeRequests()
                .antMatchers("/companies/{companyId:[\\d+]}/**").access("@guard.checkCompanyId(authentication, #companyId)")
                .antMatchers("/projects/{projectId:[\\d+]}/**").access("@guard.checkProjectId(authentication, #projectId)")
                .antMatchers(HttpMethod.GET, "/{companyId:[\\d+]}/projects/{projectId:[\\d+]}/messages/{messageId:[\\d+]}/**")
                .access("@guard.checkMessageAccessGetMethod(authentication, #companyId, #projectId, #messageId)")
                .antMatchers( "/{companyId:[\\d+]}/projects/{projectId:[\\d+]}/messages/{messageId:[\\d+]}/**")
                .access("@guard.checkMessageAccessOtherMethods(authentication, #companyId, #projectId, #messageId)")
                .antMatchers(HttpMethod.GET,"/{companyId:[\\d+]}/projects/{projectId:[\\d+]}/**")
                .access("@guard.checkProjectAccessGetMethod(authentication, #companyId, #projectId)")
                .antMatchers("/{companyId:[\\d+]}/projects/{projectId:[\\d+]}/**")
                .access("@guard.checkProjectAccessOtherMethods(authentication, #companyId, #projectId)")
                .antMatchers("/", "/resend", "/signup", "/invitations/**", "/verify", "index", "/css/*", "/actuator/*")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(new AuthEntryPoint());
    }

    @Override
    @SneakyThrows
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(myUserDetailsService);
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

