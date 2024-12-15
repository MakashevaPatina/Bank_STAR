package com.starbank.recommendation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.starbank.recommendation_service",
		"com.starbank.telegram_bot"
})
public class CombinedApplication {

	public static void main(String[] args) {
		SpringApplication.run(CombinedApplication.class, args);
	}
}