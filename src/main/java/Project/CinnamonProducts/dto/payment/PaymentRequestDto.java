package Project.CinnamonProducts.dto.payment;

import Project.CinnamonProducts.entity.payment.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentRequestDto {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    private String currency = "USD";
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    private String customerName;
    private String customerPhone;
    private String billingAddress;
    private String description;
    
    // Card details (for card payments)
    private String cardNumber;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    
    // Gateway specific fields
    private String returnUrl;
    private String cancelUrl;
    private String webhookUrl;
    
    // Additional metadata
    private String metadata;
    
    // Constructors
    public PaymentRequestDto() {}
    
    public PaymentRequestDto(BigDecimal amount, String currency, PaymentMethod paymentMethod, 
                           String customerEmail, String description) {
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.customerEmail = customerEmail;
        this.description = description;
    }
    
    // Getters and Setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    
    public String getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
    
    public String getExpiryYear() { return expiryYear; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    
    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
    
    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    // Validation methods
    public boolean requiresCardDetails() {
        return paymentMethod != null && paymentMethod.requiresCardDetails();
    }
    
    public boolean isValidCardDetails() {
        if (!requiresCardDetails()) {
            return true;
        }
        
        return cardNumber != null && !cardNumber.trim().isEmpty() &&
               cardHolderName != null && !cardHolderName.trim().isEmpty() &&
               expiryMonth != null && !expiryMonth.trim().isEmpty() &&
               expiryYear != null && !expiryYear.trim().isEmpty() &&
               cvv != null && !cvv.trim().isEmpty();
    }
}
