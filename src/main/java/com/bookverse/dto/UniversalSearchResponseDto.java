package com.bookverse.dto;

import java.util.ArrayList;
import java.util.List;

public class UniversalSearchResponseDto {

    private String query;
    private long totalFound;
    private int page;
    private int limit;
    private List<BookSummaryDto> localResults = new ArrayList<>();
    private List<DiscoveredBookDto> discoveredResults = new ArrayList<>();
    private boolean hasMoreExternal;

    public UniversalSearchResponseDto() {}

    public UniversalSearchResponseDto(String query, long totalFound, int page, int limit,
                                      List<BookSummaryDto> localResults,
                                      List<DiscoveredBookDto> discoveredResults,
                                      boolean hasMoreExternal) {
        this.query = query;
        this.totalFound = totalFound;
        this.page = page;
        this.limit = limit;
        this.localResults = localResults;
        this.discoveredResults = discoveredResults;
        this.hasMoreExternal = hasMoreExternal;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public long getTotalFound() { return totalFound; }
    public void setTotalFound(long totalFound) { this.totalFound = totalFound; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public List<BookSummaryDto> getLocalResults() { return localResults; }
    public void setLocalResults(List<BookSummaryDto> localResults) { this.localResults = localResults; }

    public List<DiscoveredBookDto> getDiscoveredResults() { return discoveredResults; }
    public void setDiscoveredResults(List<DiscoveredBookDto> discoveredResults) { this.discoveredResults = discoveredResults; }

    public boolean isHasMoreExternal() { return hasMoreExternal; }
    public void setHasMoreExternal(boolean hasMoreExternal) { this.hasMoreExternal = hasMoreExternal; }
}
