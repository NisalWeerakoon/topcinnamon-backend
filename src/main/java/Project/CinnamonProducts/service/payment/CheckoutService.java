package Project.CinnamonProducts.service.payment;

import Project.CinnamonProducts.dto.cart.CartItemDto;
import Project.CinnamonProducts.dto.payment.CheckoutRequestDto;
import Project.CinnamonProducts.dto.payment.CheckoutResponseDto;
import Project.CinnamonProducts.dto.payment.PaymentRequestDto;
import Project.CinnamonProducts.dto.payment.PaymentResponseDto;
import Project.CinnamonProducts.entity.payment.PaymentStatus;
import Project.CinnamonProducts.model.cart.Cart;
import Project.CinnamonProducts.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CheckoutService {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * Process checkout with cart items
     */
    public CheckoutResponseDto processCheckout(CheckoutRequestDto checkoutRequest) {
        try {
            // Get current cart
            Cart cart = cartService.getCart();
            
            // Validate cart
            if (cart.getLines().isEmpty()) {
                return CheckoutResponseDto.error("EMPTY_CART", "Cart is empty");
            }
            
            if (cart.getSubtotal().compareTo(BigDecimal.ZERO) <= 0) {
                return CheckoutResponseDto.error("INVALID_AMOUNT", "Cart total must be greater than zero");
            }
            
            // Validate checkout request
            if (checkoutRequest.requiresCardDetails() && !checkoutRequest.isValidCardDetails()) {
                return CheckoutResponseDto.error("INVALID_CARD_DETAILS", "Card details are required for selected payment method");
            }
            
            // Create payment request from checkout request and cart
            PaymentRequestDto paymentRequest = createPaymentRequestFromCheckout(checkoutRequest, cart);
            
            // Process payment
            PaymentResponseDto paymentResponse = paymentService.processPayment(paymentRequest);
            
            // Convert cart items to DTOs
            List<CartItemDto> cartItemDtos = cart.getLines().stream()
                    .map(line -> {
                        CartItemDto dto = new CartItemDto();
                        dto.setProductId(line.getProductId());
                        dto.setName(line.getName());
                        dto.setUnitPrice(line.getUnitPrice());
                        dto.setQuantity(line.getQuantity());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            // Create checkout response
            if (paymentResponse.getStatus() == PaymentStatus.COMPLETED) {
                // Clear cart on successful payment
                cartService.clear();
                return CheckoutResponseDto.success(paymentResponse, cartItemDtos, cart.getTotalQuantity());
            } else {
                return CheckoutResponseDto.error(paymentResponse.getErrorCode(), paymentResponse.getErrorMessage());
            }
            
        } catch (Exception e) {
            return CheckoutResponseDto.error("CHECKOUT_ERROR", e.getMessage());
        }
    }
    
    /**
     * Create payment for redirect-based processing
     */
    public CheckoutResponseDto createCheckoutPayment(CheckoutRequestDto checkoutRequest) {
        try {
            // Get current cart
            Cart cart = cartService.getCart();
            
            // Validate cart
            if (cart.getLines().isEmpty()) {
                return CheckoutResponseDto.error("EMPTY_CART", "Cart is empty");
            }
            
            if (cart.getSubtotal().compareTo(BigDecimal.ZERO) <= 0) {
                return CheckoutResponseDto.error("INVALID_AMOUNT", "Cart total must be greater than zero");
            }
            
            // Create payment request from checkout request and cart
            PaymentRequestDto paymentRequest = createPaymentRequestFromCheckout(checkoutRequest, cart);
            
            // Create payment
            PaymentResponseDto paymentResponse = paymentService.createPayment(paymentRequest);
            
            // Convert cart items to DTOs
            List<CartItemDto> cartItemDtos = cart.getLines().stream()
                    .map(line -> {
                        CartItemDto dto = new CartItemDto();
                        dto.setProductId(line.getProductId());
                        dto.setName(line.getName());
                        dto.setUnitPrice(line.getUnitPrice());
                        dto.setQuantity(line.getQuantity());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            // Create checkout response
            if (paymentResponse.getStatus() == PaymentStatus.PENDING) {
                return CheckoutResponseDto.pending(paymentResponse, cartItemDtos, cart.getTotalQuantity());
            } else {
                return CheckoutResponseDto.error(paymentResponse.getErrorCode(), paymentResponse.getErrorMessage());
            }
            
        } catch (Exception e) {
            return CheckoutResponseDto.error("CHECKOUT_ERROR", e.getMessage());
        }
    }
    
    /**
     * Complete checkout after payment verification
     */
    public CheckoutResponseDto completeCheckout(String paymentId) {
        try {
            // Verify payment
            PaymentResponseDto paymentResponse = paymentService.verifyPayment(paymentId);
            
            if (paymentResponse.getStatus() == PaymentStatus.COMPLETED) {
                // Clear cart on successful payment
                cartService.clear();
                
                // Create empty cart items since cart is cleared
                return CheckoutResponseDto.success(paymentResponse, List.of(), 0);
            } else {
                return CheckoutResponseDto.error(paymentResponse.getErrorCode(), paymentResponse.getErrorMessage());
            }
            
        } catch (Exception e) {
            return CheckoutResponseDto.error("COMPLETION_ERROR", e.getMessage());
        }
    }
    
    /**
     * Get checkout summary without processing payment
     */
    public CheckoutResponseDto getCheckoutSummary(CheckoutRequestDto checkoutRequest) {
        try {
            // Get current cart
            Cart cart = cartService.getCart();
            
            // Validate cart
            if (cart.getLines().isEmpty()) {
                return CheckoutResponseDto.error("EMPTY_CART", "Cart is empty");
            }
            
            // Convert cart items to DTOs
            List<CartItemDto> cartItemDtos = cart.getLines().stream()
                    .map(line -> {
                        CartItemDto dto = new CartItemDto();
                        dto.setProductId(line.getProductId());
                        dto.setName(line.getName());
                        dto.setUnitPrice(line.getUnitPrice());
                        dto.setQuantity(line.getQuantity());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            // Create summary response
            CheckoutResponseDto response = new CheckoutResponseDto();
            response.setTotalAmount(cart.getSubtotal());
            response.setCurrency("USD");
            response.setPaymentMethod(checkoutRequest.getPaymentMethod());
            response.setCustomerEmail(checkoutRequest.getCustomerEmail());
            response.setCustomerName(checkoutRequest.getCustomerName());
            response.setCartItems(cartItemDtos);
            response.setTotalQuantity(cart.getTotalQuantity());
            response.setDescription("Checkout Summary");
            
            return response;
            
        } catch (Exception e) {
            return CheckoutResponseDto.error("SUMMARY_ERROR", e.getMessage());
        }
    }
    
    // Private helper methods
    
    private PaymentRequestDto createPaymentRequestFromCheckout(CheckoutRequestDto checkoutRequest, Cart cart) {
        PaymentRequestDto paymentRequest = new PaymentRequestDto();
        
        // Set payment details
        paymentRequest.setAmount(cart.getSubtotal());
        paymentRequest.setCurrency("USD");
        paymentRequest.setPaymentMethod(checkoutRequest.getPaymentMethod());
        
        // Set customer details
        paymentRequest.setCustomerEmail(checkoutRequest.getCustomerEmail());
        paymentRequest.setCustomerName(checkoutRequest.getCustomerName());
        paymentRequest.setCustomerPhone(checkoutRequest.getCustomerPhone());
        paymentRequest.setBillingAddress(checkoutRequest.getBillingAddress());
        
        // Set card details if required
        if (checkoutRequest.requiresCardDetails()) {
            paymentRequest.setCardNumber(checkoutRequest.getCardNumber());
            paymentRequest.setCardHolderName(checkoutRequest.getCardHolderName());
            paymentRequest.setExpiryMonth(checkoutRequest.getExpiryMonth());
            paymentRequest.setExpiryYear(checkoutRequest.getExpiryYear());
            paymentRequest.setCvv(checkoutRequest.getCvv());
        }
        
        // Set URLs
        paymentRequest.setReturnUrl(checkoutRequest.getReturnUrl());
        paymentRequest.setCancelUrl(checkoutRequest.getCancelUrl());
        
        // Set description
        paymentRequest.setDescription("Checkout for " + cart.getTotalQuantity() + " items");
        
        // Set metadata
        String metadata = String.format("{\"cart_items\":%d,\"total_quantity\":%d,\"notes\":\"%s\"}", 
                cart.getLines().size(), 
                cart.getTotalQuantity(),
                checkoutRequest.getNotes() != null ? checkoutRequest.getNotes() : "");
        paymentRequest.setMetadata(metadata);
        
        return paymentRequest;
    }
}
