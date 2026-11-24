package io.github.aachar0.inventoryapi.service;

import io.github.aachar0.inventoryapi.domain.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

/**
 * Builds a WHERE clause from optional filters using the JPA Criteria API.
 * A Specification is a function (root, query, cb) -> Predicate; lambdas make it short.
 * Only the filters the caller actually supplied are added, joined with AND.
 */
public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> matches(String q, Long categoryId) {
        Specification<Product> spec = (root, query, cb) -> cb.conjunction(); // "true": matches everything

        if (q != null && !q.isBlank()) {
            String pattern = "%" + q.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("sku")), pattern)));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        return spec;
    }
}
