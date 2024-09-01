package com.example.employeeManagement.controller;

import com.example.employeeManagement.model.Grade;
import com.example.employeeManagement.repository.GradeRepository;
import com.example.employeeManagement.service.GradeService;
import com.example.employeeManagement.service.PaginationSortingAndFilteringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private PaginationSortingAndFilteringService paginationAndFilteringService;

    @Autowired
    private GradeRepository gradeRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<Grade>> getAllGrades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name) {

        // Creating the filtering specification
        Specification<Grade> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        // Using the shared service to get paginated, sorted, and filtered results
        Page<Grade> grades = paginationAndFilteringService.getPaginatedAndFilteredResults(
                page, size, sortField, sortDir, spec, gradeRepository);

        return ResponseEntity.ok(grades);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Grade> getGradeById(@PathVariable Long id) {
        return gradeService.getGradeById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Grade createGrade(@RequestBody Grade grade) {
        return gradeService.createOrUpdateGrade(grade);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Grade updateGrade(@PathVariable Long id, @RequestBody Grade grade) {
        grade.setId(id);
        return gradeService.createOrUpdateGrade(grade);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
    }
}
