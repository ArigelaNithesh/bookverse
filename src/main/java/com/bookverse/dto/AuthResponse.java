package com.bookverse.dto;

import java.util.Set;

public class AuthResponse {

    private Long id;
    private String email;
    private String fullName;
    private Set<String> roles;

    public AuthResponse() {}

    public AuthResponse(Long id, String email, String fullName, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
