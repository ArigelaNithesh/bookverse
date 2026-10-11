package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.entity.User;
import com.bookverse.service.AuthService;
import com.bookverse.service.ReadingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reading")
public class ReadingController {

    private final ReadingService readingService;
    private final AuthService authService;

    public ReadingController(ReadingService readingService, AuthService authService) {
        this.readingService = readingService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReadingProgressDto>>> getReadingList() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(readingService.getUserReadingList(user)));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<ReadingProgressDto>> getReadingProgress(@PathVariable Long bookId) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(readingService.getOrCreateProgress(user, bookId)));
    }

    @PutMapping("/{bookId}/progress")
    public ResponseEntity<ApiResponse<ReadingProgressDto>> updateProgress(
            @PathVariable Long bookId,
            @RequestBody UpdateProgressRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success("Progress saved", readingService.updateProgress(user, bookId, request)));
    }

    @GetMapping("/{bookId}/bookmarks")
    public ResponseEntity<ApiResponse<List<BookmarkDto>>> getBookmarks(@PathVariable Long bookId) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(readingService.getBookmarks(user, bookId)));
    }

    @PostMapping("/{bookId}/bookmarks")
    public ResponseEntity<ApiResponse<BookmarkDto>> addBookmark(
            @PathVariable Long bookId,
            @Valid @RequestBody CreateBookmarkRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success("Bookmark added", readingService.addBookmark(user, bookId, request)));
    }

    @DeleteMapping("/bookmarks/{bookmarkId}")
    public ResponseEntity<ApiResponse<Void>> deleteBookmark(@PathVariable Long bookmarkId) {
        User user = authService.getCurrentAuthenticatedUser();
        readingService.deleteBookmark(user, bookmarkId);
        return ResponseEntity.ok(ApiResponse.success("Bookmark removed", null));
    }

    @GetMapping("/{bookId}/content")
    public ResponseEntity<ApiResponse<DigitalContentDto>> getDigitalContent(@PathVariable Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(readingService.getDigitalContent(bookId)));
    }
}
