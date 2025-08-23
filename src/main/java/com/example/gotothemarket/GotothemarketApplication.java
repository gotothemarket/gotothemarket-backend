package com.example.gotothemarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GotothemarketApplication {

	public static void main(String[] args) {
		// .env 파일 로드
		try {
			io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		} catch (Exception e) {
			System.out.println("No .env file found or error loading .env file");
		}

		SpringApplication.run(GotothemarketApplication.class, args);
	}
}
