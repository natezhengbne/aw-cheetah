package com.asyncworking.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
public class FrontEndUrlConfig {

    @Value("${url}")
    private String frontEndUrlDeploy;

    @Value("http://localhost:3000")
    private String frontEndUrlDevelop;
}
