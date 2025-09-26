package Project.CinnamonProducts.controller.payment;

import Project.CinnamonProducts.dto.payment.CheckoutRequestDto;
import Project.CinnamonProducts.dto.payment.CheckoutResponseDto;
import Project.CinnamonProducts.service.payment.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*") // Allow all origins for development
public class CheckoutController {
    
    @Autowired
    private CheckoutService checkoutService;
    
    /**
     * Process checkout with cart items
     */
    @PostMapping("/process")
    public ResponseEntity<CheckoutResponseDto> processCheckout(@Valid @RequestBody CheckoutRequestDto request) {
        CheckoutResponseDto response = checkoutService.processCheckout(request);
        
        if (response.getErrorCode() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create checkout payment for redirect-based processing
     */
    @PostMapping("/create")
    public ResponseEntity<CheckoutResponseDto> createCheckoutPayment(@Valid @RequestBody CheckoutRequestDto request) {
        CheckoutResponseDto response = checkoutService.createCheckoutPayment(request);
        
        if (response.getErrorCode() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Complete checkout after payment verification
     */
    @PostMapping("/complete/{paymentId}")
    public ResponseEntity<CheckoutResponseDto> completeCheckout(@PathVariable String paymentId) {
        CheckoutResponseDto response = checkoutService.completeCheckout(paymentId);
        
        if (response.getErrorCode() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get checkout summary without processing payment
     */
    @PostMapping("/summary")
    public ResponseEntity<CheckoutResponseDto> getCheckoutSummary(@Valid @RequestBody CheckoutRequestDto request) {
        CheckoutResponseDto response = checkoutService.getCheckoutSummary(request);
        
        if (response.getErrorCode() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CheckoutResponseDto> handleValidationException(IllegalArgumentException ex) {
        CheckoutResponseDto error = CheckoutResponseDto.error("VALIDATION_ERROR", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Exception handler for general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CheckoutResponseDto> handleGeneralException(Exception ex) {
        CheckoutResponseDto error = CheckoutResponseDto.error("INTERNAL_ERROR", "An internal error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
