package edu.ufp.inf.sd.rmi.Proj.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class DigLibFactoryimpl extends UnicastRemoteObject implements DigLibFactoryRI, Serializable {

    private DBMockup dbMockup;

    private HashMap<User,DigLibSessionRI> sessionRIHashMap;


    public DBMockup getDbMockup() {
        return dbMockup;
    }

    public HashMap<User, DigLibSessionRI> getSessionRIHashMap() {
        return sessionRIHashMap;
    }

    protected DigLibFactoryimpl(DBMockup dbMockup) throws RemoteException {
        super();
        this.dbMockup = dbMockup;
        this.sessionRIHashMap = new HashMap<>();
    }

    @Override
    public boolean register(String usernamne, String pwd) {
        if(usernamne != null && pwd != null && !this.dbMockup.exists(usernamne,pwd)){
            dbMockup.register(usernamne,pwd);
            //System.out.println("regiuster feito");
            return true;
        }
        //System.out.println("regiuster nao feito");
        return false;
    }

    @Override
    public DigLibSessionRI login(String user, String pwd) throws RemoteException {
        if(this.dbMockup.exists(user,pwd)){
            //System.out.println("login feito");
            //if (this.sessionRIHashMap.containsKey(user)){
            if (this.containsUser(user, pwd) != null){
                //System.out.println("log if");
                return this.sessionRIHashMap.get(this.containsUser(user, pwd));
            }else{
                //System.out.println("log out");
                User u = this.dbMockup.findUser(user, pwd);
                DigLibSessionRI digLibSessionRI = new DiglibSessionimpl(this,u);
                this.sessionRIHashMap.put(u, digLibSessionRI);
                return digLibSessionRI;
            }
        }else {
            //System.out.println("egister feito");
            this.register(user,pwd);
            User u = this.dbMockup.findUser(user, pwd);
            DigLibSessionRI digLibSessionRI = new DiglibSessionimpl(this,u);
            this.sessionRIHashMap.put(u, digLibSessionRI);
            return digLibSessionRI;
        }
    }

    public User containsUser(String user, String pwd){
        for (User u : this.sessionRIHashMap.keySet()) {
            if(u.getUname().compareTo(user) == 0 && u.getPword().compareTo(pwd) == 0){
                return u;
            }
        }

        return null;
    }

}
