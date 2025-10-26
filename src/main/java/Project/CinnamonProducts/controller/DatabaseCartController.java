package Project.CinnamonProducts.controller;

import Project.CinnamonProducts.entity.CartEntity;
import Project.CinnamonProducts.service.CartDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart/database")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class DatabaseCartController {
    
    @Autowired
    private CartDatabaseService cartDatabaseService;
    
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<?> getUserCart(@PathVariable String userEmail) {
        List<CartEntity> cart = cartDatabaseService.getUserCart(userEmail);
        return ResponseEntity.ok().body(cart);
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllCarts() {
        List<CartEntity> allCarts = cartDatabaseService.getAllCarts();
        return ResponseEntity.ok().body(allCarts);
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request) {
        try {
            String userEmail = (String) request.get("userEmail");
            Long productId = Long.parseLong(request.get("productId").toString());
            String productName = (String) request.get("productName");
            String imageFilename = (String) request.get("imageFilename");
            BigDecimal price = new BigDecimal(request.get("price").toString());
            Integer quantity = Integer.parseInt(request.get("quantity").toString());
            String orderType = (String) request.get("orderType");
            
            CartEntity item = cartDatabaseService.addToCart(
                userEmail, productId, productName, imageFilename, price, quantity, orderType
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("item", item);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error adding to cart: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody Map<String, Object> request) {
        try {
            String userEmail = (String) request.get("userEmail");
            Long productId = Long.parseLong(request.get("productId").toString());
            String orderType = (String) request.get("orderType");
            Integer newQuantity = Integer.parseInt(request.get("quantity").toString());
            
            CartEntity item = cartDatabaseService.updateQuantity(userEmail, productId, orderType, newQuantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("item", item);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating cart: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam String userEmail, 
                                           @RequestParam Long productId,
                                           @RequestParam String orderType) {
        try {
            cartDatabaseService.removeFromCart(userEmail, productId, orderType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item removed from cart");
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error removing from cart: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/clear/{userEmail}")
    public ResponseEntity<?> clearCart(@PathVariable String userEmail) {
        try {
            cartDatabaseService.clearCart(userEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cart cleared");
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error clearing cart: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/count/{userEmail}")
    public ResponseEntity<?> getUserCartCount(@PathVariable String userEmail) {
        long count = cartDatabaseService.getUserCartCount(userEmail);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getTotalCartCount() {
        long count = cartDatabaseService.getTotalCartCount();
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok().body(response);
    }
}

