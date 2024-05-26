package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.Client;
import edu.ufp.inf.sd.rmi.Proj.client.ObserverRI;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class SubjectImpl extends UnicastRemoteObject implements SubjectRI, Serializable {

    private State subjectState;
    private final ArrayList<ObserverRI> observers = new ArrayList<>();
    private DBMockup dbMockup;
    private ArrayList<Client> clients = new ArrayList<>();;

    protected SubjectImpl() throws RemoteException {
        super();
    }

    protected SubjectImpl(State subjectState) throws RemoteException {
        super();
        this.subjectState = subjectState;
    }

    @Override
    public void attach(ObserverRI obsRI) throws RemoteException {
        this.observers.add(obsRI);
    }

    @Override
    public void detach(ObserverRI obsRI) throws RemoteException {
        this.observers.remove(obsRI);
    }

    @Override
    public State getState() throws RemoteException {
        return this.subjectState;
    }

    @Override
    public void setState(State state) throws RemoteException, InterruptedException {
        this.subjectState = state;
        notifyAllObservers();
    }

    @Override
    public ArrayList<ObserverRI> getObservers() throws RemoteException {
        return this.observers;
    }

    @Override
    public DBMockup getDbMockup() throws RemoteException {
        return dbMockup;
    }

    @Override
    public void setDbMockup(DBMockup dbMockup) throws RemoteException {
        this.dbMockup = dbMockup;
    }

    @Override
    public ArrayList<Client> getClients() throws RemoteException {
        return this.clients;
    }

    @Override
    public void setClients(ArrayList<Client> clients) throws RemoteException {
        this.clients = clients;
    }

    @Override
    public void attachClient(Client client) throws RemoteException {
        this.clients.add(client);
    }

    @Override
    public void detachClient(Client client) throws RemoteException {
        this.clients.remove(client);
    }

    public void notifyAllObservers() throws RemoteException, InterruptedException {
        for (ObserverRI obs: this.observers) {
            obs.update();
        }
    }


}
