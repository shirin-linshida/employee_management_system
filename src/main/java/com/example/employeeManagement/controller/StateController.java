package com.example.employeeManagement.controller;

import com.example.employeeManagement.dto.StateRequest;
import com.example.employeeManagement.model.State;
import com.example.employeeManagement.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/states")
public class StateController {

    @Autowired
    private StateService stateService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public List<State> getAllStates() {
        return stateService.getAllStates();
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
