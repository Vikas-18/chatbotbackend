package com.hostelchatbot.hostelchatbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HostelchatbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostelchatbotApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(@Value("${spring.data.mongodb.uri}") String uri) {
		return args -> System.out.println("Mongo URI = " + uri);
	}
}