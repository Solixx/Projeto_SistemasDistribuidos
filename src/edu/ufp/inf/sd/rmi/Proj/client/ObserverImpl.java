package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl  extends UnicastRemoteObject implements ObserverRI {

    private String id;
    private State lastObserverState;
    protected SubjectRI subjectRI;
    protected User user;

    protected ObserverImpl(String id, User f, SubjectRI subjectTI) throws RemoteException {
        super();
        this.id = id;
        this.user = f;
        this.subjectRI = subjectTI;
        this.subjectRI.attach(this);
    }

    protected ObserverImpl(String id, User f, String arg[]) throws RemoteException {
        super();
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public State getLastObserverState() {
        return lastObserverState;
    }

    public void setLastObserverState(State lastObserverState) {
        this.lastObserverState = lastObserverState;
    }

    public SubjectRI getSubjectRI() {
        return subjectRI;
    }

    public void setSubjectRI(SubjectRI subjectRI) {
        this.subjectRI = subjectRI;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
