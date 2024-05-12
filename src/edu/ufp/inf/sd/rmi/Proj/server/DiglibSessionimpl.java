package edu.ufp.inf.sd.rmi.Proj.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DiglibSessionimpl extends UnicastRemoteObject implements DigLibSessionRI {

    private  DigLibFactoryimpl digLibFactoryimpl;

    private String user;
    protected DiglibSessionimpl(DigLibFactoryimpl digLibFactoryimpl, String user) throws RemoteException {
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
}
