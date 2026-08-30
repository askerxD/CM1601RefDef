package com.example.cm1601refdef.service;

import com.example.cm1601refdef.objects.CartItem;
import com.example.cm1601refdef.objects.Part;
import java.util.ArrayList;

public class CartService {
    private ArrayList<CartItem> cart = new ArrayList<>();
    public boolean addToCart(Part part, int quantity) {
        if (quantity <= 0) return false;
        if (part == null) return false;

        int available = getAvailableQuantity(part);
        if (quantity > available) return false;

        for (CartItem item : cart) {
            if (item.getPart().getPartCode().equals(part.getPartCode())) {
                item.setQuantity(item.getQuantity() + quantity);
                return true;
            }
        }
        cart.add(new CartItem(part, quantity));
        return true;
    }

    public int getQuantityInCart(String partCode) {
        for (CartItem item : cart) {
            if (item.getPart().getPartCode().equals(partCode)) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public int getAvailableQuantity(Part part) {
        if (part == null) {
            return 0;
        }
        return part.getQuantity() - getQuantityInCart(part.getPartCode());
    }
    public void removeFromCart(String partCode) {
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getPart().getPartCode().equals(partCode)) {
                cart.remove(i);
                return;
            }
        }
    }

    public boolean removeQuantityFromCart(String partCode, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        for (int i = 0; i < cart.size(); i++) {
            CartItem item = cart.get(i);
            if (item.getPart().getPartCode().equals(partCode)) {
                if (quantity > item.getQuantity()) {
                    return false;
                }
                int newQty = item.getQuantity() - quantity;
                if (newQty == 0) {
                    cart.remove(i);
                } else {
                    item.setQuantity(newQty);
                }
                return true;
            }
        }
        return false;
    }
    public ArrayList<CartItem> getCartItems() {
        return cart;
    }
    public void clearCart() {
        cart.clear();
    }
    public double getSubtotalBeforeDiscounts() {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getPart().getPrice() * item.getQuantity();
        }
        return total;
    }

    public double getTotalAfterBulkDiscount() {
        double total = 0;
        for (CartItem item : cart) {
            double lineTotal = item.getPart().getPrice() * item.getQuantity();
            if (item.getQuantity() >= 3) {
                lineTotal *= 0.95;
            }
            total += lineTotal;
        }
        return total;
    }

    public double getBulkDiscountAmount() {
        return getSubtotalBeforeDiscounts() - getTotalAfterBulkDiscount();
    }

    public double getSynergyDiscountAmount() {
        return getTotalAfterBulkDiscount() - calculateTotal();
    }

    public double calculateTotal() {
        double total = getTotalAfterBulkDiscount();
        if (hasEngineAndElectrical()) {
            total *= 0.90;
        }
        return total;
    }

    private boolean hasEngineAndElectrical() {
        boolean hasEngine = false;
        boolean hasElectrical = false;
        for (CartItem item : cart) {
            String category = item.getPart().getCategory().toUpperCase();
            if (category.contains("ENGINE")) {
                hasEngine = true;
            }
            if (category.contains("ELECTRICAL")) {
                hasElectrical = true;
            }
        }
        return hasEngine && hasElectrical;
    }
    // Validate checkout
    public boolean canCheckout() {
        return !cart.isEmpty();
    }
}
