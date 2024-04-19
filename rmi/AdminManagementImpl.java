package rmi;

import model.Product;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public class AdminManagementImpl extends UnicastRemoteObject implements AdminManagement {
    // Reference the same user from rmi.UserManagementImpl
    private static ConcurrentHashMap<String, String> users = UserManagementImpl.users;
    private ConcurrentHashMap<String, String> admins = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> adminSessions = new ConcurrentHashMap<>();

    // Reference the static products map from rmi.ProductManagementImpl
    private ConcurrentHashMap<String, Product> products = ProductManagementImpl.products;

    public AdminManagementImpl() throws RemoteException {
        super();
        admins.put("phanee", "PhaneeIU");
    }

    @Override
    public boolean isAdmin(String sessionId) throws RemoteException {
        return sessionId != null && sessionId.startsWith("Admin-") && adminSessions.containsKey(sessionId);
    }

    @Override
    public String registerAdmin(String adminId, String newAdminUsername, String newAdminPassword) throws RemoteException {
        if (!adminSessions.containsKey(adminId)) {
            return "\nUnauthorized: Only logged-in admins can add new admins.";
        }
        if (admins.containsKey(newAdminUsername)) {
            return "\nUsername `" + newAdminUsername + "` already exists as admin.";
        }
        admins.put(newAdminUsername, newAdminPassword);
        return "\n`"+ newAdminUsername +"` profile registered successfully: ";
    }

    @Override
    public String loginAdmin(String username, String password) throws RemoteException {
        if (!admins.containsKey(username)) {
            return null;
        }
        String storedPassword = admins.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            String adminId = "Admin-" + username.hashCode();
            adminSessions.put(adminId, username); // Store the session
            return adminId;
        }
        return null;
    }

    @Override
    public String addUser(String adminId, String username, String password) throws RemoteException {
        if (!adminSessions.containsKey(adminId)) {
            return "\nUnauthorized: Invalid admin session.";
        }
        if (users.containsKey(username)) {
            return "\nUsername `"+ username +"` already exists.";
        }
        users.put(username, password);
        return "\n`" + username + "` profile registered successfully.";
    }

    @Override
    public String removeUser(String adminId, String username) throws RemoteException {
        if (!adminSessions.containsKey(adminId)) {
            return "\nUnauthorized: Invalid admin session.";
        }
        if (users.remove(username) != null) {
            return "\n`"+ username + "` profile removed successfully.";
        } else {
            return "\nUser `"+ username + "` not found.";
        }
    }

    @Override
    public int usersCount() throws RemoteException{
        return users.size();
    }
}