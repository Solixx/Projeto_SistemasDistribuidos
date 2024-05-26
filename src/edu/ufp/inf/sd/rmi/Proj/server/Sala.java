package edu.ufp.inf.sd.rmi.Proj.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Sala implements Serializable {
    static int currID = 0;
    private int id;
    private SubjectRI subjectRI;
    private ArrayList<User> users = new ArrayList<>();
    private int maxPlayers = 4;

    public Sala(SubjectRI subjectRI, int maxPlayers, User user) throws RemoteException, InterruptedException {
        this.id = currID++;
        this.subjectRI = subjectRI;

        if(maxPlayers < 2)
            this.maxPlayers = 2;
        else if(maxPlayers < 4)
            this.maxPlayers = maxPlayers;

        attach(user);
    }

    public void attach(User user) throws RemoteException, InterruptedException {
        if(this.users.size() >= this.maxPlayers)
            return;
        this.users.add(user);
        if(this.users.size() == this.maxPlayers){
            this.subjectRI.setState(new State(this.id, this.id + " GameStart"));
        }
    }

    public void detach(User user) throws RemoteException {
        if(this.users.size() == 0)
            return;
        this.users.remove(user);
        this.subjectRI.detach(user.getObserver());
    }

    public int getId() {
        return id;
    }

    public SubjectRI getSubjectRI() {
        return subjectRI;
    }

    public void setSubjectRI(SubjectRI subjectRI) {
        this.subjectRI = subjectRI;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
