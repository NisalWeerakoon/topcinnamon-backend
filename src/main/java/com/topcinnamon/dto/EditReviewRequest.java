package com.topcinnamon.dto;

import jakarta.validation.constraints.*;

public class EditReviewRequest {
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    @NotBlank(message = "Review title is required")
    @Size(max = 200, message = "Review title is too long")
    private String reviewTitle;

    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment is too long")
    private String comment;

    @NotBlank(message = "Edit token is required")
    private String editToken;

    private String productType = "Cinnamon Quills (Alba)";

    private boolean verifiedPurchase = false;

    // Constructors
    public EditReviewRequest() {}

    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getReviewTitle() { return reviewTitle; }
    public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getEditToken() { return editToken; }
    public void setEditToken(String editToken) { this.editToken = editToken; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }
}