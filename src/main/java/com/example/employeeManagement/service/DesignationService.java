package com.example.employeeManagement.service;

import com.example.employeeManagement.dto.DesignationRequest;
import com.example.employeeManagement.model.Department;
import com.example.employeeManagement.model.Designation;
import com.example.employeeManagement.repository.DepartmentRepository;
import com.example.employeeManagement.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Optional<Designation> getDesignationById(Long id) {
        return designationRepository.findById(id);
    }

    public Designation createOrUpdateDesignation(DesignationRequest designationRequest) {
        Designation designation = new Designation();
        if (designationRequest.getId() != null) {
            Optional<Designation> existingDesignation = designationRepository.findById(designationRequest.getId());
            if (existingDesignation.isPresent()) {
                designation = existingDesignation.get();
            } else {
                throw new RuntimeException("Designation not found with ID: " + designationRequest.getId());
            }
        }

        designation.setName(designationRequest.getName());

        // Fetch the department by ID and set it to the designation
        Optional<Department> departmentOptional = departmentRepository.findById(designationRequest.getDepartment_id());
        if (departmentOptional.isPresent()) {
            designation.setDepartment(departmentOptional.get());
        } else {
            throw new RuntimeException("Department not found with ID: " + designationRequest.getDepartment_id());
        }

        return designationRepository.save(designation);
    }

    public void deleteDesignation(Long id) {
        designationRepository.deleteById(id);
    }
}
