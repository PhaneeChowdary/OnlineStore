package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args)  {
        try {
            ProductManagement productManagement = new ProductManagementImpl();
            UserManagement userManagement = new UserManagementImpl();
            ShoppingCartManagement shoppingCartManagement = new ShoppingCartManagementImpl();
            AdminManagement adminManagement = new AdminManagementImpl();
            FrontController frontController = new FrontControllerImpl(productManagement, userManagement, shoppingCartManagement, adminManagement);

            Registry registry = LocateRegistry.createRegistry(2025);
            registry.bind("rmi.FrontController", frontController);
            System.out.println("rmi.FrontController bound in registry");

            System.out.println("Remote server is ready...");
        } catch (Exception e) {
            System.err.println("rmi.Server exception: " + e);
            e.printStackTrace();
        }
    }
}
