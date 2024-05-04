import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DigLibSessionRI extends Remote {


    void logout() throws RemoteException;
}
