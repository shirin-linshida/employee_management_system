package com.example.employeeManagement.specification;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.Map;

public class EntitySpecification<T> implements Specification<T> {

    private final Map<String, Object> filters;

    public EntitySpecification(Map<String, Object> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();
        filters.forEach((key, value) -> {
            if (value != null) {
                predicate.getExpressions().add(criteriaBuilder.equal(root.get(key), value));
            }
        });
        return predicate;
    }
}
