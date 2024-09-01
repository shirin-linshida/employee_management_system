package com.example.employeeManagement.service;

import com.example.employeeManagement.specification.EntitySpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaginationAndSortingService {

    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_FIELD = "id";
    private static final String DEFAULT_SORT_DIRECTION = "asc";

    public <T> Page<T> findAll(JpaRepository<T, Long> repository, Map<String, Object> filters, Pageable pageable) {
        if (repository instanceof JpaSpecificationExecutor) {
            Specification<T> spec = new EntitySpecification<>(filters);
            return ((JpaSpecificationExecutor<T>) repository).findAll(spec, pageable);
        } else {
            throw new IllegalArgumentException("Repository does not support Specifications");
        }
    }

    public Pageable createPageable(int page, int size, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        return PageRequest.of(page, size, sort);
    }
}
