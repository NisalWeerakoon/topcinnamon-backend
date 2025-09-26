package Project.CinnamonProducts.service.payment;

import Project.CinnamonProducts.dto.payment.PaymentRequestDto;
import Project.CinnamonProducts.dto.payment.PaymentResponseDto;
import Project.CinnamonProducts.entity.payment.Payment;
import Project.CinnamonProducts.entity.payment.PaymentStatus;
import Project.CinnamonProducts.repository.payment.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private MockPaymentGateway mockPaymentGateway;
    
    /**
     * Create and process a new payment
     */
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        try {
            // Validate payment request
            validatePaymentRequest(request);
            
            // Create payment record
            Payment payment = createPaymentFromRequest(request);
            payment.setStatus(PaymentStatus.PROCESSING);
            payment = paymentRepository.save(payment);
            
            // Process payment through gateway
            MockPaymentGateway.MockGatewayResponse gatewayResponse = mockPaymentGateway.processPayment(request);
            
            // Update payment based on gateway response
            if (gatewayResponse.isSuccess()) {
                payment.markAsPaid(gatewayResponse.getTransactionId(), gatewayResponse.getRawResponse());
                paymentRepository.save(payment);
                
                PaymentResponseDto response = PaymentResponseDto.success(payment);
                response.setGatewayTransactionId(gatewayResponse.getTransactionId());
                return response;
            } else {
                payment.markAsFailed(gatewayResponse.getRawResponse());
                paymentRepository.save(payment);
                
                return PaymentResponseDto.error(
                    payment.getPaymentId(), 
                    gatewayResponse.getErrorCode(), 
                    gatewayResponse.getMessage()
                );
            }
            
        } catch (Exception e) {
            return PaymentResponseDto.error("UNKNOWN", "PROCESSING_ERROR", e.getMessage());
        }
    }
    
    /**
     * Create payment for redirect-based processing
     */
    public PaymentResponseDto createPayment(PaymentRequestDto request) {
        try {
            validatePaymentRequest(request);
            
            Payment payment = createPaymentFromRequest(request);
            payment.setExpiresAt(LocalDateTime.now().plusHours(24)); // 24 hours expiry
            payment = paymentRepository.save(payment);
            
            // Generate payment URL for redirect
            String paymentUrl = mockPaymentGateway.generatePaymentUrl(
                payment.getPaymentId(),
                payment.getAmount(),
                payment.getCurrency(),
                request.getReturnUrl()
            );
            
            return PaymentResponseDto.pending(payment, paymentUrl);
            
        } catch (Exception e) {
            return PaymentResponseDto.error("UNKNOWN", "CREATION_ERROR", e.getMessage());
        }
    }
    
    /**
     * Verify payment status
     */
    public PaymentResponseDto verifyPayment(String paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentId(paymentId);
        
        if (paymentOpt.isEmpty()) {
            return PaymentResponseDto.error(paymentId, "NOT_FOUND", "Payment not found");
        }
        
        Payment payment = paymentOpt.get();
        
        // If payment is still pending, verify with gateway
        if (payment.getStatus() == PaymentStatus.PENDING && payment.getGatewayTransactionId() != null) {
            MockPaymentGateway.MockGatewayResponse gatewayResponse = 
                mockPaymentGateway.verifyPayment(payment.getGatewayTransactionId());
            
            if (gatewayResponse.isSuccess()) {
                payment.markAsPaid(payment.getGatewayTransactionId(), gatewayResponse.getRawResponse());
                paymentRepository.save(payment);
            }
        }
        
        return new PaymentResponseDto(payment);
    }
    
    /**
     * Get payment by ID
     */
    public PaymentResponseDto getPayment(String paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentId(paymentId);
        
        if (paymentOpt.isEmpty()) {
            return PaymentResponseDto.error(paymentId, "NOT_FOUND", "Payment not found");
        }
        
        return new PaymentResponseDto(paymentOpt.get());
    }
    
    /**
     * Get payments by customer email
     */
    public List<PaymentResponseDto> getPaymentsByCustomer(String customerEmail) {
        List<Payment> payments = paymentRepository.findByCustomerEmail(customerEmail);
        return payments.stream()
                .map(PaymentResponseDto::new)
                .toList();
    }
    
    /**
     * Refund payment
     */
    public PaymentResponseDto refundPayment(String paymentId, BigDecimal amount, String reason) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentId(paymentId);
        
        if (paymentOpt.isEmpty()) {
            return PaymentResponseDto.error(paymentId, "NOT_FOUND", "Payment not found");
        }
        
        Payment payment = paymentOpt.get();
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return PaymentResponseDto.error(paymentId, "INVALID_STATUS", 
                "Only completed payments can be refunded");
        }
        
        if (amount.compareTo(payment.getAmount()) > 0) {
            return PaymentResponseDto.error(paymentId, "INVALID_AMOUNT", 
                "Refund amount cannot exceed payment amount");
        }
        
        // Process refund through gateway
        MockPaymentGateway.MockGatewayResponse gatewayResponse = 
            mockPaymentGateway.refundPayment(payment.getGatewayTransactionId(), amount, reason);
        
        if (gatewayResponse.isSuccess()) {
            // Update payment status
            if (amount.compareTo(payment.getAmount()) == 0) {
                payment.setStatus(PaymentStatus.REFUNDED);
            } else {
                payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
            }
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            
            PaymentResponseDto response = new PaymentResponseDto(payment);
            response.setGatewayTransactionId(gatewayResponse.getTransactionId());
            return response;
        } else {
            return PaymentResponseDto.error(paymentId, gatewayResponse.getErrorCode(), 
                gatewayResponse.getMessage());
        }
    }
    
    /**
     * Cancel payment
     */
    public PaymentResponseDto cancelPayment(String paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentId(paymentId);
        
        if (paymentOpt.isEmpty()) {
            return PaymentResponseDto.error(paymentId, "NOT_FOUND", "Payment not found");
        }
        
        Payment payment = paymentOpt.get();
        
        if (!payment.canBeProcessed()) {
            return PaymentResponseDto.error(paymentId, "INVALID_STATUS", 
                "Payment cannot be cancelled in current status");
        }
        
        payment.markAsCancelled();
        paymentRepository.save(payment);
        
        return new PaymentResponseDto(payment);
    }
    
    /**
     * Get payment statistics
     */
    public PaymentStatsDto getPaymentStats() {
        long totalPayments = paymentRepository.count();
        long completedPayments = paymentRepository.findByStatus(PaymentStatus.COMPLETED).size();
        long pendingPayments = paymentRepository.findByStatus(PaymentStatus.PENDING).size();
        long failedPayments = paymentRepository.findByStatus(PaymentStatus.FAILED).size();
        
        Double totalRevenue = paymentRepository.getTotalRevenueSince(LocalDateTime.now().minusDays(30));
        
        return new PaymentStatsDto(
            totalPayments,
            completedPayments,
            pendingPayments,
            failedPayments,
            totalRevenue != null ? totalRevenue : 0.0
        );
    }
    
    // Private helper methods
    
    private void validatePaymentRequest(PaymentRequestDto request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        if (request.requiresCardDetails() && !request.isValidCardDetails()) {
            throw new IllegalArgumentException("Card details are required for selected payment method");
        }
        
        // Additional validations can be added here
    }
    
    private Payment createPaymentFromRequest(PaymentRequestDto request) {
        Payment payment = new Payment(
            request.getAmount(),
            request.getCurrency(),
            request.getPaymentMethod(),
            request.getCustomerEmail(),
            request.getDescription()
        );
        
        payment.setCustomerName(request.getCustomerName());
        payment.setCustomerPhone(request.getCustomerPhone());
        payment.setBillingAddress(request.getBillingAddress());
        payment.setMetadata(request.getMetadata());
        
        return payment;
    }
    
    // Inner class for payment statistics
    public static class PaymentStatsDto {
        private final long totalPayments;
        private final long completedPayments;
        private final long pendingPayments;
        private final long failedPayments;
        private final double totalRevenue;
        
        public PaymentStatsDto(long totalPayments, long completedPayments, 
                              long pendingPayments, long failedPayments, double totalRevenue) {
            this.totalPayments = totalPayments;
            this.completedPayments = completedPayments;
            this.pendingPayments = pendingPayments;
            this.failedPayments = failedPayments;
            this.totalRevenue = totalRevenue;
        }
        
        // Getters
        public long getTotalPayments() { return totalPayments; }
        public long getCompletedPayments() { return completedPayments; }
        public long getPendingPayments() { return pendingPayments; }
        public long getFailedPayments() { return failedPayments; }
        public double getTotalRevenue() { return totalRevenue; }
        
        public double getSuccessRate() {
            return totalPayments > 0 ? (double) completedPayments / totalPayments * 100 : 0;
        }
    }
}
