package Project.CinnamonProducts.controller.payment;

import Project.CinnamonProducts.dto.payment.PaymentRequestDto;
import Project.CinnamonProducts.dto.payment.PaymentResponseDto;
import Project.CinnamonProducts.entity.payment.PaymentMethod;
import Project.CinnamonProducts.entity.payment.PaymentStatus;
import Project.CinnamonProducts.service.payment.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*") // Allow all origins for development
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * Create a new payment
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto request) {
        PaymentResponseDto response = paymentService.createPayment(request);
        
        if (response.getStatus() == PaymentStatus.PENDING) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Process payment immediately
     */
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto request) {
        PaymentResponseDto response = paymentService.processPayment(request);
        
        if (response.getStatus() == PaymentStatus.COMPLETED) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPayment(@PathVariable String paymentId) {
        PaymentResponseDto response = paymentService.getPayment(paymentId);
        
        if ("NOT_FOUND".equals(response.getErrorCode())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Verify payment status
     */
    @GetMapping("/{paymentId}/verify")
    public ResponseEntity<PaymentResponseDto> verifyPayment(@PathVariable String paymentId) {
        PaymentResponseDto response = paymentService.verifyPayment(paymentId);
        
        if ("NOT_FOUND".equals(response.getErrorCode())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get payments by customer email
     */
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByCustomer(@PathVariable String email) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByCustomer(email);
        return ResponseEntity.ok(payments);
    }
    
    /**
     * Refund payment
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponseDto> refundPayment(
            @PathVariable String paymentId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reason) {
        
        PaymentResponseDto response = paymentService.refundPayment(paymentId, amount, reason);
        
        if ("NOT_FOUND".equals(response.getErrorCode())) {
            return ResponseEntity.notFound().build();
        } else if (response.getErrorCode() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cancel payment
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponseDto> cancelPayment(@PathVariable String paymentId) {
        PaymentResponseDto response = paymentService.cancelPayment(paymentId);
        
        if ("NOT_FOUND".equals(response.getErrorCode())) {
            return ResponseEntity.notFound().build();
        } else if (response.getErrorCode() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get payment statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<PaymentService.PaymentStatsDto> getPaymentStats() {
        PaymentService.PaymentStatsDto stats = paymentService.getPaymentStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Mock payment gateway callback endpoint
     */
    @GetMapping("/mock-pay/{transactionId}")
    public ResponseEntity<String> mockPaymentCallback(@PathVariable String transactionId) {
        // This endpoint simulates the payment gateway callback
        // In a real scenario, this would be called by the payment gateway
        return ResponseEntity.ok("Payment processed: " + transactionId);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment gateway is running");
    }
    
    /**
     * Get available payment methods
     */
    @GetMapping("/methods")
    public ResponseEntity<PaymentMethod[]> getPaymentMethods() {
        return ResponseEntity.ok(PaymentMethod.values());
    }
    
    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<PaymentResponseDto> handleValidationException(IllegalArgumentException ex) {
        PaymentResponseDto error = PaymentResponseDto.error("VALIDATION_ERROR", "VALIDATION_ERROR", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Exception handler for general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<PaymentResponseDto> handleGeneralException(Exception ex) {
        PaymentResponseDto error = PaymentResponseDto.error("INTERNAL_ERROR", "INTERNAL_ERROR", 
            "An internal error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
