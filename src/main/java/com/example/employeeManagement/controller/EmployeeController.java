package com.example.employeeManagement.controller;

import com.example.employeeManagement.dto.EmployeeRequest;
import com.example.employeeManagement.model.Employee;
import com.example.employeeManagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public List<Employee> getAllEmployees() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Accessed by: " + auth);
        System.out.println("Roles from Security Context: " + auth.getAuthorities());
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Optional<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping
    public Employee createEmployee(@RequestBody EmployeeRequest employeeRequest) {
        return employeeService.createOrUpdateEmployee(employeeRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authenticationService.isAdmin() or @authenticationService.getCurrentUserId() == #id")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequest employeeRequest) {
        employeeRequest.setId(id);
        return employeeService.createOrUpdateEmployee(employeeRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
}
