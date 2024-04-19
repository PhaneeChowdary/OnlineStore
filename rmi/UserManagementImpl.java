package rmi;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public class UserManagementImpl extends UnicastRemoteObject implements UserManagement {
    static ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> userSessions = new ConcurrentHashMap<>();

    public UserManagementImpl() throws RemoteException {
        super();
    }

    @Override
    public String registerUser(String username, String password) throws RemoteException {
        if (users.containsKey(username)) {
            return "\n`" + username + "` already exists.";
        }
        users.put(username, password);
        return "\n`" + username + "` profile registered successfully.";
    }

    @Override
    public String loginUser(String username, String password) throws RemoteException {
        String storedPassword = users.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            String userId = "User-" + username.hashCode();  //Unique session identifier
            userSessions.put(userId, username); // Store the session
            return userId;
        }
        return null;
    }
}
