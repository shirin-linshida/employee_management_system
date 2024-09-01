package com.example.employeeManagement.controller;

import com.example.employeeManagement.model.Language;
import com.example.employeeManagement.repository.LanguageRepository;
import com.example.employeeManagement.service.LanguageService;
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
@RequestMapping("/api/languages")
public class LanguageController {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private PaginationSortingAndFilteringService paginationAndFilteringService;

    @Autowired
    private LanguageRepository languageRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<Language>> getAllLanguages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name) {

        // Creating the filtering specification
        Specification<Language> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        // Using the shared service to get paginated, sorted, and filtered results
        Page<Language> languages = paginationAndFilteringService.getPaginatedAndFilteredResults(
                page, size, sortField, sortDir, spec, languageRepository);

        return ResponseEntity.ok(languages);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Language> getLanguageById(@PathVariable Long id) {
        return languageService.getLanguageById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Language createLanguage(@RequestBody Language language) {
        return languageService.createOrUpdateLanguage(language);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Language updateLanguage(@PathVariable Long id, @RequestBody Language language) {
        language.setId(id);
        return languageService.createOrUpdateLanguage(language);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLanguage(@PathVariable Long id) {
        languageService.deleteLanguage(id);
    }
}
