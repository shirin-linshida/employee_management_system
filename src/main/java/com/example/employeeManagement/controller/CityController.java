package com.example.employeeManagement.controller;

import com.example.employeeManagement.dto.CityRequest;
import com.example.employeeManagement.model.City;
import com.example.employeeManagement.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public List<City> getAllCities() {
        return cityService.getAllCities();
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
