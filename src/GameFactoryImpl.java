import java.rmi.RemoteException;
import java.util.HashMap;

public class GameFactoryImpl implements GameFactoryRI{
    public DBMockup getDbMockup() {
        return dbMockup;
    }

    public HashMap<String, DigLibSessionRI> getSessionRIHashMap() {
        return sessionRIHashMap;
    }

    private DBMockup dbMockup;

    private HashMap<String,DigLibSessionRI> sessionRIHashMap;


    protected GameFactoryImpl(DBMockup dbMockup) throws RemoteException {
        super();
        this.dbMockup = dbMockup;
        this.sessionRIHashMap = new HashMap<>();
    }

    @Override
    public void listGames() throws Exception {
        System.out.println("List of games");
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
                DigLibSessionRI digLibSessionRI = new DigLibSessionImpl(this,user);
                this.sessionRIHashMap.put(user, digLibSessionRI);
                return digLibSessionRI;
            }
        }else {
            this.register(user,pwd);
            DigLibSessionRI digLibSessionRI = new DigLibSessionImpl(this,user);
            this.sessionRIHashMap.put(user, digLibSessionRI);
            return digLibSessionRI;
        }
    }
}
