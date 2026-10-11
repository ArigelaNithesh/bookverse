package com.bookverse.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ImportRequestDto {

    @NotBlank(message = "Search query or subject is required")
    private String query;

    @Min(value = 1, message = "Count must be at least 1")
    @Max(value = 100, message = "Batch count cannot exceed 100")
    private Integer limit = 20;

    public ImportRequestDto() {}

    public ImportRequestDto(String query, Integer limit) {
        this.query = query;
        this.limit = limit;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
}
