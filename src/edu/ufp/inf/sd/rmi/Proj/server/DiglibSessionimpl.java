package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.Client;
import edu.ufp.inf.sd.rmi.Proj.client.ObserverRI;
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

    @Override
    public void logout() {
        this.digLibFactoryimpl.getSessionRIHashMap().remove(this.user);
    }

    @Override
    public ArrayList<Sala> listSalas() throws RemoteException {
        return digLibFactoryimpl.getDbMockup().getSalas();
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
    public void createSala(int maxPlayers, ObserverRI observerRI) throws RemoteException, InterruptedException {
        SubjectRI subjectRI = new SubjectImpl();
        subjectRI.attach(observerRI);
        observerRI.setSubjectRI(subjectRI);
        Sala sala = new Sala(subjectRI, maxPlayers, observerRI.getUser());
        digLibFactoryimpl.getDbMockup().getSalas().add(sala);
        //System.out.println("Server Create SubjectRI: " + subjectRI + " User: " + observerRI.getUser() + " Sala: " + sala);
//        System.out.println("Observers Create Server:");
//        for (ObserverRI o: sala.getSubjectRI().getObservers()){
//            System.out.println(o.getId());
//        }
    }

    @Override
    public void joinSala(int id, ObserverRI observerRI) throws RemoteException, InterruptedException {
        Sala sala = digLibFactoryimpl.getDbMockup().select(id);
        sala.getSubjectRI().attach(observerRI);
        observerRI.setSubjectRI(sala.getSubjectRI());

//        System.out.println("Server Join SubjectRI: " + sala.getSubjectRI() + " User: " + observerRI.getUser() + " Sala: " + sala);
//        System.out.println("Observers Join Server:");
//        for (ObserverRI o: sala.getSubjectRI().getObservers()){
//            System.out.println(o.getId());
//        }

        sala.attach(user);

        digLibFactoryimpl.getDbMockup().updateSala(sala);
    }
}
