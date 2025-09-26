package Project.CinnamonProducts.controller.cart;

import Project.CinnamonProducts.dto.cart.CartItemDto;
import Project.CinnamonProducts.model.cart.Cart;
import Project.CinnamonProducts.service.cart.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Cart getCart() {
        return cartService.getCart();
    }

    @PostMapping("/items")
    public Cart upsertItem(@Valid @RequestBody CartItemDto dto) {
        return cartService.addOrUpdateItem(dto);
    }

    @DeleteMapping("/items/{productId}")
    public Cart removeItem(@PathVariable long productId) {
        return cartService.removeItem(productId);
    }

    @DeleteMapping
    public ResponseEntity<Void> clear() {
        cartService.clear();
        return ResponseEntity.noContent().build();
    }
}
