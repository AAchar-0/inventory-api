package io.github.aachar0.inventoryapi.repository;

import io.github.aachar0.inventoryapi.domain.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    /** Newest first. The Pageable argument adds LIMIT/OFFSET and a total count automatically. */
    Page<StockMovement> findByProductIdOrderByCreatedAtDescIdDesc(Long productId, Pageable pageable);

    boolean existsByProductId(Long productId);
}
