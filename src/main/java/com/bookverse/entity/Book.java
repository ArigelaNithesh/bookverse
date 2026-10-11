package com.bookverse.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String subtitle;

    @Column(nullable = false, unique = true, length = 280)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 10)
    private String isbn10;

    @Column(length = 13)
    private String isbn13;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    private String edition;

    @Column(length = 50)
    private String language = "English";

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @Column(name = "list_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal listPrice;

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean bestseller = false;

    @Column(name = "digital_available", nullable = false)
    private boolean digitalAvailable = false;

    @Column(name = "digital_content_type", length = 50)
    private String digitalContentType;

    @Column(name = "digital_content_reference", length = 500)
    private String digitalContentReference;

    @Column(name = "external_source", length = 100)
    private String externalSource;

    @Column(name = "external_source_id", length = 100)
    private String externalSourceId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Book() {}

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

    public Publisher getPublisher() { return publisher; }
    public void setPublisher(Publisher publisher) { this.publisher = publisher; }

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

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Set<Author> getAuthors() { return authors; }
    public void setAuthors(Set<Author> authors) { this.authors = authors; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
