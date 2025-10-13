package com.topcinnamon.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String country;
    private String subject;

    @Column(length = 1000)
    private String message;

    @Column(length = 1000)
    private String adminNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean readByAdmin = false;
    private boolean canEdit = true;
    private String editToken;
    private String status = "Pending";
    private boolean archived = false;
    private LocalDateTime reviewedAt;

    // Default constructor
    public Contact() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.editToken = UUID.randomUUID().toString().substring(0, 8);
        this.status = "Pending";
        this.archived = false;
    }

    // Check if editing is allowed
    public boolean isEditingAllowed() {
        return canEdit && createdAt.plusHours(1).isAfter(LocalDateTime.now());
    }

    // Calculate time remaining for editing
    public String getTimeRemaining() {
        LocalDateTime expiryTime = createdAt.plusHours(1);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(expiryTime)) {
            return "Editing time expired";
        }

        long minutesRemaining = java.time.Duration.between(now, expiryTime).toMinutes();
        if (minutesRemaining > 60) {
            long hours = minutesRemaining / 60;
            long minutes = minutesRemaining % 60;
            return String.format("You can edit for %d hours and %d minutes", hours, minutes);
        } else {
            return String.format("You can edit for %d minutes", minutesRemaining);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCountry() { return country; }
    public void setCountry(String country) {
        this.country = country;
        this.updatedAt = LocalDateTime.now();
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) {
        this.subject = subject;
        this.updatedAt = LocalDateTime.now();
    }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        this.message = message;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isReadByAdmin() { return readByAdmin; }
    public void setReadByAdmin(boolean readByAdmin) {
        this.readByAdmin = readByAdmin;
        if (readByAdmin && "Pending".equals(this.status)) {
            this.status = "Pending";// Keep as Pending even when read
        }
    }

    public boolean isCanEdit() { return canEdit; }
    public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }

    public String getEditToken() { return editToken; }
    public void setEditToken(String editToken) { this.editToken = editToken; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}