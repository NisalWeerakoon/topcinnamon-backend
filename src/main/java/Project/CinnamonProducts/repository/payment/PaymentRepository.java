package Project.CinnamonProducts.repository.payment;

import Project.CinnamonProducts.entity.payment.Payment;
import Project.CinnamonProducts.entity.payment.PaymentMethod;
import Project.CinnamonProducts.entity.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentId(String paymentId);
    
    List<Payment> findByCustomerEmail(String customerEmail);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :fromDate AND p.createdAt <= :toDate")
    List<Payment> findPaymentsBetweenDates(@Param("fromDate") LocalDateTime fromDate, 
                                          @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.expiresAt < :currentTime")
    List<Payment> findExpiredPayments(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' AND p.customerEmail = :email")
    long countSuccessfulPaymentsByCustomer(@Param("email") String customerEmail);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt >= :fromDate")
    Double getTotalRevenueSince(@Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT p FROM Payment p WHERE p.gatewayTransactionId = :transactionId")
    Optional<Payment> findByGatewayTransactionId(@Param("transactionId") String gatewayTransactionId);
}
