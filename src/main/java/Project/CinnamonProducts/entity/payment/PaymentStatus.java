package Project.CinnamonProducts.entity.payment;

public enum PaymentStatus {
    PENDING("Payment is pending"),
    PROCESSING("Payment is being processed"),
    COMPLETED("Payment completed successfully"),
    FAILED("Payment failed"),
    CANCELLED("Payment was cancelled"),
    REFUNDED("Payment was refunded"),
    PARTIALLY_REFUNDED("Payment was partially refunded");
    
    private final String description;
    
    PaymentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
    
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    
    public boolean isPending() {
        return this == PENDING || this == PROCESSING;
    }
}
