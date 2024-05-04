import java.rmi.RemoteException;

public class DigLibSessionImpl implements DigLibSessionRI{
    private GameFactoryImpl gameFactory;
    private String user;
    protected DigLibSessionImpl(GameFactoryImpl gameFactory, String user) throws RemoteException {
        super();
        this.gameFactory = gameFactory;
        this.user = user;
    }
    @Override
    public void logout() {
        this.gameFactory.getSessionRIHashMap().remove(this.user);
    }
}
