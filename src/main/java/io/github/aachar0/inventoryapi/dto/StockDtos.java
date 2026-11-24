package io.github.aachar0.inventoryapi.dto;

import io.github.aachar0.inventoryapi.domain.MovementType;
import io.github.aachar0.inventoryapi.domain.StockMovement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class StockDtos {

    private StockDtos() {
    }

    public record StockMovementRequest(
            @NotNull MovementType type,
            @Min(value = 1, message = "must be at least 1") int quantity,
            @Size(max = 255) String note) {
    }

    public record StockMovementResponse(
            Long id, Long productId, MovementType type, int quantity, String note,
            String createdBy, int resultingQuantity, Instant createdAt) {

        public static StockMovementResponse from(StockMovement m) {
            return new StockMovementResponse(
                    m.getId(), m.getProduct().getId(), m.getMovementType(), m.getQuantity(), m.getNote(),
                    m.getCreatedBy(), m.getResultingQuantity(), m.getCreatedAt());
        }
    }
}
