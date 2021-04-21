package com.asyncworking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins(
						"http://localhost:3000",
						"http://www.asyncworking.com",
						"https://www.asyncworking.com"
				)
				.allowedMethods("GET", "POST", "PUT", "OPTIONS", "HEAD");
	}
}
