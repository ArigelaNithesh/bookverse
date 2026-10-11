package com.bookverse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateBookmarkRequest {

    @NotBlank(message = "Bookmark title is required")
    private String title;

    @NotNull(message = "Position index is required")
    private Integer positionIndex;

    private String note;

    public CreateBookmarkRequest() {}

    public CreateBookmarkRequest(String title, Integer positionIndex, String note) {
        this.title = title;
        this.positionIndex = positionIndex;
        this.note = note;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getPositionIndex() { return positionIndex; }
    public void setPositionIndex(Integer positionIndex) { this.positionIndex = positionIndex; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
