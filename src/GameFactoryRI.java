public interface GameFactoryRI {
    public void listGames() throws Exception;
    public boolean register(String username, String pwd) throws Exception;
    public DigLibSessionRI login(String username, String pwd) throws Exception;
}
