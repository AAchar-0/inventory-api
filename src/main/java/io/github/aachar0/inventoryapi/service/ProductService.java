package io.github.aachar0.inventoryapi.service;

import io.github.aachar0.inventoryapi.domain.Category;
import io.github.aachar0.inventoryapi.domain.Product;
import io.github.aachar0.inventoryapi.dto.CatalogDtos.ProductRequest;
import io.github.aachar0.inventoryapi.dto.CatalogDtos.ProductResponse;
import io.github.aachar0.inventoryapi.dto.PageResponse;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.BadRequestException;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.ConflictException;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.NotFoundException;
import io.github.aachar0.inventoryapi.repository.CategoryRepository;
import io.github.aachar0.inventoryapi.repository.ProductRepository;
import io.github.aachar0.inventoryapi.repository.StockMovementRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class ProductService {

    /** Only these fields may be sorted on: user input is never passed to the database unchecked. */
    private static final Set<String> SORTABLE = Set.of("name", "sku", "price", "quantity", "createdAt");

    private final ProductRepository products;
    private final CategoryRepository categories;
    private final StockMovementRepository movements;

    public ProductService(ProductRepository products, CategoryRepository categories, StockMovementRepository movements) {
        this.products = products;
        this.categories = categories;
        this.movements = movements;
    }

    // readOnly = true lets the database skip work it only needs for writes.
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(String q, Long categoryId, int page, int size,
                                                String sortField, String direction) {
        if (!SORTABLE.contains(sortField)) {
            throw new BadRequestException("Cannot sort by '" + sortField + "'. Allowed: " + new TreeSet<>(SORTABLE));
        }
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        int safeSize = Math.min(Math.max(size, 1), 100);   // clamp: 1..100 items per page
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(dir, sortField));

        return PageResponse.of(
                products.findAll(ProductSpecifications.matches(q, categoryId), pageable).map(ProductResponse::from));
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return ProductResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> lowStock() {
        return products.findLowStock().stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest r) {
        String sku = r.sku().trim();
        if (products.existsBySku(sku)) {
            throw new ConflictException("SKU '" + sku + "' already exists");
        }
        Product saved = products.save(new Product(
                sku, r.name().trim(), r.description(), r.price(), r.quantity(), r.reorderLevel(), findCategory(r.categoryId())));
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest r) {
        Product product = find(id);
        String sku = r.sku().trim();
        if (products.existsBySkuAndIdNot(sku, id)) {
            throw new ConflictException("SKU '" + sku + "' already exists");
        }
        product.setSku(sku);
        product.setName(r.name().trim());
        product.setDescription(r.description());
        product.setPrice(r.price());
        product.setReorderLevel(r.reorderLevel());
        product.setCategory(findCategory(r.categoryId()));
        // Quantity is NOT changed here: stock only changes through movements, so history stays complete.
        return ProductResponse.from(products.saveAndFlush(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = find(id);
        if (movements.existsByProductId(id)) {
            throw new ConflictException("Product has stock history and cannot be deleted");
        }
        products.delete(product);
    }

    private Product find(Long id) {
        return products.findById(id).orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }

    private Category findCategory(Long id) {
        return categories.findById(id).orElseThrow(() -> new NotFoundException("Category " + id + " not found"));
    }
}
