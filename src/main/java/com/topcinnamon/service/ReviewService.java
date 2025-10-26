package com.topcinnamon.service;

import com.topcinnamon.models.Review;
import com.topcinnamon.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays; // ADD THIS IMPORT
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // User CRUD Operations
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public Optional<Review> getReviewByEditToken(String editToken) {
        return reviewRepository.findByEditToken(editToken);
    }

    public Review updateReview(Long id, Review updatedReview) {
        return reviewRepository.findById(id).map(review -> {
            review.setCustomerName(updatedReview.getCustomerName());
            review.setEmail(updatedReview.getEmail());
            review.setRating(updatedReview.getRating());
            review.setComment(updatedReview.getComment());
            review.setReviewTitle(updatedReview.getReviewTitle());
            review.setProductType(updatedReview.getProductType());
            review.setVerifiedPurchase(updatedReview.isVerifiedPurchase());
            review.setUpdatedAt(LocalDateTime.now());
            return reviewRepository.save(review);
        }).orElse(null);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public Review markHelpful(Long id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            review.setHelpfulVotes(review.getHelpfulVotes() + 1);
            return reviewRepository.save(review);
        }
        return null;
    }

    public List<Review> getAllApprovedReviews() {
        return reviewRepository.findByStatus("APPROVED"); // Changed
    }

    public List<Review> getApprovedReviewsByProduct(String productId) {
        try {
            System.out.println("🔍 Fetching approved reviews for product: " + productId);

            List<Review> allApproved = reviewRepository.findByStatus("APPROVED"); // Changed
            System.out.println("📝 Total approved reviews: " + allApproved.size());

            if (productId == null || productId.trim().isEmpty()) {
                return allApproved;
            }

            // Traditional approach without streams
            List<Review> productReviews = new ArrayList<>();
            for (Review review : allApproved) {
                if (productId.equals(review.getProductId())) {
                    productReviews.add(review);
                }
            }

            // Sort by creation date (newest first)
            productReviews.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

            System.out.println("✅ Product-specific reviews: " + productReviews.size());
            return productReviews;

        } catch (Exception e) {
            System.err.println("❌ Error fetching approved reviews: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Review> getAllPendingReviews() {
        // Return both SUBMITTED and UNDER_REVIEW as pending
        List<Review> pending = new ArrayList<>();
        pending.addAll(reviewRepository.findByStatus("SUBMITTED"));
        pending.addAll(reviewRepository.findByStatus("UNDER_REVIEW"));
        return pending;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // ADMIN REVIEW MANAGEMENT METHODS
    public List<Review> getAllSubmittedReviews() {
        return reviewRepository.findByStatusOrderByCreatedAtDesc("SUBMITTED");
    }

    public List<Review> getAllUnderReview() {
        return reviewRepository.findByStatusOrderByCreatedAtDesc("UNDER_REVIEW");
    }

    public Review markAsUnderReview(Long id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            review.markAsUnderReview();
            return reviewRepository.save(review);
        }
        return null;
    }

    public Review approveReview(Long id, String adminNotes) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            review.approve(adminNotes);
            return reviewRepository.save(review);
        }
        return null;
    }

    public Review rejectReview(Long id, String adminNotes) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            review.reject(adminNotes);
            return reviewRepository.save(review);
        }
        return null;
    }

    public Review getReviewDetails(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public Review markAsRead(Long id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            review.setReadByAdmin(true);
            review.setUpdatedAt(LocalDateTime.now());
            return reviewRepository.save(review);
        }
        return null;
    }

    // Bulk operations
    public void bulkDeleteReviews(List<Long> reviewIds) {
        for (Long id : reviewIds) {
            deleteReview(id);
        }
    }

    public void bulkApproveReviews(List<Long> reviewIds, String adminNotes) {
        for (Long id : reviewIds) {
            Review review = reviewRepository.findById(id).orElse(null);
            if (review != null && !"APPROVED".equals(review.getStatus())) {
                review.approve(adminNotes);
                reviewRepository.save(review);
            }
        }
    }

    public void bulkRejectReviews(List<Long> reviewIds, String adminNotes) {
        for (Long id : reviewIds) {
            Review review = reviewRepository.findById(id).orElse(null);
            if (review != null && !"REJECTED".equals(review.getStatus())) {
                review.reject(adminNotes);
                reviewRepository.save(review);
            }
        }
    }

    public List<Review> getReviewsByStatus(String status) {
        return reviewRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    // Statistics methods
    public ReviewStatistics getReviewStatistics(String productId) {
        try {
            System.out.println("🔍 Calculating statistics for product: " + productId);

            // Get ALL reviews first for debugging
            List<Review> allReviews = reviewRepository.findAll();
            System.out.println("📊 Total reviews in database: " + allReviews.size());

            // Count approved reviews manually for statistics
            List<Review> approvedReviews;
            if (productId == null || productId.trim().isEmpty()) {
                approvedReviews = reviewRepository.findByStatus("APPROVED");
            } else {
                approvedReviews = reviewRepository.findByStatus("APPROVED").stream()
                        .filter(review -> productId.equals(review.getProductId()))
                        .collect(Collectors.toList());
            }

            System.out.println("✅ Approved reviews count: " + approvedReviews.size());

            ReviewStatistics reviewStats = new ReviewStatistics();

            if (!approvedReviews.isEmpty()) {
                // Calculate average rating from approved reviews
                double average = approvedReviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);

                reviewStats.setTotalReviews(approvedReviews.size());
                reviewStats.setAverageRating(average);

                // Calculate rating distribution
                Map<Integer, Long> distribution = approvedReviews.stream()
                        .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

                // Initialize all ratings 1-5
                for (int i = 1; i <= 5; i++) {
                    reviewStats.getRatingDistribution().put(i, distribution.getOrDefault(i, 0L));
                }
            } else {
                // No approved reviews
                reviewStats.setTotalReviews(0);
                reviewStats.setAverageRating(0.0);
                for (int i = 1; i <= 5; i++) {
                    reviewStats.getRatingDistribution().put(i, 0L);
                }
            }

            System.out.println("📈 Final statistics:");
            System.out.println("   - Total Reviews: " + reviewStats.getTotalReviews());
            System.out.println("   - Average Rating: " + reviewStats.getAverageRating());
            System.out.println("   - Distribution: " + reviewStats.getRatingDistribution());

            return reviewStats;

        } catch (Exception e) {
            System.err.println("❌ Error calculating statistics: " + e.getMessage());
            e.printStackTrace();

            // Return empty stats on error
            ReviewStatistics emptyStats = new ReviewStatistics();
            emptyStats.setTotalReviews(0);
            emptyStats.setAverageRating(0.0);
            for (int i = 1; i <= 5; i++) {
                emptyStats.getRatingDistribution().put(i, 0L);
            }
            return emptyStats;
        }
    }

    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            System.out.println("📊 Generating dashboard statistics...");

            // Try using query methods first, fall back to manual counting
            try {
                stats.put("totalSubmitted", reviewRepository.countByStatus("SUBMITTED"));
                stats.put("totalUnderReview", reviewRepository.countByStatus("UNDER_REVIEW"));
                stats.put("totalApproved", reviewRepository.countByStatus("APPROVED"));
                stats.put("totalRejected", reviewRepository.countByStatus("REJECTED"));
            } catch (Exception e) {
                System.out.println("⚠️ Using manual counting for status stats");
                // Manual counting fallback
                List<Review> allReviews = reviewRepository.findAll();
                stats.put("totalSubmitted", allReviews.stream().filter(r -> "SUBMITTED".equals(r.getStatus())).count());
                stats.put("totalUnderReview", allReviews.stream().filter(r -> "UNDER_REVIEW".equals(r.getStatus())).count());
                stats.put("totalApproved", allReviews.stream().filter(r -> "APPROVED".equals(r.getStatus())).count());
                stats.put("totalRejected", allReviews.stream().filter(r -> "REJECTED".equals(r.getStatus())).count());
            }

            stats.put("totalReviews", reviewRepository.count());

            // Recent submissions (last 24 hours)
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            try {
                stats.put("recentSubmissions", reviewRepository.countByCreatedAtAfter(yesterday));
            } catch (Exception e) {
                System.out.println("⚠️ Using manual counting for recent submissions");
                List<Review> allReviews = reviewRepository.findAll();
                stats.put("recentSubmissions", allReviews.stream()
                        .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().isAfter(yesterday))
                        .count());
            }

            System.out.println("✅ Dashboard stats generated: " + stats);

        } catch (Exception e) {
            System.err.println("❌ Error generating dashboard stats: " + e.getMessage());
            e.printStackTrace();

            // Return safe default values
            stats.put("totalSubmitted", 0);
            stats.put("totalUnderReview", 0);
            stats.put("totalApproved", 0);
            stats.put("totalRejected", 0);
            stats.put("totalReviews", 0);
            stats.put("recentSubmissions", 0);
        }

        return stats;
    }

    // Statistics DTOs
    public static class ReviewStatistics {
        private int totalReviews;
        private double averageRating;
        private Map<Integer, Long> ratingDistribution = new HashMap<>();

        // Getters and Setters
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

        public Map<Integer, Long> getRatingDistribution() { return ratingDistribution; }
        public void setRatingDistribution(Map<Integer, Long> ratingDistribution) {
            this.ratingDistribution = ratingDistribution;
        }
    }

    // Scheduled tasks
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteOldUnreadReviews() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Review> oldUnreadReviews = reviewRepository
                .findByReadByAdminFalseAndCreatedAtBefore(thirtyDaysAgo);

        if (!oldUnreadReviews.isEmpty()) {
            reviewRepository.deleteAll(oldUnreadReviews);
            System.out.println("Deleted " + oldUnreadReviews.size() + " old unread reviews");
        }
    }

    @Scheduled(cron = "0 */30 * * * ?")
    public void disableEditingForOldReviews() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Review> oldReviews = reviewRepository
                .findByCanEditTrueAndCreatedAtBefore(oneHourAgo);

        for (Review review : oldReviews) {
            review.setCanEdit(false);
            reviewRepository.save(review);
        }

        if (!oldReviews.isEmpty()) {
            System.out.println("Disabled editing for " + oldReviews.size() + " old reviews");
        }
    }

    // Add this method to ReviewService.java for debugging
    public ReviewStatistics getReviewStatisticsWithDebug(String productId) {
        try {
            System.out.println("🐛 DEBUG STATISTICS START ===========================");
            System.out.println("📊 Calculating statistics for product: " + productId);

            // Check how many approved reviews exist
            List<Review> allApproved = reviewRepository.findByStatus("APPROVED");
            System.out.println("✅ Total APPROVED reviews in database: " + allApproved.size());

            // Show details of approved reviews
            if (!allApproved.isEmpty()) {
                System.out.println("📝 Approved reviews details:");
                for (Review review : allApproved) {
                    System.out.println("   - ID: " + review.getId() +
                            " | Rating: " + review.getRating() +
                            " | Product: " + review.getProductId() +
                            " | Status: " + review.getStatus() +
                            " | Approved: " + review.isApproved());
                }
            }

            // Test the repository query directly
            System.out.println("🔍 Testing repository query...");
            Object[] repoStats = reviewRepository.findReviewStatistics(productId);
            System.out.println("📡 Repository query result: " + Arrays.toString(repoStats));

            // Manual calculation for comparison
            System.out.println("🔢 Manual calculation...");
            List<Review> approvedReviews;
            if (productId == null || productId.trim().isEmpty()) {
                approvedReviews = allApproved;
            } else {
                approvedReviews = allApproved.stream()
                        .filter(review -> productId.equals(review.getProductId()))
                        .collect(Collectors.toList());
            }

            System.out.println("✅ Manual count: " + approvedReviews.size() + " approved reviews");

            if (!approvedReviews.isEmpty()) {
                double manualAvg = approvedReviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);
                System.out.println("📈 Manual average: " + manualAvg);
            }

            // Now call the original method
            System.out.println("🔄 Calling original getReviewStatistics method...");
            ReviewStatistics stats = getReviewStatistics(productId);

            System.out.println("📊 Final statistics result:");
            System.out.println("   - Total Reviews: " + stats.getTotalReviews());
            System.out.println("   - Average Rating: " + stats.getAverageRating());
            System.out.println("   - Distribution: " + stats.getRatingDistribution());

            System.out.println("🐛 DEBUG STATISTICS END =============================");

            return stats;

        } catch (Exception e) {
            System.err.println("❌ DEBUG STATISTICS ERROR: " + e.getMessage());
            e.printStackTrace();
            return new ReviewStatistics(); // Return empty stats
        }
    }
}