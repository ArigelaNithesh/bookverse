package com.bookverse.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReviewRequest {

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    private Integer rating;

    @Size(max = 150, message = "Headline must be under 150 characters")
    private String headline;

    @Size(max = 2000, message = "Review text must be under 2000 characters")
    private String reviewText;

    public CreateReviewRequest() {}

    public CreateReviewRequest(Integer rating, String headline, String reviewText) {
        this.rating = rating;
        this.headline = headline;
        this.reviewText = reviewText;
    }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
}
