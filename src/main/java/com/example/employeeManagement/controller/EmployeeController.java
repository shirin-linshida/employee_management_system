package com.example.employeeManagement.controller;

import com.example.employeeManagement.dto.EmployeeRequest;
import com.example.employeeManagement.model.Employee;
import com.example.employeeManagement.repository.EmployeeRepository;
import com.example.employeeManagement.service.EmployeeService;
import com.example.employeeManagement.service.PaginationSortingAndFilteringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PaginationSortingAndFilteringService paginationAndFilteringService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<Employee>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {

        // Creating the filtering specification
        Specification<Employee> spec = Specification.where(null);

        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }

        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }

        // Using the shared service to get paginated, sorted, and filtered results
        Page<Employee> employees = paginationAndFilteringService.getPaginatedAndFilteredResults(
                page, size, sortField, sortDir, spec, employeeRepository);

        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
