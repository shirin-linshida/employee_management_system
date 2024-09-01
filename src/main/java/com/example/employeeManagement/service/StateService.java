package com.example.employeeManagement.service;

import com.example.employeeManagement.dto.StateRequest;
import com.example.employeeManagement.model.Country;
import com.example.employeeManagement.model.State;
import com.example.employeeManagement.repository.CountryRepository;
import com.example.employeeManagement.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    public List<State> getAllStates() {
        return stateRepository.findAll();
    }

    public Optional<State> getStateById(Long id) {
        return stateRepository.findById(id);
    }

    public State createOrUpdateState(StateRequest stateRequest) {
        State state = new State();
        if (stateRequest.getId() != null) {
            Optional<State> existingState = stateRepository.findById(stateRequest.getId());
            if (existingState.isPresent()) {
                state = existingState.get();
            } else {
                throw new RuntimeException("State not found with ID: " + stateRequest.getId());
            }
        }

        state.setName(stateRequest.getName());

        // Fetch the country by ID and set it to the state
        Optional<Country> countryOptional = countryRepository.findById(stateRequest.getCountry_id());
        if (countryOptional.isPresent()) {
            state.setCountry(countryOptional.get());
        } else {
            throw new RuntimeException("Country not found with ID: " + stateRequest.getCountry_id());
        }

        return stateRepository.save(state);
    }

    public void deleteState(Long id) {
        stateRepository.deleteById(id);
    }
}
