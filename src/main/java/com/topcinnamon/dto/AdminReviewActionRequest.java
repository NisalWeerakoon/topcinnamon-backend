package com.topcinnamon.dto;

public class AdminReviewActionRequest {
    private String action; // APPROVE, REJECT, DELETE
    private String adminNotes;

    // Constructors
    public AdminReviewActionRequest() {}

    public AdminReviewActionRequest(String action, String adminNotes) {
        this.action = action;
        this.adminNotes = adminNotes;
    }

    // Getters and Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}