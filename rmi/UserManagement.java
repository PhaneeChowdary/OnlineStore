package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserManagement extends Remote {
    String registerUser(String username, String password) throws RemoteException;
    String loginUser(String username, String password) throws RemoteException;
}
