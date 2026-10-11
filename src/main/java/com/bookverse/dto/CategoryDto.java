package com.bookverse.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryDto {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long parentId;
    private String parentName;
    private Integer displayOrder;
    private boolean active;
    private List<CategoryDto> subcategories = new ArrayList<>();

    public CategoryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<CategoryDto> getSubcategories() { return subcategories; }
    public void setSubcategories(List<CategoryDto> subcategories) { this.subcategories = subcategories; }
}
