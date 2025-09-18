package com.example.gotothemarket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GoToTheMarket API")
                        .version("1.0")
                        .description("전통시장 API"))
                // 로컬 서버 추가
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local development server"))
                .addServersItem(new Server()
                        .url("https://api.gotothemarket.site")
                        .description("Production server"));
    }
}