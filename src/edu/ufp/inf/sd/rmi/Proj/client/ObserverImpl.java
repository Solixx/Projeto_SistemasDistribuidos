package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl  extends UnicastRemoteObject implements ObserverRI {

    private final int id;
    private State lastObserverState;
    protected SubjectRI subjectRI;
    protected User user;

    public ObserverImpl(User f, SubjectRI subjectTI) throws RemoteException {
        super();
        this.id = f.getId();
        this.user = f;
        this.subjectRI = subjectTI;
        this.subjectRI.attach(this);
        this.user.setObserver(this);
    }

    public ObserverImpl(User f, String arg[]) throws RemoteException {
        super();
        this.id = f.getId();
        this.user = f;
    }

    @Override
    public void update() throws RemoteException {
        setLastObserverState(this.subjectRI.getState());
        for (Player p: user.game.players) {
            if(p.user.getId() == user.getId()){
                p.panel.repaint();
            }
        }
    }

    @Override
    public int getId() throws RemoteException {
        return id;
    }

    @Override
    public State getLastObserverState() throws RemoteException {
        return lastObserverState;
    }

    @Override
    public void setLastObserverState(State lastObserverState) throws RemoteException {
        this.lastObserverState = lastObserverState;
    }

    @Override
    public SubjectRI getSubjectRI() throws RemoteException {
        return subjectRI;
    }

    @Override
    public void setSubjectRI(SubjectRI subjectRI) throws RemoteException {
        this.subjectRI = subjectRI;
    }

    @Override
    public User getUser() throws RemoteException {
        return user;
    }

    @Override
    public void setUser(User user) throws RemoteException {
        this.user = user;
    }


}
