package com.example.employeeManagement.config;

import com.example.employeeManagement.model.Role;
import com.example.employeeManagement.model.User;
import com.example.employeeManagement.repository.RoleRepository;
import com.example.employeeManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner firstTimeRun() {
        return args -> {
            // Create roles if they don't exist
            if (roleRepository.count() == 0) {
                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);

                Role staffRole = new Role();
                staffRole.setName("ROLE_STAFF");
                roleRepository.save(staffRole);
            }

            // Create an admin user if it doesn't exist
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("adminpassword"));

                // Fetch roles
                Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);

                userRepository.save(admin);
            }
        };
    }
}
