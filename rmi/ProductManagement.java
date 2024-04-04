package rmi;

import model.Product;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ProductManagement extends Remote {
    void addProduct(Product product) throws RemoteException;
    boolean updateProduct(String productId, Product updatedProduct) throws RemoteException;
    String removeProduct(String productId) throws RemoteException;
    List<Product> browseProducts() throws RemoteException;
    String purchaseProduct(String productId, int quantity) throws RemoteException;
    Product getProductDetails(String productId) throws RemoteException;
}