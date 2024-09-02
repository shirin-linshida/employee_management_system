package com.example.employeeManagement.repository;

import com.example.employeeManagement.model.City;
import com.example.employeeManagement.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long>, JpaSpecificationExecutor<City> {
    Optional<City> findByNameAndState(String name, State state);
}
