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

    // Query Methods
    public List<Review> getAllApprovedReviews() {
        return reviewRepository.findByApprovedTrue();
    }

    public List<Review> getApprovedReviewsByProduct(String productId) {
        try {
            System.out.println("🔍 Fetching approved reviews for product: " + productId);

            List<Review> allApproved = reviewRepository.findByApprovedTrue();
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
        return reviewRepository.findByApprovedFalse();
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
        Object[] stats = reviewRepository.findReviewStatistics(productId);
        List<Object[]> distribution = reviewRepository.findRatingDistribution(productId);

        ReviewStatistics reviewStats = new ReviewStatistics();

        if (stats != null && stats.length == 2) {
            reviewStats.setTotalReviews(((Long) stats[0]).intValue());
            reviewStats.setAverageRating((Double) stats[1]);
        }

        // Initialize rating distribution
        for (int i = 1; i <= 5; i++) {
            reviewStats.getRatingDistribution().put(i, 0L);
        }

        // Fill actual distribution
        for (Object[] dist : distribution) {
            Integer rating = (Integer) dist[0];
            Long count = (Long) dist[1];
            reviewStats.getRatingDistribution().put(rating, count);
        }

        return reviewStats;
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
}