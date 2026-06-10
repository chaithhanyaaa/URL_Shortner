package com.chaithanya.redirect_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RedirectServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedirectServiceApplication.class, args);
	}

}
