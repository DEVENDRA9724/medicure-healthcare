package com.project.staragile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedicureApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicureApplication.class, args);
        System.out.println("=== Medicure Healthcare Application Started Successfully ===");
        System.out.println("API Endpoints available at: http://localhost:8080/api/doctors");
    }
}