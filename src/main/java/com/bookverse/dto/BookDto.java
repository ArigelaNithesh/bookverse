package com.bookverse.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookDto {

    private Long id;
    private String title;
    private String subtitle;
    private String slug;
    private String description;
    private String isbn10;
    private String isbn13;
    private String publisherName;
    private LocalDate publicationDate;
    private String edition;
    private String language;
    private Integer pageCount;
    private String coverImageUrl;
    private Long categoryId;
    private String categoryName;
    private List<AuthorDto> authors = new ArrayList<>();
    private BigDecimal listPrice;
    private BigDecimal sellingPrice;
    private Integer stockQuantity;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private boolean featured;
    private boolean active;
    private boolean bestseller;
    private boolean digitalAvailable;
    private String digitalContentType;
    private String digitalContentReference;
    private String externalSource;
    private String externalSourceId;

    public BookDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIsbn10() { return isbn10; }
    public void setIsbn10(String isbn10) { this.isbn10 = isbn10; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<AuthorDto> getAuthors() { return authors; }
    public void setAuthors(List<AuthorDto> authors) { this.authors = authors; }

    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isBestseller() { return bestseller; }
    public void setBestseller(boolean bestseller) { this.bestseller = bestseller; }

    public boolean isDigitalAvailable() { return digitalAvailable; }
    public void setDigitalAvailable(boolean digitalAvailable) { this.digitalAvailable = digitalAvailable; }

    public String getDigitalContentType() { return digitalContentType; }
    public void setDigitalContentType(String digitalContentType) { this.digitalContentType = digitalContentType; }

    public String getDigitalContentReference() { return digitalContentReference; }
    public void setDigitalContentReference(String digitalContentReference) { this.digitalContentReference = digitalContentReference; }

    public String getExternalSource() { return externalSource; }
    public void setExternalSource(String externalSource) { this.externalSource = externalSource; }

    public String getExternalSourceId() { return externalSourceId; }
    public void setExternalSourceId(String externalSourceId) { this.externalSourceId = externalSourceId; }
}
