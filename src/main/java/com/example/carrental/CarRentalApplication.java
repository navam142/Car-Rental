package com.example.carrental;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarRentalApplication {

    public static void main(String[] args) {

        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .load();
            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.getProperties().forEach((key, value) -> System.out.println(key + ": " + value));
        SpringApplication.run(CarRentalApplication.class, args);
    }

}
