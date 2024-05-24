package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.DigLibFactoryimpl;
import edu.ufp.inf.sd.rmi.Proj.server.DiglibSessionimpl;
import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl  extends UnicastRemoteObject implements ObserverRI, Serializable {

    private final int id;
    private State lastObserverState;
    protected SubjectRI subjectRI;
    protected User user;

    public ObserverImpl(User user) throws RemoteException {
        super();
        this.id = user.getId();
        this.user = user;
        user.setObserver(this);
    }

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
    public void update() throws RemoteException, InterruptedException {
        setLastObserverState(this.subjectRI.getState());
        for (Player p: user.getGame().players) {
            //System.out.println("Id: " + user.getId() + "players: " + p);
            if(p.user.getId() == user.getId()){
                p.panel.repaint();
            }
        }

        this.receiver();
    }

    public void receiver() throws RemoteException, InterruptedException {
        String[] str;

        str = lastObserverState.getMsg().split(" ");

        if(str[0].isEmpty())    return;

        if(str[0].equals("StartGame")){
            System.out.println("Userid: " + user.getId() + " State: " + lastObserverState.getMsg());
            new Window(user.getGame(), this);
            return;
        }

        //System.out.println("lastState: " + lastObserverState.getMsg() + "userID: " + user.getId());

        if(!isNumeric(str[0])) return;

        Player p = user.getGame().findPlayer(Integer.parseInt(str[0]));


        if (str[1].equals("mapUpdate")) { //p null
            user.getGame().setSpriteMap(str[2], Integer.parseInt(str[3]), Integer.parseInt(str[4]), user);
            //Game.setSpriteMap(Client.in.next(), Client.in.nextInt(), Client.in.nextInt(), user);
            user.getGame().findPlayer(user.getId()).panel.repaint();
            //game.you.panel.repaint();
        }
        else if (str[1].equals("newCoordinate")) {
            p.x = Integer.parseInt(str[2]);
            p.y = Integer.parseInt(str[3]);

            if(p.getUser().getId() == 1){
                System.out.println("Player: " + id);
                System.out.println("Str P1 newCord: " + str[0] + " " + str[1] + " " + str[2] +  " " + str[3]);
            }

            user.getGame().findPlayer(user.getId()).panel.repaint();
            //game.you.panel.repaint();
        }
        else if (str[1].equals("newStatus")) {
            p.sc.setLoopStatus(str[2]);
        }
        else if (str[1].equals("stopStatusUpdate")) {
            p.sc.stopLoopStatus();
        }
        else if (str[1].equals("playerJoined")) {
            p.alive = true;
        }
    }

    public Player findPlayer(int id) throws RemoteException{
        return user.getGame().findPlayer(id);
    }

    @Override
    public Game createGame(int numPlayer, SubjectRI subject, DigLibFactoryimpl digLibFactoryImpl) throws RemoteException {
        System.out.println("createGame observer");
        if (user.getGame() == null) {
            this.setSubjectRI(subject);
            subject.attach(this);
            Game game = new Game(numPlayer, this.user, subject);
            this.user.setGame(game);
            digLibFactoryImpl.getDbMockup().insertSala(game);
            return game;
        } else {
            return null;
        }
    }

    @Override
    public boolean joinSala(int id, Game sala) throws RemoteException, InterruptedException {
        if(user.getGame() == null){
            if(sala.users.size() < sala.maxPlayers){
                //new ObserverImpl(this.user, sala.subjectRI);
                this.setSubjectRI(sala.subjectRI);
                sala.subjectRI.attach(this);
                this.user.setObserver(this);
                sala.addUser(user);
            }
            return true;
        } else{
            return false;
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

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
