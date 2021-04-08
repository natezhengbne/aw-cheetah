package com.asyncworking.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class EmailConfig {

    @Value("http://localhost:3000")
    private String frontendUrl;
}
