package com.example.employeeManagement.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class EmployeeSecurity {
    public boolean hasEditPermission(String id, Authentication authentication) {
        // Implement your permission logic here
        return true; // Or false based on the logic
    }
}
