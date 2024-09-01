package com.example.employeeManagement.controller;

import com.example.employeeManagement.dto.StateRequest;
import com.example.employeeManagement.model.State;
import com.example.employeeManagement.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@RestController
@RequestMapping("/api/states")
public class StateController {

    @Autowired
    private StateService stateService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<State>> getAllStates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name) {

        // Sorting
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // Pagination
        Pageable pageable = PageRequest.of(page, size, sort);

        // Filtering Specification
        Specification<State> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        // Fetching paginated and filtered results
        Page<State> states = stateService.getStates(spec, pageable);

        return ResponseEntity.ok(states);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<State> getStateById(@PathVariable Long id) {
        return stateService.getStateById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public State createState(@RequestBody StateRequest stateRequest) {
        return stateService.createOrUpdateState(stateRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public State updateState(@PathVariable Long id, @RequestBody StateRequest stateRequest) {
        stateRequest.setId(id);
        return stateService.createOrUpdateState(stateRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteState(@PathVariable Long id) {
        stateService.deleteState(id);
    }
}
