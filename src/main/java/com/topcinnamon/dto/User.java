package com.topcinnamon.models;

public class User {
    private String id;
    private String fullName;
    private String email;
    private boolean authenticated;

    // Constructors
    public User() {}

    public User(String fullName, String email, boolean authenticated) {
        this.fullName = fullName;
        this.email = email;
        this.authenticated = authenticated;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
}