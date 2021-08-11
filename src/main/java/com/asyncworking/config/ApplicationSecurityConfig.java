package com.asyncworking.config;

import com.asyncworking.auth.ApplicationUserService;
import com.asyncworking.auth.AuthEntryPoint;
import com.asyncworking.jwt.JwtService;
import com.asyncworking.jwt.JwtTokenVerifyFilter;
import com.asyncworking.jwt.JwtUsernameAndPasswordAuthFilter;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.SecretKey;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApplicationUserService applicationUserService;

    private final SecretKey secretKey;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @SneakyThrows
    protected void configure(HttpSecurity http) {
        http
                .csrf().disable()
                .cors().configurationSource(request -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOrigins(List.of("http://localhost:3000",
                    "http://www.asyncworking.com",
                    "https://www.asyncworking.com"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        })
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthFilter(authenticationManager(), jwtService, userRepository))
                .addFilterAfter(new JwtTokenVerifyFilter(secretKey), JwtUsernameAndPasswordAuthFilter.class)
                .authorizeRequests()
                .antMatchers(GET, "/{companyId:^[1-9]\\d*$}/projects/{projectId:^[1-9]\\d*$}/**")
                .access("@guard.checkProjectAccessGetMethod(authentication, #companyId, #projectId)")
                .antMatchers("/{companyId:^[1-9]\\d*$}/projects/{projectId:^[1-9]\\d*$}/**")
                .access("@guard.checkProjectAccessOtherMethods(authentication, #companyId, #projectId)")
                .antMatchers("/companies/{companyId:^[1-9]\\d*$}/**").access("@guard.checkCompanyAccess(authentication, #companyId)")
                .antMatchers("/projects/{projectId:^[1-9]\\d*$}/**").access("@guard.checkProjectAccess(authentication, #projectId)")
                .antMatchers("/", "/resend", "/signup", "/password", "/invitations/**", "/verify", "index", "/css/*", "/actuator" +
                        "/*")
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
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

