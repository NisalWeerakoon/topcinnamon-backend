package com.topcinnamon.dto;

import jakarta.validation.constraints.*;

public class ReviewRequest {
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

    private String productId;
    private String productName;
    private boolean verifiedPurchase = false;
    private String productType = "Cinnamon Quills (Alba)";


    // Constructors
    public ReviewRequest() {}

    public ReviewRequest(String customerName, String email, int rating, String comment,
                         String reviewTitle, String productId, String productName) {
        this.customerName = customerName;
        this.email = email;
        this.rating = rating;
        this.comment = comment;
        this.reviewTitle = reviewTitle;
        this.productId = productId;
        this.productName = productName;
    }

    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getReviewTitle() { return reviewTitle; }
    public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

}