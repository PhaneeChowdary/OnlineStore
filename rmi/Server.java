package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            ProductManagement productManagement = new ProductManagementImpl();
            UserManagement userManagement = new UserManagementImpl();
            ShoppingCartManagement shoppingCartManagement = new ShoppingCartManagementImpl();
            AdminManagement adminManagement = new AdminManagementImpl();

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("ProductManagement", productManagement);
            registry.bind("UserManagement", userManagement);
            registry.bind("ShoppingCartManagement", shoppingCartManagement);
            registry.bind("AdminManagement", adminManagement);

            System.out.println("Server is ready...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
