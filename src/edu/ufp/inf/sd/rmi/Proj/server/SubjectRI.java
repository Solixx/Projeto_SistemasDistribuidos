package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface SubjectRI extends Remote {

    public void attach(ObserverRI obsRI) throws RemoteException;

    public void detach(ObserverRI obsRI) throws RemoteException;

    State getState() throws RemoteException;

    void setState(State state) throws RemoteException, InterruptedException;

    ArrayList<ObserverRI> getObservers() throws RemoteException;

    DBMockup getDbMockup() throws RemoteException;

    void setDbMockup(DBMockup dbMockup) throws RemoteException;
}
