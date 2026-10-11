package com.bookverse.dto;

public class AuthorDto {

    private Long id;
    private String name;
    private String biography;
    private String website;

    public AuthorDto() {}

    public AuthorDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
