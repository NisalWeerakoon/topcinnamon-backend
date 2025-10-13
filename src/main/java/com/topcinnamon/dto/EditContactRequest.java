package com.topcinnamon.dto;

public class EditContactRequest {
    private String name;
    private String email;
    private String phone;
    private String country;
    private String subject;
    private String message;
    private String editToken;

    // Constructors
    public EditContactRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getEditToken() { return editToken; }
    public void setEditToken(String editToken) { this.editToken = editToken; }
}