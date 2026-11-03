package Project.CinnamonProducts.dto.payment;

import Project.CinnamonProducts.entity.payment.PaymentMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequestDto {
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    private String customerName;
    private String customerPhone;
    private String billingAddress;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    // Card details (for card payments)
    private String cardNumber;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    
    // Gateway specific fields
    private String returnUrl;
    private String cancelUrl;
    
    // Additional information
    private String notes;
    
    // Constructors
    public CheckoutRequestDto() {}
    
    public CheckoutRequestDto(String customerEmail, PaymentMethod paymentMethod) {
        this.customerEmail = customerEmail;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters and Setters
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
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
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
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
