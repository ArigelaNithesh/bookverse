package com.bookverse.dto;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryWorkDetailDto {

    private String key;
    private String title;
    private String description;
    private List<String> subjects = new ArrayList<>();
    private List<String> covers = new ArrayList<>();
    private List<String> authors = new ArrayList<>();
    private String firstPublishDate;
    private boolean inStore;
    private Long storeBookId;
    private String storeSlug;

    public DiscoveryWorkDetailDto() {}

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }

    public List<String> getCovers() { return covers; }
    public void setCovers(List<String> covers) { this.covers = covers; }

    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors; }

    public String getFirstPublishDate() { return firstPublishDate; }
    public void setFirstPublishDate(String firstPublishDate) { this.firstPublishDate = firstPublishDate; }

    public boolean isInStore() { return inStore; }
    public void setInStore(boolean inStore) { this.inStore = inStore; }

    public Long getStoreBookId() { return storeBookId; }
    public void setStoreBookId(Long storeBookId) { this.storeBookId = storeBookId; }

    public String getStoreSlug() { return storeSlug; }
    public void setStoreSlug(String storeSlug) { this.storeSlug = storeSlug; }
}
