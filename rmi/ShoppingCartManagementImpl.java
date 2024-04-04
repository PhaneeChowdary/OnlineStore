package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class ShoppingCartManagementImpl extends UnicastRemoteObject implements ShoppingCartManagement {
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> userCarts = new ConcurrentHashMap<>();

    public ShoppingCartManagementImpl() throws RemoteException {
        super();
    }

    @Override
    public String addToCart(String userId, String productId, int quantity) throws RemoteException {
        userCarts.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).merge(productId, quantity, Integer::sum);
        return "Product " + productId + " added to cart. Quantity: " + quantity;
    }

    @Override
    public String removeFromCart(String userId, String productId) throws RemoteException {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart != null && cart.remove(productId) != null) {
            return "Product " + productId + " removed from cart.";
        } else {
            return "Product not found in cart.";
        }
    }

    @Override
    public String updateCartItem(String userId, String productId, int quantity) throws RemoteException {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart != null && cart.containsKey(productId)) {
            cart.put(productId, quantity);
            return "Cart updated. Product " + productId + " quantity: " + quantity;
        } else {
            return "Product not found in cart.";
        }
    }

    @Override
    public String viewCart(String userId) throws RemoteException {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart == null || cart.isEmpty()) {
            return "Your cart is empty.";
        } else {
            StringBuilder cartView = new StringBuilder("Your cart contains:\n");
            cart.forEach((productId, quantity) -> cartView.append("Product ID: ").append(productId).append(", Quantity: ").append(quantity).append("\n"));
            return cartView.toString();
        }
    }

    @Override
    public String checkout(String userId) throws RemoteException {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart == null || cart.isEmpty()) {
            return "Your cart is empty.";
        } else {
            // Here, implement your checkout logic, like inventory check, payment, etc.
            // For simplicity, let's just clear the cart
            cart.clear();
            return "Checkout successful. Your cart is now empty.";
        }
    }
}
