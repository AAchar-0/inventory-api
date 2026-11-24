package io.github.aachar0.inventoryapi.repository;

import io.github.aachar0.inventoryapi.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * JpaSpecificationExecutor adds findAll(Specification, Pageable): dynamic filters built in Java code
 * (see ProductSpecifications) instead of one hard-coded query per filter combination.
 */
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsByCategoryId(Long categoryId);

    /**
     * Loads the product and locks its row (SELECT ... FOR UPDATE) until the transaction ends.
     * Two people selling the last item at the same moment are processed one after the other,
     * so stock can never go wrong. Must be called inside a @Transactional method.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findWithLockById(Long id);

    /** JPQL (Java Persistence Query Language) works on entities, not tables. "join fetch" loads the category in the same query. */
    @Query("select p from Product p join fetch p.category "
            + "where p.quantity <= p.reorderLevel order by p.quantity asc, p.name asc")
    List<Product> findLowStock();
}
