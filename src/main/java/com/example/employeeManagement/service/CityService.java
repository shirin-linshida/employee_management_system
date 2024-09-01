package com.example.employeeManagement.service;

import com.example.employeeManagement.dto.CityRequest;
import com.example.employeeManagement.model.City;
import com.example.employeeManagement.model.State;
import com.example.employeeManagement.repository.CityRepository;
import com.example.employeeManagement.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StateRepository stateRepository;

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public Optional<City> getCityById(Long id) {
        return cityRepository.findById(id);
    }

    public Page<City> getCities(Specification<City> spec, Pageable pageable) {
        return cityRepository.findAll(spec, pageable);
    }

    public City createOrUpdateCity(CityRequest cityRequest) {
        City city = new City();
        if (cityRequest.getId() != null) {
            Optional<City> existingCity = cityRepository.findById(cityRequest.getId());
            if (existingCity.isPresent()) {
                city = existingCity.get();
            } else {
                throw new RuntimeException("City not found with ID: " + cityRequest.getId());
            }
        }

        city.setName(cityRequest.getName());

        // Fetch the country by ID and set it to the city
        Optional<State> stateOptional = stateRepository.findById(cityRequest.getState_id());
        if (stateOptional.isPresent()) {
            city.setState(stateOptional.get());
        } else {
            throw new RuntimeException("Country not found with ID: " + cityRequest.getState_id());
        }

        return cityRepository.save(city);
    }

    public void deleteCity(Long id) {
        cityRepository.deleteById(id);
    }
}
