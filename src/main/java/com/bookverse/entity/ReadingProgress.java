package com.bookverse.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "book_id"})
})
public class ReadingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "last_position_scroll")
    private Integer lastPositionScroll = 0;

    @Column(name = "completion_percentage", precision = 5, scale = 2)
    private BigDecimal completionPercentage = BigDecimal.ZERO;

    @Column(name = "last_chapter_title")
    private String lastChapterTitle;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.updatedAt = LocalDateTime.now();
    }

    public ReadingProgress() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Integer getLastPositionScroll() { return lastPositionScroll; }
    public void setLastPositionScroll(Integer lastPositionScroll) { this.lastPositionScroll = lastPositionScroll; }

    public BigDecimal getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(BigDecimal completionPercentage) { this.completionPercentage = completionPercentage; }

    public String getLastChapterTitle() { return lastChapterTitle; }
    public void setLastChapterTitle(String lastChapterTitle) { this.lastChapterTitle = lastChapterTitle; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
