package io.github.aachar0.inventoryapi.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A generic wrapper for one page of results. "<T>" is a type parameter: PageResponse<ProductResponse>
 * is a page of products, PageResponse<StockMovementResponse> a page of movements; one class serves both.
 * We build our own JSON shape instead of returning Spring's Page directly, so the API stays stable.
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
