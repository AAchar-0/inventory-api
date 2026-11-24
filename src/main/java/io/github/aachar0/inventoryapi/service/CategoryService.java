package io.github.aachar0.inventoryapi.service;

import io.github.aachar0.inventoryapi.domain.Category;
import io.github.aachar0.inventoryapi.dto.CatalogDtos.CategoryRequest;
import io.github.aachar0.inventoryapi.dto.CatalogDtos.CategoryResponse;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.ConflictException;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.NotFoundException;
import io.github.aachar0.inventoryapi.repository.CategoryRepository;
import io.github.aachar0.inventoryapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD (Create, Read, Update, Delete) for categories. */
@Service
public class CategoryService {

    private final CategoryRepository categories;
    private final ProductRepository products;

    public CategoryService(CategoryRepository categories, ProductRepository products) {
        this.categories = categories;
        this.products = products;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categories.findAllByOrderByNameAsc().stream().map(CategoryResponse::from).toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (categories.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Category '" + name + "' already exists");
        }
        return CategoryResponse.from(categories.save(new Category(name, request.description())));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = find(id);
        String name = request.name().trim();
        if (categories.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("Category '" + name + "' already exists");
        }
        category.setName(name);
        category.setDescription(request.description());
        return CategoryResponse.from(categories.saveAndFlush(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = find(id);
        if (products.existsByCategoryId(id)) {
            throw new ConflictException("Category still has products; move or delete them first");
        }
        categories.delete(category);
    }

    private Category find(Long id) {
        return categories.findById(id).orElseThrow(() -> new NotFoundException("Category " + id + " not found"));
    }
}
