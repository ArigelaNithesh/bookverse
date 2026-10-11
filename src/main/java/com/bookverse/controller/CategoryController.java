package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.CategoryDto;
import com.bookverse.service.BookService;
import com.bookverse.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final BookService bookService;

    public CategoryController(CategoryService categoryService, BookService bookService) {
        this.categoryService = categoryService;
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryHierarchy()));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories()));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryBySlug(slug)));
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<com.bookverse.dto.BookSummaryDto>>> getBooksByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(bookService.getBooksByCategory(id, pageable)));
    }
}

