package io.github.aachar0.inventoryapi.service;

import io.github.aachar0.inventoryapi.domain.Product;
import io.github.aachar0.inventoryapi.domain.StockMovement;
import io.github.aachar0.inventoryapi.dto.PageResponse;
import io.github.aachar0.inventoryapi.dto.StockDtos.StockMovementRequest;
import io.github.aachar0.inventoryapi.dto.StockDtos.StockMovementResponse;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.ConflictException;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.InsufficientStockException;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.NotFoundException;
import io.github.aachar0.inventoryapi.repository.ProductRepository;
import io.github.aachar0.inventoryapi.repository.StockMovementRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The heart of the app: changing stock safely. */
@Service
public class StockService {

    private final ProductRepository products;
    private final StockMovementRepository movements;

    public StockService(ProductRepository products, StockMovementRepository movements) {
        this.products = products;
        this.movements = movements;
    }

    /**
     * One transaction = all-or-nothing. We lock the product row, compute the new quantity, update the
     * product and insert the movement. If anything throws, the database rolls everything back.
     */
    @Transactional
    public StockMovementResponse record(Long productId, StockMovementRequest request, String username) {
        Product product = products.findWithLockById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        int newQuantity;
        try {
            newQuantity = request.type().apply(product.getQuantity(), request.quantity());
        } catch (ArithmeticException overflow) {
            throw new ConflictException("Quantity is too large");
        }
        if (newQuantity < 0) {
            throw new InsufficientStockException(product.getSku(), product.getQuantity(), request.quantity());
        }

        product.setQuantity(newQuantity); // saved automatically when the transaction commits (dirty checking)
        StockMovement saved = movements.save(new StockMovement(
                product, request.type(), request.quantity(), request.note(), username, newQuantity));
        return StockMovementResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<StockMovementResponse> history(Long productId, int page, int size) {
        if (!products.existsById(productId)) {
            throw new NotFoundException("Product " + productId + " not found");
        }
        int safeSize = Math.min(Math.max(size, 1), 100);
        return PageResponse.of(
                movements.findByProductIdOrderByCreatedAtDescIdDesc(productId, PageRequest.of(Math.max(page, 0), safeSize))
                        .map(StockMovementResponse::from));
    }
}
