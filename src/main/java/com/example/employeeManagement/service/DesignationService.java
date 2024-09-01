package com.example.employeeManagement.service;

import com.example.employeeManagement.model.Designation;
import com.example.employeeManagement.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    public List<Designation> getAllDesignations() {
        return designationRepository.findAll();
    }

    public Optional<Designation> getDesignationById(Long id) {
        return designationRepository.findById(id);
    }

    public Designation createOrUpdateDesignation(Designation designation) {
        return designationRepository.save(designation);
    }

    public void deleteDesignation(Long id) {
        designationRepository.deleteById(id);
    }
}
