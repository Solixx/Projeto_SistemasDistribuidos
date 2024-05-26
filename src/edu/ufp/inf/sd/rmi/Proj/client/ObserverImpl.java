package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;
import edu.ufp.inf.sd.rmi.Proj.server.User;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl  extends UnicastRemoteObject implements ObserverRI, Serializable {

    private int id;
    private State lastObserverState;
    private SubjectRI subjectRI;
    private User user;
    private Client client;

    public ObserverImpl(User user) throws RemoteException {
        super();
        this.id = user.getId();
        this.user = user;
        user.setObserver(this);
    }

    public ObserverImpl(int id, SubjectRI subjectTI, User user) throws RemoteException {
        super();
        this.id = user.getId();
        this.subjectRI = subjectTI;
        this.subjectRI.attach(this);
        this.user = user;
        user.setObserver(this);
    }

    public ObserverImpl(String id, String arg[]) throws RemoteException {
        super();
        //this.id = id;
    }

    @Override
    public void update() throws RemoteException {
        setLastObserverState(this.subjectRI.getState());
        //this.chatFrame.updateTextArea();

        String[] msg = this.lastObserverState.getMsg().split(" ");

        if(msg[1].equals("GameStart")){
            System.out.println("GameStart ObserverImpl: " + this.id);
            this.client = new Client(this.id, this.subjectRI, this);
        } else{
            this.client.getReceiver().run(this.lastObserverState.getMsg());
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public State getLastObserverState() {
        return lastObserverState;
    }

    @Override
    public void setLastObserverState(State lastObserverState) {
        this.lastObserverState = lastObserverState;
    }

    @Override
    public SubjectRI getSubjectRI() {
        return subjectRI;
    }

    @Override
    public void setSubjectRI(SubjectRI subjectRI) {
        this.subjectRI = subjectRI;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) throws RemoteException {
        this.user = user;
    }
}
