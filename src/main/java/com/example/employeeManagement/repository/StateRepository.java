package com.example.employeeManagement.repository;

import com.example.employeeManagement.model.State;
import com.example.employeeManagement.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long>, JpaSpecificationExecutor<State> {
    Optional<State> findByNameAndCountry(String name, Country country);
}
