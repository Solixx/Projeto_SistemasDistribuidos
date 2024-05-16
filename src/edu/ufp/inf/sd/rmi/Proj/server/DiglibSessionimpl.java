package edu.ufp.inf.sd.rmi.Proj.server;

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
    public void criarSala(int numPlayer) throws RemoteException {
        System.out.println("numP: "+numPlayer);
        System.out.println("numP: "+this.user);
        this.digLibFactoryimpl.getDbMockup().insertSala(new Game(numPlayer, this.user));
    }

    @Override
    public void joinSala(int id) throws RemoteException {
        Game sala = this.digLibFactoryimpl.getDbMockup().select(id);
        if(sala.users.size() < sala.maxPlayers)    sala.addUser(user);
    }

    @Override
    public ArrayList<Game> listSalas() throws RemoteException {
        return this.digLibFactoryimpl.getDbMockup().listSalas();
    }

    @Override
    public Game select(int id) throws RemoteException {
        return this.digLibFactoryimpl.getDbMockup().select(id);
    }

    public DigLibFactoryimpl getDigLibFactoryimpl() {
        return digLibFactoryimpl;
    }

    public void setDigLibFactoryimpl(DigLibFactoryimpl digLibFactoryimpl) {
        this.digLibFactoryimpl = digLibFactoryimpl;
    }

    public User serachUser(String name, String pwd){
        return this.getDigLibFactoryimpl().getDbMockup().findUser(name, pwd);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
