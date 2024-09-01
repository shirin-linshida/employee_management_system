package com.example.employeeManagement.controller;

import com.example.employeeManagement.dto.CityRequest;
import com.example.employeeManagement.model.City;
import com.example.employeeManagement.repository.CityRepository;
import com.example.employeeManagement.service.CityService;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PaginationSortingAndFilteringService paginationSortingAndFilteringService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<City>> getAllCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long stateId) {

        // Filtering Specification
        Specification<City> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (stateId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("state").get("id"), stateId));
        }

        // Fetching paginated and filtered results using the shared service
        Page<City> cities = paginationSortingAndFilteringService.getPaginatedAndFilteredResults(
                page, size, sortField, sortDir, spec, cityRepository);

        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<City> getCityById(@PathVariable Long id) {
        return cityService.getCityById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public City createCity(@RequestBody CityRequest cityRequest) {
        return cityService.createOrUpdateCity(cityRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public City updateCity(@PathVariable Long id, @RequestBody CityRequest cityRequest) {
        cityRequest.setId(id);
        return cityService.createOrUpdateCity(cityRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
    }
}
