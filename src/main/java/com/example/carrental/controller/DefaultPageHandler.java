package com.example.carrental.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultPageHandler {
    @Value("${frontend.url}")
    private String frontendUrl;

    @GetMapping("/")
    public String home() {
        return "redirect:" + frontendUrl;
    }
}
