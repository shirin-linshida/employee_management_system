package com.example.employeeManagement.repository;

import com.example.employeeManagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> , JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByEmail(String email);

    @Query("SELECT e.id FROM Employee e WHERE e.email = (SELECT u.username FROM User u WHERE u.id = :userId)")
    Optional<Long> findEmployeeIdByUserId(@Param("userId") Long userId);
}
