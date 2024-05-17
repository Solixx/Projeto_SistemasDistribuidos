package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.Proj.client.ObserverRI;
import edu.ufp.inf.sd.rmi.Proj.client.User;
import edu.ufp.inf.sd.rmi.Proj.client.Game;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class DiglibSessionimpl extends UnicastRemoteObject implements DigLibSessionRI {

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

        if(user.game == null){
            Game game = new Game(numPlayer, this.user, subject);
            this.user.game = game;
            this.digLibFactoryimpl.getDbMockup().insertSala(game);
            return true;
        } else{
            return false;
        }
    }

    @Override
    public boolean joinSala(int id) throws RemoteException {
        Game sala = this.digLibFactoryimpl.getDbMockup().select(id);

        if(user.game == null){
            if(sala.users.size() < sala.maxPlayers){
                sala.addUser(user);
                new ObserverImpl(this.user, sala.subjectRI);
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

    public DigLibFactoryimpl getDigLibFactoryimpl() {
        return digLibFactoryimpl;
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

}
