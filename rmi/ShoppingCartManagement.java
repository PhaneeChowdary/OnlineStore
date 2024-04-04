package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ShoppingCartManagement extends Remote {
    String addToCart(String userId, String productId, int quantity) throws RemoteException;
    String removeFromCart(String userId, String productId) throws RemoteException;
    String updateCartItem(String userId, String productId, int quantity) throws RemoteException;
    String viewCart(String userId) throws RemoteException;
    String checkout(String userId) throws RemoteException;
}
