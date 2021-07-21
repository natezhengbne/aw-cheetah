package com.asyncworking.config;

import com.asyncworking.auth.ApplicationUserService;
import com.asyncworking.jwt.JwtConfig;
import com.asyncworking.jwt.JwtTokenVerifier;
import com.asyncworking.jwt.JwtUsernameAndPasswordAuthFilter;
import com.asyncworking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApplicationUserService myUserDetailsService;


    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;


    @SneakyThrows
    protected void configure(HttpSecurity http) {
        http
                .csrf().disable()
                .cors().configurationSource(request -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOrigins(List.of("http://localhost:3000"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        })
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthFilter(authenticationManager(), secretKey, userRepository))
                .addFilterAfter(new JwtTokenVerifier(secretKey), JwtUsernameAndPasswordAuthFilter.class)
                .authorizeRequests()
//                .antMatchers("/login", "/company", "/signup", "/invitations/companies",
//                        "/invitations/register", "/resend", "/verify",
//                        "/companies", "/companies/company-info", "/companies/{companyId}",
//                        "/companies/{companyId}/profile", "/companies/{companyId}/employees",
//                        "/companies/{companyId}/available-employees",
//                        "/projects", "/projects/{companyId}", "/projects/{projectId}/project-info", "/projects/{projectid}/members",
//                        "/projects/{projectId}/todolists", "/projects/{projectId}/todolists/{todolistId}/todoitems",
//                        "/projects/{projectId}/todolists/{todolistId}",
//                        "/projects/{projectId}/todoitems/{todoitemId}",
//                        "/projects/{projectId}/todoitems/{todoitemId}",
//                        "/projects/{projectId}/todoitems/{todoitemId}/completed",
//                        "/projects/{projectId}/messages",
//                        "/projects/{projectId}/messages/{messageId}",
//                        "/projects/{projectId}/message-categories"
//                )
//                .permitAll()
                .antMatchers("/","/resend", "index", "/css/*", "/actuator/*")
                .permitAll()
                .anyRequest()
                .authenticated();
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

