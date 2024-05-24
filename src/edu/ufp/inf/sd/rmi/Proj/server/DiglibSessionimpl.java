package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.Proj.client.ObserverRI;
import edu.ufp.inf.sd.rmi.Proj.client.User;
import edu.ufp.inf.sd.rmi.Proj.client.Game;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class DiglibSessionimpl extends UnicastRemoteObject implements DigLibSessionRI, Serializable {

    private  DigLibFactoryimpl digLibFactoryimpl;
    private User user;

    protected DiglibSessionimpl(DigLibFactoryimpl digLibFactoryimpl, User user) throws RemoteException {
        super();
        this.digLibFactoryimpl = digLibFactoryimpl;
        this.user = user;
    }

//    @Override
//    public Book[] Search(String title, String author) {
//        return this.digLibFactoryimpl.getDbMockup().select(title,author);
//    }

    @Override
    public void logout() {
        this.digLibFactoryimpl.getSessionRIHashMap().remove(this.user);
    }

    @Override
    public boolean criarSala(int numPlayer) throws RemoteException {
        SubjectRI subject = new SubjectImpl();
        new ObserverImpl(this.user, subject);

        if(user.getGame() == null){
            Game game = new Game(numPlayer, this.user, subject);
            this.user.setGame(game);
            this.digLibFactoryimpl.getDbMockup().insertSala(game);
            return true;
        } else{
            return false;
        }
    }

    @Override
    public boolean joinSala(int id) throws RemoteException, InterruptedException {
        Game sala = this.digLibFactoryimpl.getDbMockup().select(id);

        if(user.getGame() == null){
            if(sala.users.size() < sala.maxPlayers){
                new ObserverImpl(this.user, sala.subjectRI);
                sala.addUser(user);
            }
            return true;
        } else{
            return false;
        }
    }

    @Override
    public ArrayList<Game> listSalas() throws RemoteException {
        return this.digLibFactoryimpl.getDbMockup().listSalas();
    }

    @Override
    public Game select(int id) throws RemoteException {
        return this.digLibFactoryimpl.getDbMockup().select(id);
    }

    @Override
    public User serachUser(String name, String pwd) throws RemoteException {
        return this.getDigLibFactoryimpl().getDbMockup().findUser(name, pwd);
    }


    public void setDigLibFactoryimpl(DigLibFactoryimpl digLibFactoryimpl) throws RemoteException {
        this.digLibFactoryimpl = digLibFactoryimpl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public DigLibFactoryimpl getDigLibFactoryimpl() throws RemoteException {
        return digLibFactoryimpl;
    }

    @Override
    public void insertSalaDB(Game game) throws RemoteException {
        digLibFactoryimpl.getDbMockup().insertSala(game);
    }

    @Override
    public SubjectRI createSubjectRI(ObserverRI observer) throws RemoteException {
        SubjectRI subject = new SubjectImpl();
        subject.attach(observer);
        observer.setSubjectRI(subject);
        return subject;
    }

    @Override
    public void updateSubjectRIGame(int id, ObserverRI observer) throws RemoteException {
        Game game = serachGame(id);
        SubjectRI subject = game.subjectRI;

        subject.attach(observer);
        observer.setSubjectRI(subject);
    }

    @Override
    public Game serachGame(int id) throws RemoteException {
        return digLibFactoryimpl.getDbMockup().select(id);
    }
}
