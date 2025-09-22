package fr.monclub.club_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClubServiceApplication {

	public static void main(String[] args) {
        System.out.println("POSTGRES_USER=" + System.getenv("POSTGRES_USER"));
        SpringApplication.run(ClubServiceApplication.class, args);
	}

}
