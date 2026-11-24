package io.github.aachar0.inventoryapi.service;

import io.github.aachar0.inventoryapi.domain.Category;
import io.github.aachar0.inventoryapi.domain.MovementType;
import io.github.aachar0.inventoryapi.domain.Product;
import io.github.aachar0.inventoryapi.domain.StockMovement;
import io.github.aachar0.inventoryapi.dto.StockDtos.StockMovementRequest;
import io.github.aachar0.inventoryapi.dto.StockDtos.StockMovementResponse;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.InsufficientStockException;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.NotFoundException;
import io.github.aachar0.inventoryapi.repository.ProductRepository;
import io.github.aachar0.inventoryapi.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A UNIT test: the service runs for real, but its repositories are Mockito mocks (fake objects we control).
 * No Spring and no database start, so the whole class runs in milliseconds.
 * Pattern for every test: Arrange (set up) -> Act (call) -> Assert (check).
 */
@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    ProductRepository products;

    @Mock
    StockMovementRepository movements;

    StockService service;
    Product product;

    @BeforeEach
    void setUp() {
        service = new StockService(products, movements);
        product = new Product("USB-C-1M", "USB-C cable", null, new BigDecimal("6.50"), 10, 5,
                new Category("Electronics", null));
    }

    @Test
    void inMovementIncreasesStockAndRecordsHistory() {
        when(products.findWithLockById(1L)).thenReturn(Optional.of(product));
        when(movements.save(any(StockMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StockMovementResponse response =
                service.record(1L, new StockMovementRequest(MovementType.IN, 15, "Delivery"), "mary");

        assertThat(product.getQuantity()).isEqualTo(25);
        assertThat(response.resultingQuantity()).isEqualTo(25);
        assertThat(response.createdBy()).isEqualTo("mary");
        assertThat(response.type()).isEqualTo(MovementType.IN);
    }

    @Test
    void outMovementBelowZeroIsRejectedAndNothingIsSaved() {
        when(products.findWithLockById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.record(1L, new StockMovementRequest(MovementType.OUT, 11, null), "mary"))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("USB-C-1M");

        assertThat(product.getQuantity()).isEqualTo(10);          // unchanged
        verify(movements, never()).save(any());                   // no history row written
    }

    @Test
    void outMovementOfExactlyAllStockIsAllowed() {
        when(products.findWithLockById(1L)).thenReturn(Optional.of(product));
        when(movements.save(any(StockMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.record(1L, new StockMovementRequest(MovementType.OUT, 10, null), "mary");

        ArgumentCaptor<StockMovement> saved = ArgumentCaptor.forClass(StockMovement.class);
        verify(movements).save(saved.capture());                  // capture what was handed to save()
        assertThat(saved.getValue().getResultingQuantity()).isZero();
        assertThat(product.isLowStock()).isTrue();                // 0 <= reorder level 5
    }

    @Test
    void unknownProductThrowsNotFound() {
        when(products.findWithLockById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.record(99L, new StockMovementRequest(MovementType.IN, 1, null), "mary"))
                .isInstanceOf(NotFoundException.class);
    }
}
