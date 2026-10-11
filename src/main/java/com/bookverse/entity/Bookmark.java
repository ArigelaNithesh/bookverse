package com.bookverse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "position_index", nullable = false)
    private Integer positionIndex;

    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Bookmark() {}

    public Bookmark(User user, Book book, String title, Integer positionIndex, String note) {
        this.user = user;
        this.book = book;
        this.title = title;
        this.positionIndex = positionIndex;
        this.note = note;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getPositionIndex() { return positionIndex; }
    public void setPositionIndex(Integer positionIndex) { this.positionIndex = positionIndex; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
