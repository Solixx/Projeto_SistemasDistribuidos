package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.DigLibFactoryimpl;
import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {

    void update() throws RemoteException, InterruptedException;

    public int getId() throws RemoteException;

    public State getLastObserverState() throws RemoteException;

    public void setLastObserverState(State lastObserverState) throws RemoteException;

    public SubjectRI getSubjectRI() throws RemoteException;

    public void setSubjectRI(SubjectRI subjectRI) throws RemoteException;

    public User getUser() throws RemoteException;

    public void setUser(User user) throws RemoteException;

    public Player findPlayer(int id)  throws RemoteException;

    Game createGame(int numPlayer, SubjectRI subject, DigLibFactoryimpl digLibFactoryimpl) throws RemoteException;

    boolean joinSala(int id, Game sala) throws RemoteException, InterruptedException;
}
