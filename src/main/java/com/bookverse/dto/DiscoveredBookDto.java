package com.bookverse.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DiscoveredBookDto {

    private String key;
    private String title;
    private String subtitle;
    private List<String> authors = new ArrayList<>();
    private String authorName;
    private Integer firstPublishYear;
    private Integer editionCount;
    private String isbn13;
    private String isbn10;
    private String coverImageUrl;
    private Integer coverId;
    private String publisher;
    private List<String> subjects = new ArrayList<>();
    private String description;
    private Integer pageCount;
    private Double ratingsAverage;

    // Presence in BOOKVERSE local store
    private boolean inStore;
    private Long storeBookId;
    private String storeSlug;
    private BigDecimal sellingPrice;
    private BigDecimal listPrice;
    private Integer stockQuantity;
    private Boolean active;

    public DiscoveredBookDto() {}

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public Integer getFirstPublishYear() { return firstPublishYear; }
    public void setFirstPublishYear(Integer firstPublishYear) { this.firstPublishYear = firstPublishYear; }

    public Integer getEditionCount() { return editionCount; }
    public void setEditionCount(Integer editionCount) { this.editionCount = editionCount; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public String getIsbn10() { return isbn10; }
    public void setIsbn10(String isbn10) { this.isbn10 = isbn10; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Integer getCoverId() { return coverId; }
    public void setCoverId(Integer coverId) { this.coverId = coverId; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }

    public Double getRatingsAverage() { return ratingsAverage; }
    public void setRatingsAverage(Double ratingsAverage) { this.ratingsAverage = ratingsAverage; }

    public boolean isInStore() { return inStore; }
    public void setInStore(boolean inStore) { this.inStore = inStore; }

    public Long getStoreBookId() { return storeBookId; }
    public void setStoreBookId(Long storeBookId) { this.storeBookId = storeBookId; }

    public String getStoreSlug() { return storeSlug; }
    public void setStoreSlug(String storeSlug) { this.storeSlug = storeSlug; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
