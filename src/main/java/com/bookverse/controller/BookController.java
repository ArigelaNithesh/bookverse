package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.BookDto;
import com.bookverse.dto.BookSummaryDto;
import com.bookverse.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Boolean digitalAvailable,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        Page<BookSummaryDto> result = bookService.searchBooks(
                keyword, categoryId, minPrice, maxPrice, minRating, year, language, digitalAvailable, pageable
        );
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> searchBooks(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        Page<BookSummaryDto> result = bookService.searchBooks(
                q, categoryId, minPrice, maxPrice, minRating, null, null, null, pageable
        );
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getBookById(id)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<BookDto>> getBookBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getBookBySlug(slug)));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<BookSummaryDto>>> getFeaturedBooks() {
        return ResponseEntity.ok(ApiResponse.success(bookService.getFeaturedBooks()));
    }

    @GetMapping("/bestsellers")
    public ResponseEntity<ApiResponse<List<BookSummaryDto>>> getBestsellers() {
        return ResponseEntity.ok(ApiResponse.success(bookService.getBestsellers()));
    }

    private Pageable createPageable(int page, int size, String sortParam) {
        if (size > 50) size = 50;
        if (size < 1) size = 12;
        if (page < 0) page = 0;

        String[] parts = sortParam.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // Whitelist allowable sort columns
        if (!List.of("id", "sellingPrice", "title", "averageRating", "publicationDate", "createdAt").contains(property)) {
            property = "id";
        }

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
