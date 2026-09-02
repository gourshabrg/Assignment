package com.example.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Order Service is running. Try POST /api/orders or GET /api/orders/{id}";
    }
}
