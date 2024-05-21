package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.Game;
import edu.ufp.inf.sd.rmi.Proj.client.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DigLibSessionRI extends Remote {

    //Book [] Search(String title, String author) throws RemoteException; //TODO Search de Salas

    void logout() throws RemoteException;

    boolean criarSala(int numPlayer) throws RemoteException;

    boolean joinSala(int id) throws RemoteException, InterruptedException;

    ArrayList<Game> listSalas() throws RemoteException;

    Game select(int id)  throws RemoteException;

    public User serachUser(String name, String pwd) throws RemoteException;

    User getUser() throws RemoteException;

    void setUser(User user) throws RemoteException;
}
