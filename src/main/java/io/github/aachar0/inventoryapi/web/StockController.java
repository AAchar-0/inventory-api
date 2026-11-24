package io.github.aachar0.inventoryapi.web;

import io.github.aachar0.inventoryapi.dto.PageResponse;
import io.github.aachar0.inventoryapi.dto.StockDtos.StockMovementRequest;
import io.github.aachar0.inventoryapi.dto.StockDtos.StockMovementResponse;
import io.github.aachar0.inventoryapi.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productId}/movements")
@Tag(name = "Stock movements")
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final StockService stock;

    public StockController(StockService stock) {
        this.stock = stock;
    }

    /** Spring injects the Authentication of the caller; for a JWT login, getName() is the token's subject (username). */
    @Operation(summary = "Receive (IN) or remove (OUT) stock. Any logged-in user")
    @PostMapping
    public ResponseEntity<StockMovementResponse> record(
            @PathVariable Long productId,
            @Valid @RequestBody StockMovementRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stock.record(productId, request, authentication.getName()));
    }

    @Operation(summary = "Movement history of a product, newest first")
    @GetMapping
    public PageResponse<StockMovementResponse> history(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return stock.history(productId, page, size);
    }
}
