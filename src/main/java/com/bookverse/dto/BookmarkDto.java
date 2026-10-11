package com.bookverse.dto;

import java.time.LocalDateTime;

public class BookmarkDto {

    private Long id;
    private Long bookId;
    private String title;
    private Integer positionIndex;
    private String note;
    private LocalDateTime createdAt;

    public BookmarkDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getPositionIndex() { return positionIndex; }
    public void setPositionIndex(Integer positionIndex) { this.positionIndex = positionIndex; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
