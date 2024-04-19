package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontController extends Remote {
    Object processRequest(String requestType, Object... parameters) throws RemoteException;
}
