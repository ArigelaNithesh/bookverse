package com.bookverse.dto;

import java.math.BigDecimal;

public class CartItemDto {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookSlug;
    private String coverImageUrl;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineTotal;
    private Integer availableStock;

    public CartItemDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookSlug() { return bookSlug; }
    public void setBookSlug(String bookSlug) { this.bookSlug = bookSlug; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
}
