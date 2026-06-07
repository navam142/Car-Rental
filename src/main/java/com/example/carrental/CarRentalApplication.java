package com.example.carrental;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class CarRentalApplication {

    public static void main(String[] args) {

        Logger logger =  Logger.getLogger(CarRentalApplication.class.getName());

        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();
            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load env variables", e);
            System.exit(1);
        }
        System.getProperties().forEach((key, value) -> logger.info(key + ": " + value));
        SpringApplication.run(CarRentalApplication.class, args);
    }

}
