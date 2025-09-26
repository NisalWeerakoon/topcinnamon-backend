package Project.CinnamonProducts.controller.payment;

import Project.CinnamonProducts.dto.payment.CheckoutRequestDto;
import Project.CinnamonProducts.dto.payment.CheckoutResponseDto;
import Project.CinnamonProducts.dto.payment.PaymentRequestDto;
import Project.CinnamonProducts.dto.payment.PaymentResponseDto;
import Project.CinnamonProducts.entity.payment.PaymentMethod;
import Project.CinnamonProducts.service.payment.CheckoutService;
import Project.CinnamonProducts.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class PaymentTestController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private CheckoutService checkoutService;
    
    /**
     * Test payment processing
     */
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> testPayment(@RequestParam(defaultValue = "100.00") String amount,
                                                         @RequestParam(defaultValue = "test@example.com") String email,
                                                         @RequestParam(defaultValue = "CREDIT_CARD") String method) {
        try {
            PaymentRequestDto request = new PaymentRequestDto();
            request.setAmount(new BigDecimal(amount));
            request.setCurrency("USD");
            request.setPaymentMethod(PaymentMethod.valueOf(method));
            request.setCustomerEmail(email);
            request.setCustomerName("Test Customer");
            request.setDescription("Test Payment");
            
            // Add mock card details for testing
            if (request.requiresCardDetails()) {
                request.setCardNumber("4111111111111111");
                request.setCardHolderName("Test User");
                request.setExpiryMonth("12");
                request.setExpiryYear("2025");
                request.setCvv("123");
            }
            
            PaymentResponseDto response = paymentService.processPayment(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            PaymentResponseDto error = PaymentResponseDto.error("TEST_ERROR", "TEST_ERROR", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Test checkout flow
     */
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDto> testCheckout(@RequestParam(defaultValue = "test@example.com") String email,
                                                           @RequestParam(defaultValue = "CREDIT_CARD") String method) {
        try {
            CheckoutRequestDto request = new CheckoutRequestDto();
            request.setCustomerEmail(email);
            request.setCustomerName("Test Customer");
            request.setPaymentMethod(PaymentMethod.valueOf(method));
            
            // Add mock card details for testing
            if (request.requiresCardDetails()) {
                request.setCardNumber("4111111111111111");
                request.setCardHolderName("Test User");
                request.setExpiryMonth("12");
                request.setExpiryYear("2025");
                request.setCvv("123");
            }
            
            CheckoutResponseDto response = checkoutService.processCheckout(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            CheckoutResponseDto error = CheckoutResponseDto.error("TEST_ERROR", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get payment statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            PaymentService.PaymentStatsDto stats = paymentService.getPaymentStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalPayments", stats.getTotalPayments());
            response.put("completedPayments", stats.getCompletedPayments());
            response.put("pendingPayments", stats.getPendingPayments());
            response.put("failedPayments", stats.getFailedPayments());
            response.put("totalRevenue", stats.getTotalRevenue());
            response.put("successRate", stats.getSuccessRate());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Payment Gateway");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
