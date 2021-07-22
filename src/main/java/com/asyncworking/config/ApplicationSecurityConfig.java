package com.asyncworking.config;

import com.asyncworking.auth.ApplicationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApplicationUserService myUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/company", "/signup", "/invitations/companies",
                        "/invitations/register", "/resend", "/verify","/emailsent",
                        "/companies", "/companies/company-info", "/companies/{companyId}",
                        "/companies/{companyId}/profile", "/companies/{companyId}/employees",
                        "/companies/{companyId}/available-employees",
                        "/projects", "/projects/{companyId}", "/projects/{projectId}/project-info", "/projects/{projectid}/members",
                        "/projects/{projectId}/todolists", "/projects/{projectId}/todolists/{todolistId}/todoitems",
                        "/projects/{projectId}/todolists/{todolistId}",
                        "/projects/{projectId}/todoitems/{todoitemId}",
                        "/projects/{projectId}/todoitems/{todoitemId}",
                        "/projects/{projectId}/todoitems/{todoitemId}/completed",
                        "/projects/{projectId}/messages",
                        "/projects/{projectId}/messages/{messageId}",
                        "/projects/{projectId}/message-categories"
                )
                .permitAll()
                .antMatchers("/", "index", "/css/*", "/actuator/*")
                .permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(myUserDetailsService);
        return provider;
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

