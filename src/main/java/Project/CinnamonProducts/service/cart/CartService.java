package Project.CinnamonProducts.service.cart;

import Project.CinnamonProducts.dto.cart.CartItemDto;
import Project.CinnamonProducts.model.cart.Cart;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private static final String SESSION_KEY = "CART_SESSION";

    private final HttpSession httpSession;

    public CartService(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public Cart getCart() {
        Cart cart = (Cart) httpSession.getAttribute(SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            httpSession.setAttribute(SESSION_KEY, cart);
        }
        return cart;
    }

    public Cart addOrUpdateItem(CartItemDto dto) {
        Cart cart = getCart();
        cart.addOrUpdateItem(dto);
        return cart;
    }

    public Cart removeItem(long productId) {
        Cart cart = getCart();
        cart.removeItem(productId);
        return cart;
    }

    public void clear() {
        getCart().clear();
    }
}
