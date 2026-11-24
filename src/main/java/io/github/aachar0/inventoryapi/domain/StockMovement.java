package io.github.aachar0.inventoryapi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/** One row per stock change (table "stock_movements"). Rows are only ever added, never edited. */
@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 10)
    private MovementType movementType;

    @Column(nullable = false)
    private int quantity;

    @Column(length = 255)
    private String note;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "resulting_quantity", nullable = false)
    private int resultingQuantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected StockMovement() {
    }

    public StockMovement(Product product, MovementType movementType, int quantity,
                         String note, String createdBy, int resultingQuantity) {
        this.product = product;
        this.movementType = movementType;
        this.quantity = quantity;
        this.note = note;
        this.createdBy = createdBy;
        this.resultingQuantity = resultingQuantity;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public MovementType getMovementType() { return movementType; }
    public int getQuantity() { return quantity; }
    public String getNote() { return note; }
    public String getCreatedBy() { return createdBy; }
    public int getResultingQuantity() { return resultingQuantity; }
    public Instant getCreatedAt() { return createdAt; }
}
