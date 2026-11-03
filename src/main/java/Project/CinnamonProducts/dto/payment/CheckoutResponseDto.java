package Project.CinnamonProducts.dto.payment;

import Project.CinnamonProducts.dto.cart.CartItemDto;
import Project.CinnamonProducts.entity.payment.PaymentMethod;
import Project.CinnamonProducts.entity.payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CheckoutResponseDto {
    
    private String paymentId;
    private BigDecimal totalAmount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String customerEmail;
    private String customerName;
    private String description;
    private LocalDateTime createdAt;
    
    // Payment gateway specific fields
    private String paymentUrl;
    private String redirectUrl;
    private boolean requiresRedirect;
    
    // Cart information
    private List<CartItemDto> cartItems;
    private int totalQuantity;
    
    // Error information
    private String errorCode;
    private String errorMessage;
    
    // Constructors
    public CheckoutResponseDto() {}
    
    public CheckoutResponseDto(PaymentResponseDto paymentResponse) {
        this.paymentId = paymentResponse.getPaymentId();
        this.totalAmount = paymentResponse.getAmount();
        this.currency = paymentResponse.getCurrency();
        this.status = paymentResponse.getStatus();
        this.paymentMethod = paymentResponse.getPaymentMethod();
        this.customerEmail = paymentResponse.getCustomerEmail();
        this.customerName = paymentResponse.getCustomerName();
        this.description = paymentResponse.getDescription();
        this.createdAt = paymentResponse.getCreatedAt();
        this.paymentUrl = paymentResponse.getPaymentUrl();
        this.redirectUrl = paymentResponse.getRedirectUrl();
        this.requiresRedirect = paymentResponse.isRequiresRedirect();
        this.errorCode = paymentResponse.getErrorCode();
        this.errorMessage = paymentResponse.getErrorMessage();
    }
    
    public static CheckoutResponseDto success(PaymentResponseDto paymentResponse, List<CartItemDto> cartItems, int totalQuantity) {
        CheckoutResponseDto response = new CheckoutResponseDto(paymentResponse);
        response.setCartItems(cartItems);
        response.setTotalQuantity(totalQuantity);
        return response;
    }
    
    public static CheckoutResponseDto error(String errorCode, String errorMessage) {
        CheckoutResponseDto response = new CheckoutResponseDto();
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }
    
    public static CheckoutResponseDto pending(PaymentResponseDto paymentResponse, List<CartItemDto> cartItems, int totalQuantity) {
        CheckoutResponseDto response = new CheckoutResponseDto(paymentResponse);
        response.setCartItems(cartItems);
        response.setTotalQuantity(totalQuantity);
        response.setRequiresRedirect(true);
        return response;
    }
    
    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
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
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    
    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
    
    public boolean isRequiresRedirect() { return requiresRedirect; }
    public void setRequiresRedirect(boolean requiresRedirect) { this.requiresRedirect = requiresRedirect; }
    
    public List<CartItemDto> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItemDto> cartItems) { this.cartItems = cartItems; }
    
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
