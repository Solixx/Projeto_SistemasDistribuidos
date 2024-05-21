package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl  extends UnicastRemoteObject implements ObserverRI  {

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
    public void update() throws RemoteException, InterruptedException {
        setLastObserverState(this.subjectRI.getState());
        for (Player p: user.game.players) {
            System.out.println("Id: " + user.getId() + "players: " + p);
            if(p.user.getId() == user.getId()){
                p.panel.repaint();
            }
        }

        String[] str;

        str = lastObserverState.getMsg().split(" ");

        if(str[0].isEmpty())    return;

        if(str[0].equals("StartGame")){
            user.game.gameStarted = true;
            new Window(user.game, user.game.findObserver(user.getId()));
            return;
        }

        System.out.println("lastState: " + lastObserverState.getMsg() + "userID: " + user.getId());

        Player p = user.game.findPlayer(Integer.parseInt(str[0]));


        if (str[1].equals("mapUpdate")) { //p null
            user.game.setSpriteMap(str[2], Integer.parseInt(str[3]), Integer.parseInt(str[4]), user);
            //Game.setSpriteMap(Client.in.next(), Client.in.nextInt(), Client.in.nextInt(), user);
            user.game.findPlayer(user.getId()).panel.repaint();
            //game.you.panel.repaint();
        }
        else if (str[1].equals("newCoordinate")) {
            p.x = Integer.parseInt(str[2]);
            p.y = Integer.parseInt(str[3]);
            user.game.findPlayer(user.getId()).panel.repaint();
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
        return user.game.findPlayer(id);
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
