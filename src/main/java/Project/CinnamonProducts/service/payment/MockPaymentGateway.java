package Project.CinnamonProducts.service.payment;

import Project.CinnamonProducts.dto.payment.PaymentRequestDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class MockPaymentGateway {
    
    private final Random random = new Random();
    
    // Mock gateway configuration
    private static final String GATEWAY_NAME = "MockPaymentGateway";
    private static final String SUCCESS_RATE = "85"; // 85% success rate for testing
    private static final String BASE_PAYMENT_URL = "http://localhost:8080/api/payment/mock-pay";
    
    /**
     * Process payment through mock gateway
     */
    public MockGatewayResponse processPayment(PaymentRequestDto request) {
        // Simulate processing delay
        try {
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Generate mock transaction ID
        String transactionId = generateTransactionId();
        
        // Simulate success/failure based on configuration
        boolean isSuccess = simulateSuccess();
        
        MockGatewayResponse response = new MockGatewayResponse();
        response.setTransactionId(transactionId);
        response.setGatewayName(GATEWAY_NAME);
        response.setProcessedAt(LocalDateTime.now());
        response.setRawResponse(generateRawResponse(request, transactionId, isSuccess));
        
        if (isSuccess) {
            response.setStatus("success");
            response.setMessage("Payment processed successfully");
            response.setPaymentUrl(BASE_PAYMENT_URL + "/" + transactionId);
        } else {
            response.setStatus("failed");
            response.setMessage(generateFailureReason());
            response.setErrorCode(generateErrorCode());
        }
        
        return response;
    }
    
    /**
     * Verify payment status
     */
    public MockGatewayResponse verifyPayment(String transactionId) {
        // Simulate verification delay
        try {
            Thread.sleep(500 + random.nextInt(1000)); // 0.5-1.5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        MockGatewayResponse response = new MockGatewayResponse();
        response.setTransactionId(transactionId);
        response.setGatewayName(GATEWAY_NAME);
        response.setProcessedAt(LocalDateTime.now());
        
        // For demo purposes, assume all verifications succeed
        response.setStatus("success");
        response.setMessage("Payment verified successfully");
        response.setRawResponse("{\"status\":\"verified\",\"transaction_id\":\"" + transactionId + "\"}");
        
        return response;
    }
    
    /**
     * Refund payment
     */
    public MockGatewayResponse refundPayment(String transactionId, BigDecimal amount, String reason) {
        // Simulate refund processing delay
        try {
            Thread.sleep(1500 + random.nextInt(2000)); // 1.5-3.5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String refundId = generateRefundId();
        boolean isSuccess = simulateSuccess(); // 85% success rate
        
        MockGatewayResponse response = new MockGatewayResponse();
        response.setTransactionId(refundId);
        response.setGatewayName(GATEWAY_NAME);
        response.setProcessedAt(LocalDateTime.now());
        
        if (isSuccess) {
            response.setStatus("success");
            response.setMessage("Refund processed successfully");
            response.setRawResponse(generateRefundResponse(transactionId, refundId, amount, reason));
        } else {
            response.setStatus("failed");
            response.setMessage("Refund failed - insufficient funds");
            response.setErrorCode("REFUND_FAILED");
        }
        
        return response;
    }
    
    /**
     * Generate payment URL for redirect-based payments
     */
    public String generatePaymentUrl(String paymentId, BigDecimal amount, String currency, String returnUrl) {
        return BASE_PAYMENT_URL + "/" + paymentId + 
               "?amount=" + amount + 
               "&currency=" + currency + 
               "&return_url=" + returnUrl;
    }
    
    // Private helper methods
    
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + 
               String.format("%06d", random.nextInt(1000000));
    }
    
    private String generateRefundId() {
        return "REF_" + System.currentTimeMillis() + "_" + 
               String.format("%06d", random.nextInt(1000000));
    }
    
    private boolean simulateSuccess() {
        return random.nextInt(100) < Integer.parseInt(SUCCESS_RATE);
    }
    
    private String generateFailureReason() {
        String[] reasons = {
            "Insufficient funds",
            "Card declined",
            "Invalid card details",
            "Expired card",
            "Network timeout",
            "Gateway unavailable",
            "Fraud detection triggered"
        };
        return reasons[random.nextInt(reasons.length)];
    }
    
    private String generateErrorCode() {
        String[] errorCodes = {
            "INSUFFICIENT_FUNDS",
            "CARD_DECLINED",
            "INVALID_CARD",
            "EXPIRED_CARD",
            "NETWORK_ERROR",
            "GATEWAY_ERROR",
            "FRAUD_DETECTED"
        };
        return errorCodes[random.nextInt(errorCodes.length)];
    }
    
    private String generateRawResponse(PaymentRequestDto request, String transactionId, boolean isSuccess) {
        Map<String, Object> response = new HashMap<>();
        response.put("transaction_id", transactionId);
        response.put("amount", request.getAmount());
        response.put("currency", request.getCurrency());
        response.put("status", isSuccess ? "success" : "failed");
        response.put("processed_at", LocalDateTime.now().toString());
        response.put("payment_method", request.getPaymentMethod().toString());
        
        if (!isSuccess) {
            response.put("error_code", generateErrorCode());
            response.put("error_message", generateFailureReason());
        }
        
        return response.toString();
    }
    
    private String generateRefundResponse(String originalTransactionId, String refundId, 
                                        BigDecimal amount, String reason) {
        Map<String, Object> response = new HashMap<>();
        response.put("refund_id", refundId);
        response.put("original_transaction_id", originalTransactionId);
        response.put("refund_amount", amount);
        response.put("reason", reason);
        response.put("status", "success");
        response.put("processed_at", LocalDateTime.now().toString());
        
        return response.toString();
    }
    
    // Inner class for gateway response
    public static class MockGatewayResponse {
        private String transactionId;
        private String gatewayName;
        private String status;
        private String message;
        private String errorCode;
        private String paymentUrl;
        private String rawResponse;
        private LocalDateTime processedAt;
        
        // Getters and Setters
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getGatewayName() { return gatewayName; }
        public void setGatewayName(String gatewayName) { this.gatewayName = gatewayName; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        
        public String getPaymentUrl() { return paymentUrl; }
        public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
        
        public String getRawResponse() { return rawResponse; }
        public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }
        
        public LocalDateTime getProcessedAt() { return processedAt; }
        public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
        
        public boolean isSuccess() {
            return "success".equals(status);
        }
        
        public boolean isFailed() {
            return "failed".equals(status);
        }
    }
}
