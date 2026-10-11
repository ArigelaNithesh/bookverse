package com.bookverse.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminBookImportDto {

    private String openLibraryKey;

    @NotBlank(message = "Book title is required")
    private String title;

    private String subtitle;

    private List<String> authors = new ArrayList<>();

    private String authorName;

    private String isbn13;

    private String isbn10;

    private String publisherName;

    private String categoryName;

    private Integer publicationYear;

    private Integer pageCount;

    private String coverImageUrl;

    private String description;

    @NotNull(message = "List price is required")
    @DecimalMin(value = "0.0", message = "List price must be positive")
    private BigDecimal listPrice = BigDecimal.valueOf(499.00);

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", message = "Selling price must be positive")
    private BigDecimal sellingPrice = BigDecimal.valueOf(399.00);

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity = 25;

    private boolean active = true;

    private boolean featured = false;

    private boolean bestseller = false;

    private boolean digitalAvailable = false;

    public AdminBookImportDto() {}

    public String getOpenLibraryKey() { return openLibraryKey; }
    public void setOpenLibraryKey(String openLibraryKey) { this.openLibraryKey = openLibraryKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public String getIsbn10() { return isbn10; }
    public void setIsbn10(String isbn10) { this.isbn10 = isbn10; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }

    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public boolean isBestseller() { return bestseller; }
    public void setBestseller(boolean bestseller) { this.bestseller = bestseller; }

    public boolean isDigitalAvailable() { return digitalAvailable; }
    public void setDigitalAvailable(boolean digitalAvailable) { this.digitalAvailable = digitalAvailable; }
}
