package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.DiscoveryWorkDetailDto;
import com.bookverse.dto.UniversalSearchResponseDto;
import com.bookverse.service.OpenLibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    private final OpenLibraryService openLibraryService;

    public DiscoveryController(OpenLibraryService openLibraryService) {
        this.openLibraryService = openLibraryService;
    }

    /**
     * Universal Book Search endpoint:
     * Searches both local BOOKVERSE inventory and Open Library's world catalog.
     * Supports search by full title, partial title, author, ISBN, subject, and general keywords.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UniversalSearchResponseDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String subject,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        UniversalSearchResponseDto response = openLibraryService.universalSearch(
                q, title, author, isbn, subject, page, limit
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Work Details endpoint:
     * Retrieves rich metadata, descriptions, covers, and edition details for an Open Library work.
     */
    @GetMapping("/work")
    public ResponseEntity<ApiResponse<DiscoveryWorkDetailDto>> getWorkDetails(
            @RequestParam String key
    ) {
        DiscoveryWorkDetailDto details = openLibraryService.getWorkDetails(key);
        return ResponseEntity.ok(ApiResponse.success(details));
    }
}
