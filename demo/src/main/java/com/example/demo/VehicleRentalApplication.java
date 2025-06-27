package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class VehicleRentalApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();
        System.setProperty("STRIPE_API_KEY", dotenv.get("STRIPE_API_KEY"));
		SpringApplication.run(VehicleRentalApplication.class, args);
	}

}
