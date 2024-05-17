package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class SubjectImpl extends UnicastRemoteObject implements SubjectRI {

    private State subjectState;
    private final ArrayList<ObserverRI> observers = new ArrayList<>();

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
    public void setState(State state) throws RemoteException {
        this.subjectState = state;
        notifyAllObservers();
    }

    @Override
    public ArrayList<ObserverRI> getObservers() throws RemoteException {
        return this.observers;
    }

    public void notifyAllObservers() throws RemoteException {
        for (ObserverRI obs: this.observers) {
            obs.update();
        }
    }
}
