package rmi.server;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DigLibSessionRI extends Remote {

    //Book [] Search(String title, String author) throws RemoteException; //TODO Search de Salas

    void logout() throws RemoteException;

}
