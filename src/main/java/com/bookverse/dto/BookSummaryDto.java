package com.bookverse.dto;

import java.math.BigDecimal;

public class BookSummaryDto {

    private Long id;
    private String title;
    private String slug;
    private String coverImageUrl;
    private String authorName;
    private String categoryName;
    private BigDecimal listPrice;
    private BigDecimal sellingPrice;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private boolean inStock;
    private boolean digitalAvailable;

    public BookSummaryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public boolean isDigitalAvailable() { return digitalAvailable; }
    public void setDigitalAvailable(boolean digitalAvailable) { this.digitalAvailable = digitalAvailable; }
}
