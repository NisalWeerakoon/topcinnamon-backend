package Project.CinnamonProducts.service;

import Project.CinnamonProducts.entity.CartEntity;
import Project.CinnamonProducts.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartDatabaseService {
    
    @Autowired
    private CartRepository cartRepository;
    
    public List<CartEntity> getUserCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail);
    }
    
    public List<CartEntity> getAllCarts() {
        return cartRepository.findAll();
    }
    
    public CartEntity addToCart(String userEmail, Long productId, String productName, 
                               String imageFilename, BigDecimal price, Integer quantity, String orderType) {
        Optional<CartEntity> existing = cartRepository.findByUserEmailAndProductIdAndOrderType(
            userEmail, productId, orderType
        );
        
        if (existing.isPresent()) {
            CartEntity item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartRepository.save(item);
        } else {
            CartEntity newItem = new CartEntity();
            newItem.setUserEmail(userEmail);
            newItem.setProductId(productId);
            newItem.setProductName(productName);
            newItem.setImageFilename(imageFilename);
            newItem.setUnitPrice(price);
            newItem.setQuantity(quantity);
            newItem.setOrderType(orderType);
            return cartRepository.save(newItem);
        }
    }
    
    public CartEntity updateQuantity(String userEmail, Long productId, String orderType, Integer newQuantity) {
        Optional<CartEntity> existing = cartRepository.findByUserEmailAndProductIdAndOrderType(
            userEmail, productId, orderType
        );
        
        if (existing.isPresent()) {
            CartEntity item = existing.get();
            item.setQuantity(newQuantity);
            return cartRepository.save(item);
        }
        return null;
    }
    
    public void removeFromCart(String userEmail, Long productId, String orderType) {
        cartRepository.deleteByUserEmailAndProductIdAndOrderType(userEmail, productId, orderType);
    }
    
    public void clearCart(String userEmail) {
        cartRepository.deleteByUserEmail(userEmail);
    }
    
    public long getUserCartCount(String userEmail) {
        return cartRepository.countByUserEmail(userEmail);
    }
    
    public long getTotalCartCount() {
        return cartRepository.count();
    }
}

