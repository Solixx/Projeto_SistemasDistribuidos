package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;
import edu.ufp.inf.sd.rmi.Proj.server.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {

    void update() throws RemoteException;

    int getId() throws RemoteException;

    State getLastObserverState() throws RemoteException;

    void setLastObserverState(State lastObserverState) throws RemoteException;

    SubjectRI getSubjectRI() throws RemoteException;

    void setSubjectRI(SubjectRI subjectRI) throws RemoteException;

    User getUser() throws RemoteException;

    void setUser(User user) throws RemoteException;
}
