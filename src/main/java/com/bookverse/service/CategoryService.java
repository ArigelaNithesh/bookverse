package com.bookverse.service;

import com.bookverse.dto.CategoryDto;
import com.bookverse.entity.Category;
import com.bookverse.exception.ConflictException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getCategoryHierarchy() {
        return categoryRepository.findByParentIsNullOrderByDisplayOrderAscNameAsc().stream()
                .map(this::mapToDtoWithChildren)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc().stream()
                .map(this::mapToDtoFlat)
                .collect(Collectors.toList());
    }

    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public CategoryDto getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return mapToDtoWithChildren(category);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsBySlug(dto.getSlug())) {
            throw new ConflictException("Category with slug already exists: " + dto.getSlug());
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setSlug(dto.getSlug().toLowerCase().trim().replaceAll("[^a-z0-9-]", "-"));
        category.setDescription(dto.getDescription());
        category.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        category.setActive(dto.isActive());

        if (dto.getParentId() != null) {
            Category parent = getCategoryEntityById(dto.getParentId());
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return mapToDtoFlat(saved);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = getCategoryEntityById(id);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        category.setActive(dto.isActive());

        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(id)) {
                throw new ConflictException("Category cannot be its own parent");
            }
            Category parent = getCategoryEntityById(dto.getParentId());
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updated = categoryRepository.save(category);
        return mapToDtoFlat(updated);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryEntityById(id);
        categoryRepository.delete(category);
    }

    public CategoryDto mapToDtoFlat(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setActive(category.isActive());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
            dto.setParentName(category.getParent().getName());
        }
        return dto;
    }

    public CategoryDto mapToDtoWithChildren(Category category) {
        CategoryDto dto = mapToDtoFlat(category);
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            dto.setSubcategories(category.getSubcategories().stream()
                    .filter(Category::isActive)
                    .map(this::mapToDtoWithChildren)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
