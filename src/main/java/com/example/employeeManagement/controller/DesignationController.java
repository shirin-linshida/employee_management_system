package com.example.employeeManagement.controller;

import com.example.employeeManagement.dto.DesignationRequest;
import com.example.employeeManagement.model.Designation;
import com.example.employeeManagement.repository.DesignationRepository;
import com.example.employeeManagement.service.DesignationService;
import com.example.employeeManagement.service.PaginationSortingAndFilteringService;
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
@RequestMapping("/api/designations")
public class DesignationController {

    @Autowired
    private DesignationService designationService;

    @Autowired
    private PaginationSortingAndFilteringService paginationAndFilteringService;

    @Autowired
    private DesignationRepository designationRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<Designation>> getAllDesignations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId) {

        // Creating the filtering specification
        Specification<Designation> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (departmentId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("department").get("id"), departmentId));
        }

        // Using the shared service to get paginated, sorted, and filtered results
        Page<Designation> designations = paginationAndFilteringService.getPaginatedAndFilteredResults(
                page, size, sortField, sortDir, spec, designationRepository);

        return ResponseEntity.ok(designations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Designation> getDesignationById(@PathVariable Long id) {
        return designationService.getDesignationById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Designation createDesignation(@RequestBody DesignationRequest designationRequest) {
        return designationService.createOrUpdateDesignation(designationRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Designation updateDesignation(@PathVariable Long id, @RequestBody DesignationRequest designationRequest) {
        designationRequest.setId(id);
        return designationService.createOrUpdateDesignation(designationRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDesignation(@PathVariable Long id) {
        designationService.deleteDesignation(id);
    }
}
