package com.example.employeeManagement.controller;

import com.example.employeeManagement.exception.GlobalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.employeeManagement.dto.LoginRequest;
import com.example.employeeManagement.dto.JwtResponse;
import com.example.employeeManagement.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws AuthenticationException {
        try {
            logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());
            Authentication authentication = authenticationService.authenticateUser(loginRequest);
            String jwt = authenticationService.generateJwtToken(authentication);
            logger.info("JWT: {}", jwt);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception ex) {
            logger.error("Login failed: {}", ex.getMessage());
            throw ex;
        }
    }
}
