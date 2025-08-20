package com.example.gotothemarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 API 경로에 대해
                .allowedOrigins("*")  // 모든 도메인 허용 (대회용이므로)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);  // 쿠키/인증 정보는 비허용
        // Swagger 전용 설정
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("*");
        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("*");
    }
}