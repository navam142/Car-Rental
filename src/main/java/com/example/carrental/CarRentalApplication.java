package com.example.carrental;

import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarRentalApplication {

    public static void main(String[] args) {
        io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
                .filename("credentials.env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        for (DotenvEntry entry : dotenv.entries()) {
            String key = entry.getKey();
            if (System.getenv(key) == null && System.getProperty(key) == null) {
                System.setProperty(key, entry.getValue());
            }
        }

        SpringApplication.run(CarRentalApplication.class, args);
    }

}
