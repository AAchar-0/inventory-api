package io.github.aachar0.inventoryapi.dto;

import io.github.aachar0.inventoryapi.domain.Category;
import io.github.aachar0.inventoryapi.domain.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

/** Request and response shapes for categories and products. */
public final class CatalogDtos {

    private CatalogDtos() {
    }

    public record CategoryRequest(
            @NotBlank @Size(max = 80) String name,
            @Size(max = 255) String description) {
    }

    public record CategoryResponse(Long id, String name, String description) {
        public static CategoryResponse from(Category c) {
            return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
        }
    }

    public record ProductRequest(
            @NotBlank @Size(max = 40) String sku,
            @NotBlank @Size(max = 120) String name,
            @Size(max = 500) String description,
            @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal price,
            @Min(0) int quantity,
            @Min(0) int reorderLevel,
            @NotNull Long categoryId) {
    }

    public record ProductResponse(
            Long id, String sku, String name, String description, BigDecimal price,
            int quantity, int reorderLevel, boolean lowStock,
            Long categoryId, String categoryName, Instant createdAt, Instant updatedAt) {

        /** Static factory: converts an entity into its response shape. */
        public static ProductResponse from(Product p) {
            return new ProductResponse(
                    p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getPrice(),
                    p.getQuantity(), p.getReorderLevel(), p.isLowStock(),
                    p.getCategory().getId(), p.getCategory().getName(),
                    p.getCreatedAt(), p.getUpdatedAt());
        }
    }
}
