package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SubjectRI extends Remote {

    public void attach(ObserverRI obsRI) throws RemoteException;

    public void detach(ObserverRI obsRI) throws RemoteException;

    State getState() throws RemoteException;

    void setState(State state) throws RemoteException;
}
