package Project.CinnamonProducts.entity.payment;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    BANK_TRANSFER("Bank Transfer"),
    DIGITAL_WALLET("Digital Wallet"),
    CASH_ON_DELIVERY("Cash on Delivery"),
    PAYPAL("PayPal"),
    STRIPE("Stripe"),
    MOCK_GATEWAY("Mock Gateway");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean requiresCardDetails() {
        return this == CREDIT_CARD || this == DEBIT_CARD;
    }
    
    public boolean isOnline() {
        return this != CASH_ON_DELIVERY;
    }
}
