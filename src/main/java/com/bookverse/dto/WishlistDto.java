package com.bookverse.dto;

import java.util.ArrayList;
import java.util.List;

public class WishlistDto {

    private Long id;
    private List<BookSummaryDto> books = new ArrayList<>();

    public WishlistDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<BookSummaryDto> getBooks() { return books; }
    public void setBooks(List<BookSummaryDto> books) { this.books = books; }
}
