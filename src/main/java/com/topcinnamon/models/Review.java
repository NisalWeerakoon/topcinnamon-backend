package com.topcinnamon.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private int rating;

    @Column(length = 1000)
    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean approved = false;

    private boolean readByAdmin = false;
    private boolean canEdit = true;
    private String editToken;

    // Enhanced status field
    private String status = "SUBMITTED"; // SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED

    // Enhanced fields
    private String reviewTitle;
    private String productId;
    private String productName;
    private int helpfulVotes = 0;
    private boolean verifiedPurchase = false;
    private String productType = "Cinnamon Quills (Alba)";
    private String adminNotes;
    private LocalDateTime reviewedAt;

    // Constructors
    public Review() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.editToken = generateEditToken();
        this.status = "SUBMITTED";
    }

    public Review(String customerName, String email, int rating, String comment,
                  String reviewTitle, String productId, String productName) {
        this();
        this.customerName = customerName;
        this.email = email;
        this.rating = rating;
        this.comment = comment;
        this.reviewTitle = reviewTitle;
        this.productId = productId;
        this.productName = productName;
    }

    private String generateEditToken() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public boolean isEditingAllowed() {
        return canEdit && createdAt.plusHours(1).isAfter(LocalDateTime.now());
    }

    // Status management methods
    public void markAsUnderReview() {
        this.status = "UNDER_REVIEW";
        this.readByAdmin = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void approve(String adminNotes) {
        this.status = "APPROVED";
        this.approved = true;
        this.readByAdmin = true;
        this.adminNotes = adminNotes;
        this.reviewedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String adminNotes) {
        this.status = "REJECTED";
        this.approved = false;
        this.readByAdmin = true;
        this.adminNotes = adminNotes;
        this.reviewedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public int getRating() { return rating; }
    public void setRating(int rating) {
        this.rating = rating;
        this.updatedAt = LocalDateTime.now();
    }

    public String getComment() { return comment; }
    public void setComment(String comment) {
        this.comment = comment;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) {
        this.approved = approved;
        this.status = approved ? "APPROVED" : "REJECTED";
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isReadByAdmin() { return readByAdmin; }
    public void setReadByAdmin(boolean readByAdmin) {
        this.readByAdmin = readByAdmin;
        this.updatedAt = LocalDateTime.now();
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

    public String getReviewTitle() { return reviewTitle; }
    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
        this.updatedAt = LocalDateTime.now();
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getHelpfulVotes() { return helpfulVotes; }
    public void setHelpfulVotes(int helpfulVotes) { this.helpfulVotes = helpfulVotes; }

    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
        this.updatedAt = LocalDateTime.now();
    }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}