package rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class DigLibFactoryimpl extends UnicastRemoteObject implements DigLibFactoryRI {

    private DBMockup dbMockup;

    private HashMap<String,DigLibSessionRI> sessionRIHashMap;


    public DBMockup getDbMockup() {
        return dbMockup;
    }

    public HashMap<String, DigLibSessionRI> getSessionRIHashMap() {
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
            return true;
        }
        return false;
    }

    @Override
    public DigLibSessionRI login(String user, String pwd) throws RemoteException {
        if(this.dbMockup.exists(user,pwd)){
            if (this.sessionRIHashMap.containsKey(user)){
                return this.sessionRIHashMap.get(user);
            }else{
                DigLibSessionRI digLibSessionRI = new DiglibSessionimpl(this,user);
                this.sessionRIHashMap.put(user, digLibSessionRI);
                return digLibSessionRI;
            }
        }else {
            this.register(user,pwd);
            DigLibSessionRI digLibSessionRI = new DiglibSessionimpl(this,user);
            this.sessionRIHashMap.put(user, digLibSessionRI);
            return digLibSessionRI;
        }
    }

}
