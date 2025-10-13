package com.topcinnamon.controller;

import com.topcinnamon.dto.ReviewRequest;
import com.topcinnamon.dto.EditReviewRequest;
import com.topcinnamon.models.Review;
import com.topcinnamon.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // USER ENDPOINTS
    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequest reviewRequest) {
        try {
            System.out.println("🎯 SUBMISSION STARTED =======================================");
            System.out.println("📦 Received review submission data:");
            System.out.println("   👤 Customer: " + reviewRequest.getCustomerName());
            System.out.println("   📧 Email: " + reviewRequest.getEmail());
            System.out.println("   ⭐ Rating: " + reviewRequest.getRating());
            System.out.println("   📝 Title: " + reviewRequest.getReviewTitle());
            System.out.println("   💬 Comment: " + (reviewRequest.getComment() != null ?
                    reviewRequest.getComment().substring(0, Math.min(50, reviewRequest.getComment().length())) + "..." : "null"));
            System.out.println("   🏷️ Product ID: " + reviewRequest.getProductId());
            System.out.println("   🏷️ Product Name: " + reviewRequest.getProductName());
            System.out.println("   🌸 Product Type: " + reviewRequest.getProductType());
            System.out.println("   ✅ Verified Purchase: " + reviewRequest.isVerifiedPurchase());

            // Validation
            if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
                System.err.println("❌ VALIDATION FAILED: Rating must be between 1 and 5");
                return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
            }

            // Create review entity
            Review review = new Review(
                    reviewRequest.getCustomerName(),
                    reviewRequest.getEmail(),
                    reviewRequest.getRating(),
                    reviewRequest.getComment(),
                    reviewRequest.getReviewTitle(),
                    reviewRequest.getProductId(),
                    reviewRequest.getProductName()
            );

            // Set additional fields
            review.setProductType(reviewRequest.getProductType());
            review.setVerifiedPurchase(reviewRequest.isVerifiedPurchase());

            System.out.println("💾 Attempting to save review to database...");

            // Save to database
            Review savedReview = reviewService.saveReview(review);

            System.out.println("✅ REVIEW SAVED SUCCESSFULLY!");
            System.out.println("   📋 Saved Review ID: " + savedReview.getId());
            System.out.println("   🔑 Edit Token: " + savedReview.getEditToken());
            System.out.println("   📅 Created At: " + savedReview.getCreatedAt());
            System.out.println("   📊 Status: " + savedReview.getStatus());
            System.out.println("   ✅ Approved: " + savedReview.isApproved());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Review submitted successfully! It will be visible after approval.");
            response.put("submissionId", savedReview.getId());
            response.put("editToken", savedReview.getEditToken());
            response.put("editAllowedUntil", savedReview.getCreatedAt().plusHours(1));

            System.out.println("🎯 SUBMISSION COMPLETED =====================================");
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            System.err.println("💥 SUBMISSION FAILED WITH EXCEPTION!");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("   Exception Type: " + e.getClass().getName());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error submitting review: " + e.getMessage());
        }
    }

    // Secure endpoint for authenticated users (simplified version)
    @PostMapping("/secure")
    public ResponseEntity<?> submitReviewSecure(@RequestBody ReviewRequest reviewRequest) {
        try {
            // For authenticated users, mark as verified purchase
            reviewRequest.setVerifiedPurchase(true);

            // Ensure required fields are present
            if (reviewRequest.getCustomerName() == null || reviewRequest.getCustomerName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name is required");
            }
            if (reviewRequest.getEmail() == null || reviewRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            return submitReview(reviewRequest);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error submitting review: " + e.getMessage());
        }
    }

    // DEBUG ENDPOINT
    @GetMapping("/debug/all")
    public ResponseEntity<?> getAllReviewsDebug() {
        try {
            List<Review> allReviews = reviewService.getAllReviews();
            System.out.println("📊 DEBUG: Total reviews in database: " + allReviews.size());

            if (allReviews.isEmpty()) {
                System.out.println("📭 Database is empty - no reviews found");
            } else {
                allReviews.forEach(review -> {
                    System.out.println("   📋 Review ID: " + review.getId() +
                            " | Status: " + review.getStatus() +
                            " | Approved: " + review.isApproved() +
                            " | Product: " + review.getProductId() +
                            " | Customer: " + review.getCustomerName());
                });
            }

            return ResponseEntity.ok().body(allReviews);
        } catch (Exception e) {
            System.err.println("❌ DEBUG ENDPOINT FAILED: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/edit/{editToken}")
    public ResponseEntity<?> getReviewForEditing(@PathVariable String editToken) {
        Optional<Review> review = reviewService.getReviewByEditToken(editToken);
        if (review.isPresent()) {
            Review reviewObj = review.get();
            if (!reviewObj.isEditingAllowed()) {
                return ResponseEntity.badRequest().body("Editing time has expired. You can no longer edit this review.");
            }
            return ResponseEntity.ok().body(reviewObj);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long id,
            @RequestBody EditReviewRequest editRequest) {
        try {
            Optional<Review> existingReview = reviewService.getReviewById(id);
            if (existingReview.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Review review = existingReview.get();

            // Validate edit token for user edits
            if (!review.getEditToken().equals(editRequest.getEditToken())) {
                return ResponseEntity.badRequest().body("Invalid edit token");
            }

            if (!review.isEditingAllowed()) {
                return ResponseEntity.badRequest().body("Editing time has expired");
            }

            if (editRequest.getRating() < 1 || editRequest.getRating() > 5) {
                return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
            }

            // Update review fields
            review.setCustomerName(editRequest.getCustomerName());
            review.setEmail(editRequest.getEmail());
            review.setRating(editRequest.getRating());
            review.setComment(editRequest.getComment());
            review.setReviewTitle(editRequest.getReviewTitle());
            review.setProductType(editRequest.getProductType());
            review.setVerifiedPurchase(editRequest.isVerifiedPurchase());
            review.setUpdatedAt(LocalDateTime.now());

            Review updatedReview = reviewService.saveReview(review);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review updated successfully!");
            response.put("review", updatedReview);
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating review: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long id,
            @RequestParam String editToken) {
        try {
            Optional<Review> review = reviewService.getReviewById(id);
            if (review.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // For users, validate edit token and time
            if (editToken != null && !review.get().getEditToken().equals(editToken)) {
                return ResponseEntity.badRequest().body("Invalid edit token");
            }

            if (editToken != null && !review.get().isEditingAllowed()) {
                return ResponseEntity.badRequest().body("Deletion time has expired");
            }

            reviewService.deleteReview(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review deleted successfully!");
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting review: " + e.getMessage());
        }
    }

    // PUBLIC ENDPOINTS
    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedReviews(@RequestParam(required = false) String productId) {
        try {
            List<Review> reviews = reviewService.getApprovedReviewsByProduct(productId);
            return ResponseEntity.ok().body(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reviews: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getReviewStatistics(@RequestParam(required = false) String productId) {
        try {
            ReviewService.ReviewStatistics stats = reviewService.getReviewStatistics(productId);
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching statistics: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/helpful")
    public ResponseEntity<?> markReviewAsHelpful(@PathVariable Long id) {
        try {
            Review updatedReview = reviewService.markHelpful(id);
            if (updatedReview != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Thank you for your feedback!");
                response.put("helpfulVotes", updatedReview.getHelpfulVotes());
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking review as helpful: " + e.getMessage());
        }
    }

    // ADMIN ENDPOINTS
    @GetMapping("/admin/all")
    public ResponseEntity<List<Review>> getAllReviewsAdmin() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<Review>> getPendingReviews() {
        List<Review> reviews = reviewService.getAllPendingReviews();
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/admin/submitted")
    public ResponseEntity<List<Review>> getSubmittedReviews() {
        List<Review> reviews = reviewService.getAllSubmittedReviews();
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/admin/under-review")
    public ResponseEntity<List<Review>> getUnderReview() {
        List<Review> reviews = reviewService.getAllUnderReview();
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<Review>> getReviewsByStatus(@PathVariable String status) {
        List<Review> reviews = reviewService.getReviewsByStatus(status.toUpperCase());
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/admin/{id}/details")
    public ResponseEntity<?> getReviewDetails(@PathVariable Long id) {
        Review review = reviewService.getReviewDetails(id);
        if (review != null) {
            return ResponseEntity.ok().body(review);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/admin/{id}/start-review")
    public ResponseEntity<?> startReview(@PathVariable Long id) {
        Review review = reviewService.markAsUnderReview(id);
        if (review != null) {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Review marked as under review",
                    "review", review
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/admin/{id}/approve")
    public ResponseEntity<?> approveReview(@PathVariable Long id,
                                           @RequestBody(required = false) Map<String, String> request) {
        String adminNotes = request != null ? request.get("adminNotes") : "";
        Review review = reviewService.approveReview(id, adminNotes);
        if (review != null) {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Review approved successfully!",
                    "review", review
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/admin/{id}/reject")
    public ResponseEntity<?> rejectReview(@PathVariable Long id,
                                          @RequestBody(required = false) Map<String, String> request) {
        String adminNotes = request != null ? request.get("adminNotes") : "";
        Review review = reviewService.rejectReview(id, adminNotes);
        if (review != null) {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Review rejected successfully!",
                    "review", review
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Bulk actions
    @PostMapping("/admin/bulk-approve")
    public ResponseEntity<?> bulkApproveReviews(@RequestBody BulkActionRequest request) {
        try {
            reviewService.bulkApproveReviews(request.getReviewIds(), request.getAdminNotes());
            return ResponseEntity.ok().body(Map.of("message", "Reviews approved successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error bulk approving reviews: " + e.getMessage());
        }
    }

    @PostMapping("/admin/bulk-reject")
    public ResponseEntity<?> bulkRejectReviews(@RequestBody BulkActionRequest request) {
        try {
            reviewService.bulkRejectReviews(request.getReviewIds(), request.getAdminNotes());
            return ResponseEntity.ok().body(Map.of("message", "Reviews rejected successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error bulk rejecting reviews: " + e.getMessage());
        }
    }

    @GetMapping("/admin/dashboard/stats")
    public ResponseEntity<?> getAdminDashboardStats() {
        try {
            Map<String, Object> stats = reviewService.getAdminDashboardStats();
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            // Return default stats if service fails
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("totalSubmitted", 0);
            defaultStats.put("totalUnderReview", 0);
            defaultStats.put("totalApproved", 0);
            defaultStats.put("totalRejected", 0);
            defaultStats.put("totalReviews", 0);
            defaultStats.put("recentSubmissions", 0);
            return ResponseEntity.ok().body(defaultStats);
        }
    }

    @PutMapping("/admin/{id}/mark-read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Review updatedReview = reviewService.markAsRead(id);
        if (updatedReview != null) {
            return ResponseEntity.ok().body("Review marked as read");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> adminDeleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok().body("Review deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting review: " + e.getMessage());
        }
    }

    @GetMapping("/admin/search")
    public ResponseEntity<List<Review>> searchReviews(@RequestParam String query) {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok().body(reviews);
    }

    // DTO for bulk actions
    public static class BulkActionRequest {
        private List<Long> reviewIds;
        private String adminNotes;

        // Getters and setters
        public List<Long> getReviewIds() { return reviewIds; }
        public void setReviewIds(List<Long> reviewIds) { this.reviewIds = reviewIds; }
        public String getAdminNotes() { return adminNotes; }
        public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    }

    // TEST ENDPOINTS
    @GetMapping("/test")
    public ResponseEntity<String> testGetEndpoint() {
        return ResponseEntity.ok("✅ ReviewController GET is working! " + LocalDateTime.now());
    }

    @PostMapping("/test")
    public ResponseEntity<?> testPostEndpoint(@RequestBody(required = false) Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ ReviewController POST is working!");
        response.put("timestamp", LocalDateTime.now());
        response.put("received", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/export/data")
    public ResponseEntity<?> getReviewsExportData(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String productId) {
        try {
            List<Review> reviews;

            if (status != null && !status.isEmpty()) {
                reviews = reviewService.getReviewsByStatus(status.toUpperCase());
            } else {
                reviews = reviewService.getAllReviews();
            }

            // Filter by product ID if provided (using the String productId field)
            if (productId != null && !productId.isEmpty()) {
                reviews = reviews.stream()
                        .filter(review -> productId.equals(review.getProductId()))
                        .collect(Collectors.toList());
            }

            // Convert to export-friendly format
            List<Map<String, Object>> exportData = reviews.stream().map(review -> {
                Map<String, Object> reviewData = new HashMap<>();
                reviewData.put("id", review.getId());
                reviewData.put("customerName", review.getCustomerName());
                reviewData.put("email", review.getEmail());
                reviewData.put("rating", review.getRating());
                reviewData.put("reviewTitle", review.getReviewTitle());
                reviewData.put("comment", review.getComment());
                reviewData.put("productType", review.getProductType() != null ? review.getProductType() : "Not Specified");
                reviewData.put("productName", review.getProductName() != null ? review.getProductName() : "Not Specified");
                reviewData.put("productId", review.getProductId() != null ? review.getProductId() : "Not Specified");
                reviewData.put("status", review.getStatus());
                reviewData.put("verifiedPurchase", review.isVerifiedPurchase());
                reviewData.put("helpfulVotes", review.getHelpfulVotes());
                reviewData.put("createdAt", review.getCreatedAt());
                reviewData.put("approved", review.isApproved());
                reviewData.put("adminNotes", review.getAdminNotes() != null ? review.getAdminNotes() : "");
                return reviewData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok().body(exportData);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching export data: " + e.getMessage());
        }
    }

    @GetMapping("/admin/export/csv")
    public ResponseEntity<?> exportReviewsToCsv(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String productId) {
        try {
            List<Review> reviews;

            if (status != null && !status.isEmpty()) {
                reviews = reviewService.getReviewsByStatus(status.toUpperCase());
            } else {
                reviews = reviewService.getAllReviews();
            }

            // Filter by product ID if provided
            if (productId != null && !productId.isEmpty()) {
                reviews = reviews.stream()
                        .filter(review -> productId.equals(review.getProductId()))
                        .collect(Collectors.toList());
            }

            // Create CSV content
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("ID,Customer Name,Email,Rating,Review Title,Comment,Product Type,Product Name,Product ID,Status,Verified Purchase,Created At,Approved,Helpful Votes\n");

            for (Review review : reviews) {
                csvContent.append(escapeCsvField(String.valueOf(review.getId()))).append(",");
                csvContent.append(escapeCsvField(review.getCustomerName())).append(",");
                csvContent.append(escapeCsvField(review.getEmail())).append(",");
                csvContent.append(escapeCsvField(String.valueOf(review.getRating()))).append(",");
                csvContent.append(escapeCsvField(review.getReviewTitle())).append(",");
                csvContent.append(escapeCsvField(review.getComment())).append(",");
                csvContent.append(escapeCsvField(review.getProductType())).append(",");
                csvContent.append(escapeCsvField(review.getProductName())).append(",");
                csvContent.append(escapeCsvField(review.getProductId())).append(",");
                csvContent.append(escapeCsvField(review.getStatus())).append(",");
                csvContent.append(escapeCsvField(String.valueOf(review.isVerifiedPurchase()))).append(",");
                csvContent.append(escapeCsvField(review.getCreatedAt() != null ? review.getCreatedAt().toString() : "")).append(",");
                csvContent.append(escapeCsvField(String.valueOf(review.isApproved()))).append(",");
                csvContent.append(escapeCsvField(String.valueOf(review.getHelpfulVotes())));
                csvContent.append("\n");
            }

            // Return as file download
            byte[] csvBytes = csvContent.toString().getBytes();

            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv; charset=utf-8")
                    .header("Content-Disposition", "attachment; filename=reviews_export.csv")
                    .body(csvBytes);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error exporting reviews: " + e.getMessage());
        }
    }

    private String escapeCsvField(String field) {
        if (field == null) return "";
        // Escape quotes and wrap in quotes if contains comma or quote
        if (field.contains("\"") || field.contains(",") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}