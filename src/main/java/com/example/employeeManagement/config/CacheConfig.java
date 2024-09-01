package com.example.employeeManagement.config;

import com.example.employeeManagement.model.Employee;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public LoadingCache<String, Employee> employeeCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Employee>() {
                    @Override
                    public Employee load(String key) {
                        // Implement cache loading logic
                        return null;
                    }
                });
    }
}
