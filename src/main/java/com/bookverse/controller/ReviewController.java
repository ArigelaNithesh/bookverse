package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.CreateReviewRequest;
import com.bookverse.dto.ReviewDto;
import com.bookverse.entity.User;
import com.bookverse.service.AuthService;
import com.bookverse.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;

    public ReviewController(ReviewService reviewService, AuthService authService) {
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewsForBook(bookId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto>> createReview(
            @PathVariable Long bookId,
            @Valid @RequestBody CreateReviewRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        ReviewDto review = reviewService.createReview(user, bookId, request);
        return new ResponseEntity<>(ApiResponse.success("Review submitted successfully", review), HttpStatus.CREATED);
    }
}
