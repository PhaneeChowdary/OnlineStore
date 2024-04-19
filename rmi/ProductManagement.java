package rmi;

import model.Product;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ProductManagement extends Remote {
    String addProduct(Product product) throws RemoteException;
    StringBuilder updateProduct(String productId, Product updatedProduct) throws RemoteException;
    String removeProduct(String productId) throws RemoteException;
    List<Product> browseProducts() throws RemoteException;
    Product getProductDetails(String productId) throws RemoteException;
    int productsCount() throws RemoteException;
}