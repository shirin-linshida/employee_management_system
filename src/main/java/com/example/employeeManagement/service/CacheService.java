package com.example.employeeManagement.service;

import com.example.employeeManagement.model.Employee;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final Cache<String, Employee> employeeCache;

    // Ensure proper injection of the Cache bean
    @Autowired
    public CacheService(Cache<String, Employee> employeeCache) {
        this.employeeCache = employeeCache;
    }

    // Example method to use the cache
    public Employee getEmployeeFromCache(String id) {
        return employeeCache.getIfPresent(id);
    }

    public void addEmployeeToCache(String id, Employee employee) {
        employeeCache.put(id, employee);
    }

    public void clearCache() {
        employeeCache.invalidateAll(); // Clears the entire cache
    }
}
