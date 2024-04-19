package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminManagement extends Remote {
    String registerAdmin(String adminID, String username, String password) throws RemoteException;
    String loginAdmin(String username, String password) throws RemoteException;
    String addUser(String adminId, String username, String password) throws RemoteException;
    String removeUser(String adminId, String username) throws RemoteException;
    boolean isAdmin(String sessionId) throws RemoteException;
    int usersCount() throws RemoteException;
}
