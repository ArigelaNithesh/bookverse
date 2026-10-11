package com.bookverse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReadingProgressDto {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String coverImageUrl;
    private Integer lastPositionScroll;
    private BigDecimal completionPercentage;
    private String lastChapterTitle;
    private LocalDateTime updatedAt;

    public ReadingProgressDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Integer getLastPositionScroll() { return lastPositionScroll; }
    public void setLastPositionScroll(Integer lastPositionScroll) { this.lastPositionScroll = lastPositionScroll; }

    public BigDecimal getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(BigDecimal completionPercentage) { this.completionPercentage = completionPercentage; }

    public String getLastChapterTitle() { return lastChapterTitle; }
    public void setLastChapterTitle(String lastChapterTitle) { this.lastChapterTitle = lastChapterTitle; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
