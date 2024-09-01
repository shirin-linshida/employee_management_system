package com.example.employeeManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index"; // This will resolve to src/main/resources/templates/index.html
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard"; // This will resolve to src/main/resources/templates/admin-dashboard.html
    }

    @GetMapping("/staff/dashboard")
    public String staffDashboard() {
        return "staff-dashboard"; // This will resolve to src/main/resources/templates/staff-dashboard.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // This will resolve to src/main/resources/templates/login.html
    }
}
