package rmi;

import model.Product;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public class AdminManagementImpl extends UnicastRemoteObject implements AdminManagement {
    // Reference the same user from UserManagementImpl
    private static ConcurrentHashMap<String, String> users = UserManagementImpl.users;
    private ConcurrentHashMap<String, String> admins = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> adminSessions = new ConcurrentHashMap<>();
    // Reference the static products map from ProductManagementImpl
    private ConcurrentHashMap<String, Product> products = ProductManagementImpl.products;


    public AdminManagementImpl() throws RemoteException {
        super();
    }

    @Override
    public String registerAdmin(String username, String password) throws RemoteException {
        if (admins.containsKey(username)) {
            return "\nAdmin username already exists.";
        }
        admins.put(username, password);
        return "\nAdmin registered successfully.";
    }

    @Override
    public String loginAdmin(String username, String password) throws RemoteException {
        String storedPassword = admins.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            String adminId = "Admin-" + username.hashCode();
            adminSessions.put(adminId, username); // Store the session
            return adminId;
        }
        return null;
    }

    @Override
    public String addProduct(String adminId, Product product) throws RemoteException {
        if (!adminSessions.containsKey(adminId)) {
            return "\nUnauthorized: Invalid admin session.";
        }
        products.put(product.getProductId(), product);
        return "\nProduct added successfully: " + product.getName();
    }

    @Override
    public String removeProduct(String adminId, String productId) throws RemoteException {
        if (!adminSessions.containsKey(adminId)) {
            return "\nUnauthorized: Invalid admin session.";
        }
        Product removedProduct = products.remove(productId);
        if (removedProduct != null) {
            return "\nProduct removed successfully: " + productId;
        } else {
            return "\nProduct not found: " + productId;
        }
    }

    @Override
    public String updateProduct(String adminId, String productId, String newName, String newDescription, Double newPrice, Integer newQuantity) throws RemoteException {
        if (!adminSessions.containsKey(adminId)) {
            return "\nUnauthorized: Invalid admin session.";
        }

        Product product = products.get(productId);
        if (product == null) {
            return "\nProduct not found: " + productId;
        }

        // Update product details if new values are provided
        if (newName != null && !newName.isEmpty()) product.setName(newName);
        if (newDescription != null && !newDescription.isEmpty()) product.setDescription(newDescription);
        if (newPrice != null && newPrice >= 0) product.setPrice(newPrice);
        if (newQuantity != null && newQuantity >= 0) product.setQuantityAvailable(newQuantity);

        products.put(productId, product);
        return "\nProduct updated successfully: " + productId;
    }

    @Override
    public String addUser(String adminId, String username, String password) throws RemoteException {
        if (!adminSessions.containsKey(adminId)) {
            return "\nUnauthorized: Invalid admin session.";
        }
        if (users.containsKey(username)) {
            return "\nUsername already exists.";
        }
        users.put(username, password);
        return "\nUser added successfully: " + username;
    }

    @Override
    public String removeUser(String adminId, String username) throws RemoteException {
        if (!adminSessions.containsKey(adminId)) {
            return "\nUnauthorized: Invalid admin session.";
        }
        if (users.remove(username) != null) {
            return "\nUser removed successfully: " + username;
        } else {
            return "\nUser not found: " + username;
        }
    }

}
