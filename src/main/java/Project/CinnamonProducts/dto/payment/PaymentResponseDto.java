package Project.CinnamonProducts.dto.payment;

import Project.CinnamonProducts.entity.payment.Payment;
import Project.CinnamonProducts.entity.payment.PaymentMethod;
import Project.CinnamonProducts.entity.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDto {
    
    private String paymentId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String customerEmail;
    private String customerName;
    private String description;
    private String gatewayTransactionId;
    private String gatewayResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private LocalDateTime failedAt;
    private LocalDateTime expiresAt;
    
    // Payment gateway specific fields
    private String paymentUrl;
    private String redirectUrl;
    private boolean requiresRedirect;
    
    // Error information
    private String errorCode;
    private String errorMessage;
    
    // Constructors
    public PaymentResponseDto() {}
    
    public PaymentResponseDto(Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.amount = payment.getAmount();
        this.currency = payment.getCurrency();
        this.status = payment.getStatus();
        this.paymentMethod = payment.getPaymentMethod();
        this.customerEmail = payment.getCustomerEmail();
        this.customerName = payment.getCustomerName();
        this.description = payment.getDescription();
        this.gatewayTransactionId = payment.getGatewayTransactionId();
        this.gatewayResponse = payment.getGatewayResponse();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
        this.paidAt = payment.getPaidAt();
        this.failedAt = payment.getFailedAt();
        this.expiresAt = payment.getExpiresAt();
    }
    
    public static PaymentResponseDto success(Payment payment) {
        PaymentResponseDto response = new PaymentResponseDto(payment);
        response.setStatus(PaymentStatus.COMPLETED);
        return response;
    }
    
    public static PaymentResponseDto error(String paymentId, String errorCode, String errorMessage) {
        PaymentResponseDto response = new PaymentResponseDto();
        response.setPaymentId(paymentId);
        response.setStatus(PaymentStatus.FAILED);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }
    
    public static PaymentResponseDto pending(Payment payment, String paymentUrl) {
        PaymentResponseDto response = new PaymentResponseDto(payment);
        response.setPaymentUrl(paymentUrl);
        response.setRequiresRedirect(true);
        return response;
    }
    
    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String gatewayTransactionId) { this.gatewayTransactionId = gatewayTransactionId; }
    
    public String getGatewayResponse() { return gatewayResponse; }
    public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    
    public LocalDateTime getFailedAt() { return failedAt; }
    public void setFailedAt(LocalDateTime failedAt) { this.failedAt = failedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    
    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
    
    public boolean isRequiresRedirect() { return requiresRedirect; }
    public void setRequiresRedirect(boolean requiresRedirect) { this.requiresRedirect = requiresRedirect; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
