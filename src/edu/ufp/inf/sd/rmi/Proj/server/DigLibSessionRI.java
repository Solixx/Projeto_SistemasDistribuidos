package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.Game;
import edu.ufp.inf.sd.rmi.Proj.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DigLibSessionRI extends Remote {

    void logout() throws RemoteException;

    ArrayList<Sala> listSalas() throws RemoteException;

    User getUser() throws RemoteException;

    void setUser(User user) throws RemoteException;

    DigLibFactoryimpl getDigLibFactoryimpl() throws RemoteException;

    void createSala(int maxPlayers, ObserverRI observerRI) throws RemoteException, InterruptedException;

    void joinSala(int id, ObserverRI observerRI) throws RemoteException, InterruptedException;
}
