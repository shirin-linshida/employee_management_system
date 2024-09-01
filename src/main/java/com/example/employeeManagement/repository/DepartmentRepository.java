package com.example.employeeManagement.repository;

import com.example.employeeManagement.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> ,  JpaSpecificationExecutor<Department> {
}
