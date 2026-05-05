package com.example.carrental.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class DotenvPropertySourceFactory extends DefaultPropertySourceFactory {
    @Override
    public org.springframework.core.env.PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        Map<String, Object> properties = new HashMap<>();
        dotenv.entries().forEach(entry ->
                properties.put(entry.getKey(), entry.getValue())
        );

        return new MapPropertySource("dotenv", properties);
    }
}

@Configuration
@PropertySource(value = "file:.env", factory = DotenvPropertySourceFactory.class, ignoreResourceNotFound = true)
public class EnvConfig {

}