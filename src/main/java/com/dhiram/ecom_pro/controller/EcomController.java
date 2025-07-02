package com.dhiram.ecom_pro.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class EcomController {
    @GetMapping()
    public String welcomeMessage(@RequestParam(defaultValue = "guest") String param) {
        return "Hey " + param + ", welcome to the E-Commerce Project backend! 🚀";
    }

}
