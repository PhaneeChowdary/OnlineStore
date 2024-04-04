package rmi;

import model.Product;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminManagement extends Remote {
    String registerAdmin(String username, String password) throws RemoteException;
    String loginAdmin(String username, String password) throws RemoteException;

    String addProduct(String adminId, Product product) throws RemoteException;
    String removeProduct(String adminId, String productId) throws RemoteException;
    String updateProduct(String adminId, String productId, String newName, String newDescription, Double newPrice, Integer newQuantity) throws RemoteException;

    String addUser(String adminId, String username, String password) throws RemoteException;
    String removeUser(String adminId, String username) throws RemoteException;
}
