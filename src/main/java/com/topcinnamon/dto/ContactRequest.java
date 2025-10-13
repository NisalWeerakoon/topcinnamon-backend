package com.topcinnamon.dto;

import jakarta.validation.constraints.*;

public class ContactRequest {

    @NotBlank(message = "Name cannot be empty") // <-- FIX
    private String name;

    @NotBlank(message = "Email is required") // <-- FIX
    @Email(message = "Must be a valid email address") // <-- FIX
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{7,25}$", message = "Invalid phone number format")
    private String phone;

    private String country;
    private String subject;

    @NotBlank(message = "Message cannot be empty") // <-- FIX
    @Size(max = 1000, message = "Message is too long")
    private String message;

    // Constructors
    public ContactRequest() {}

    public ContactRequest(String name, String email, String phone, String country, String subject, String message) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.subject = subject;
        this.message = message;
    }

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
}