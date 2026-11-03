package Project.CinnamonProducts.model.cart;

import Project.CinnamonProducts.dto.cart.CartItemDto;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {

    private final Map<Long, CartLine> productIdToLine = new LinkedHashMap<>();

    public Collection<CartLine> getLines() {
        return productIdToLine.values();
    }

    public void addOrUpdateItem(CartItemDto dto) {
        CartLine line = productIdToLine.get(dto.getProductId());
        if (line == null) {
            line = new CartLine(dto.getProductId(), dto.getName(), dto.getUnitPrice(), dto.getQuantity());
            productIdToLine.put(dto.getProductId(), line);
        } else {
            line.setName(dto.getName());
            line.setUnitPrice(dto.getUnitPrice());
            line.setQuantity(dto.getQuantity());
        }
    }

    public void removeItem(long productId) {
        productIdToLine.remove(productId);
    }

    public void clear() {
        productIdToLine.clear();
    }

    public BigDecimal getSubtotal() {
        return productIdToLine.values().stream()
                .map(CartLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalQuantity() {
        return productIdToLine.values().stream()
                .mapToInt(CartLine::getQuantity)
                .sum();
    }

    public static class CartLine {
        private final long productId;
        private String name;
        private BigDecimal unitPrice;
        private int quantity;

        public CartLine(long productId, String name, BigDecimal unitPrice, int quantity) {
            this.productId = productId;
            this.name = name;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        public long getProductId() { return productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getLineTotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
