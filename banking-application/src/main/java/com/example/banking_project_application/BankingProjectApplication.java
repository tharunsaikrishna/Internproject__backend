package com.example.banking_project_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.banking_project_application.repositories")
@EntityScan(basePackages = "com.example.banking_project_application.entity")


public class BankingProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingProjectApplication.class, args);
	}

}
