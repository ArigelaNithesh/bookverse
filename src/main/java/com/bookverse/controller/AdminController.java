package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final BookService bookService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final OpenLibraryService openLibraryService;

    public AdminController(AdminService adminService,
                           BookService bookService,
                           OrderService orderService,
                           CategoryService categoryService,
                           OpenLibraryService openLibraryService) {
        this.adminService = adminService;
        this.bookService = bookService;
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.openLibraryService = openLibraryService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStatsDto>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboardStats()));
    }

    // Book Management
    @GetMapping("/books")
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> getAdminBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(bookService.searchAdminBooks(keyword, categoryId, pageable)));
    }

    @PostMapping("/books")
    public ResponseEntity<ApiResponse<BookDto>> createBook(@Valid @RequestBody BookDto dto) {
        BookDto created = bookService.createBook(dto);
        return new ResponseEntity<>(ApiResponse.success("Book created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto dto) {
        BookDto updated = bookService.updateBook(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Book updated successfully", updated));
    }

    @PatchMapping("/books/{id}/status")
    public ResponseEntity<ApiResponse<BookDto>> updateBookStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Boolean active = payload.get("active");
        if (active == null) active = true;
        BookDto updated = bookService.updateBookStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success("Book status updated", updated));
    }

    @PatchMapping("/books/{id}/stock")
    public ResponseEntity<ApiResponse<BookDto>> updateBookStock(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer stock = payload.get("stockQuantity");
        if (stock == null) stock = 0;
        BookDto updated = bookService.updateBookStock(id, stock);
        return ResponseEntity.ok(ApiResponse.success("Book inventory updated", updated));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book removed successfully", null));
    }

    // Order Management
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getAdminOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(pageable)));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        OrderDto updated = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", updated));
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAdminUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllCustomers()));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> payload
    ) {
        Boolean active = payload.get("active");
        if (active == null) active = true;
        return ResponseEntity.ok(ApiResponse.success("User status updated", adminService.toggleUserStatus(id, active)));
    }

    // Category Management
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories()));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryDto dto) {
        return new ResponseEntity<>(ApiResponse.success("Category created", categoryService.createCategory(dto)), HttpStatus.CREATED);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Category updated", categoryService.updateCategory(id, dto)));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }

    // Metadata Import
    @GetMapping("/imports")
    public ResponseEntity<ApiResponse<List<MetadataImportJobDto>>> getImports() {
        return ResponseEntity.ok(ApiResponse.success(openLibraryService.getImportHistory()));
    }

    @PostMapping("/imports/open-library")
    public ResponseEntity<ApiResponse<MetadataImportJobDto>> importFromOpenLibrary(@Valid @RequestBody ImportRequestDto request) {
        MetadataImportJobDto job = openLibraryService.importBooks(request);
        return ResponseEntity.ok(ApiResponse.success("Import job processed", job));
    }

    @PostMapping("/books/import-discovered")
    public ResponseEntity<ApiResponse<BookDto>> importDiscoveredBook(@Valid @RequestBody AdminBookImportDto request) {
        BookDto imported = openLibraryService.importDiscoveredBook(request);
        return new ResponseEntity<>(ApiResponse.success("Book imported to store successfully", imported), HttpStatus.CREATED);
    }

    @GetMapping("/imports/{id}")
    public ResponseEntity<ApiResponse<MetadataImportJobDto>> getImportJob(@PathVariable Long id) {
        MetadataImportJobDto job = openLibraryService.getImportJobById(id);
        return ResponseEntity.ok(ApiResponse.success(job));
    }
}
