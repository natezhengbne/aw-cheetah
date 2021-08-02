package com.asyncworking.config;

import com.asyncworking.auth.AuthPermissionEvaluator;
import com.asyncworking.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

    private final RoleRepository roleRepository;

    @Override
    public MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
        methodSecurityExpressionHandler.setPermissionEvaluator(new AuthPermissionEvaluator(roleRepository));
        return methodSecurityExpressionHandler;
    }
}
