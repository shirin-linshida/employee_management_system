package com.example.employeeManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;

@RestController
@RequestMapping("/api")
public class HealthCheckController {

    @GetMapping("/greet")
    public String greet(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();

        // Get current time
        LocalTime now = LocalTime.now();

        // Determine the time-based greeting
        String greeting;
        if (now.isBefore(LocalTime.NOON)) {
            greeting = "Good morning";
        } else if (now.isBefore(LocalTime.of(17, 0))) {
            greeting = "Good afternoon";
        } else {
            greeting = "Good evening";
        }

        // Return the greeting message
        return String.format("%s from IP: %s", greeting, clientIp);
    }
}
