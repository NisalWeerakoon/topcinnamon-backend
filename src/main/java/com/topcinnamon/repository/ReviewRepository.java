package com.topcinnamon.repository;

import com.topcinnamon.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Basic queries
    List<Review> findByApprovedTrue();
    List<Review> findByApprovedFalse();
    Optional<Review> findByEditToken(String editToken);
    List<Review> findByReadByAdminFalseAndCreatedAtBefore(LocalDateTime date);
    List<Review> findByCanEditTrueAndCreatedAtBefore(LocalDateTime date);

    // Enhanced queries
    List<Review> findByApprovedTrueAndProductId(String productId);
    List<Review> findByApprovedTrueOrderByHelpfulVotesDesc();
    List<Review> findByApprovedTrueOrderByCreatedAtDesc();
    List<Review> findAllByOrderByCreatedAtDesc();
    List<Review> findByStatusOrderByCreatedAtDesc(String status);
    List<Review> findByApprovedFalseAndStatus(String status);

    // Dashboard queries
    long countByStatus(String status);
    long countByCreatedAtAfter(LocalDateTime date);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.approved = true")
    Optional<Double> findAverageRating();

    // Statistics queries
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.approved = true AND (:productId IS NULL OR r.productId = :productId) GROUP BY r.rating")
    List<Object[]> findRatingDistribution(@Param("productId") String productId);

    @Query("SELECT COUNT(r), COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.approved = true AND (:productId IS NULL OR r.productId = :productId)")
    Object[] findReviewStatistics(@Param("productId") String productId);

    // Search functionality
    @Query("SELECT r FROM Review r WHERE " +
            "LOWER(r.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.reviewTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.comment) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.productName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Review> searchReviews(@Param("query") String query);

    // For filtering
    @Query("SELECT r FROM Review r WHERE " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:productId IS NULL OR r.productId = :productId) AND " +
            "(LOWER(r.customerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.reviewTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.comment) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY r.createdAt DESC")
    List<Review> findWithFilters(@Param("status") String status,
                                 @Param("productId") String productId,
                                 @Param("search") String search);
}