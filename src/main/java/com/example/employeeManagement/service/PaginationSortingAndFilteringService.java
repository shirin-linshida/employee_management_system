package com.example.employeeManagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
public class PaginationSortingAndFilteringService {

    public <T> Page<T> getPaginatedAndFilteredResults(
            int page,
            int size,
            String sortField,
            String sortDir,
            Specification<T> spec,
            JpaSpecificationExecutor<T> repository) {

        // Sorting
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // Pagination
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetching paginated and filtered results from repository
        return repository.findAll(spec, pageable);
    }
}

