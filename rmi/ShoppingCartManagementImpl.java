package rmi;

import model.Product;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShoppingCartManagementImpl extends UnicastRemoteObject implements ShoppingCartManagement {
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> userCarts = new ConcurrentHashMap<>();

    public ShoppingCartManagementImpl() throws RemoteException {
        super();
    }

    @Override
    public String addToCart(String userId, String productId, int quantity) throws RemoteException {
        userCarts.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).merge(productId, quantity, Integer::sum);
        return "\nProduct " + productId + " added to cart. Quantity: " + quantity;
    }


    @Override
    public String removeFromCart(String userId, String productId) throws RemoteException {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart != null && cart.remove(productId) != null) {
            return "\nProduct " + productId + " removed from cart.";
        } else {
            return "\nProduct not found in cart.";
        }
    }

    @Override
    public String updateCartItem(String userId, String productId, int quantity) throws RemoteException {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart != null && cart.containsKey(productId)) {
            if (quantity <= 0) {
                cart.remove(productId);  // Remove item if quantity is zero or less
                return "\nItem removed from cart as the quantity set to " + quantity;
            } else {
                cart.put(productId, quantity);
                return "\nCart updated. Product " + productId + " quantity set to: " + quantity;
            }
        } else {
            return "\nProduct not found in cart.";
        }
    }

    @Override
    public String viewCart(String userId) throws RemoteException {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart == null || cart.isEmpty()) {
            return "\nYour cart is empty.";
        } else {
            StringBuilder cartView = new StringBuilder("\nYour cart contains:\n");
            cart.forEach((productId, quantity) -> cartView.append(ProductManagementImpl.products.get(productId).getName()).append(", Product ID: ").append(productId).append(", Quantity: ").append(quantity).append("\n"));
            return cartView.toString();
        }
    }

    @Override
    public String checkout(String userId) throws RemoteException {
        ConcurrentHashMap<String, Integer> cart = userCarts.get(userId);
        if (cart == null || cart.isEmpty()) {
            return "\nYour cart is empty.";
        } else {
            // Attempt to update inventory
            StringBuilder checkoutReport = new StringBuilder();
            boolean allItemsProcessed = true;

            for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                String productId = entry.getKey();
                int quantityToPurchase = entry.getValue();

                // Synchronize on the specific product to ensure thread safety
                Product product = ProductManagementImpl.products.get(productId);
                if (product != null) {
                    synchronized (product) {
                        int currentQuantity = product.getQuantityAvailable();
                        if (quantityToPurchase <= currentQuantity) {
                            product.setQuantityAvailable(currentQuantity - quantityToPurchase);
                            checkoutReport.append("\nItem checked out: ").append(product.getName())
                                    .append(", Quantity: ").append(quantityToPurchase);
                        } else {
                            allItemsProcessed = false;
                            checkoutReport.append("\nFailed to checkout ").append(product.getName())
                                    .append(", Requested: ").append(quantityToPurchase)
                                    .append(", Available: ").append(currentQuantity);
                        }
                    }
                } else {
                    allItemsProcessed = false;
                    checkoutReport.append("\nProduct not found: ").append(productId);
                }
            }

            if (allItemsProcessed) {
                cart.clear(); // Clear the cart only if all items are successfully processed
                return "\nCheckout successful. Your cart is now empty." + checkoutReport.toString();
            } else {
                return "\nCheckout incomplete. Some items could not be processed." + checkoutReport.toString();
            }
        }
    }
}
