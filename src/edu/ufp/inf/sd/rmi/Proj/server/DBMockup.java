package edu.ufp.inf.sd.rmi.Proj.server;

import edu.ufp.inf.sd.rmi.Proj.client.User;
import edu.ufp.inf.sd.rmi.Proj.client.Game;

import java.util.ArrayList;


/**
 * This class simulates a DBMockup for managing users and books.
 *
 * @author rmoreira
 *
 */
public class DBMockup {

    private final ArrayList<Game> games;// = new ArrayList(); //TODO Mudar para Salas
    private final ArrayList<User> users;// = new ArrayList();

    /**
     * This constructor creates and inits the database with some books and users.
     */
    public DBMockup() {
        games = new ArrayList<>();
        users = new ArrayList<>();
        //Add 3 books
        //sala.add(new Book("Distributed Systems: principles and paradigms", "Tanenbaum"));
        //books.add(new Book("Distributed Systems: concepts and design", "Colouris"));
        //books.add(new Book("Distributed Computing Networks", "Tanenbaum"));
        //Add one user
        //users.add(new User("guest", "ufp"));
    }

    /**
     * Registers a new user.
     *
     * @param u username
     * @param p passwd
     */
    public void register(String u, String p) {
        if (!exists(u, p)) {
            users.add(new User(u, p));
        }
    }

    /**
     * Checks the credentials of an user.
     *
     * @param u username
     * @param p passwd
     * @return
     */
    public boolean exists(String u, String p) {
        for (User usr : this.users) {
            if (usr.getUname().compareTo(u) == 0 && usr.getPword().compareTo(p) == 0) {
                return true;
            }
        }
        return false;
        //return ((u.equalsIgnoreCase("guest") && p.equalsIgnoreCase("ufp")) ? true : false);
    }

    public User findUser(String u, String p){
        for (User usr : this.users) {
            if (usr.getUname().compareTo(u) == 0 && usr.getPword().compareTo(p) == 0) {
                return usr;
            }
        }
        return null;
    }

    /**
     * Inserts a new sala into the DigLib.
     */
      public void insertSala(Game sala) {
          games.add(sala);
      }

      public ArrayList<Game> listSalas(){
          for (Game sala : games) {
              System.out.println("Sala: " + sala.getId() + " MaxPlayers: " + sala.maxPlayers + "CurrentPlayers: " + sala.users.size());
          }

          return games;
      }

    /**
     * Looks up for books with given title and author keywords.
     * TODO Mudar para Salas
     */
    public Game select(int id) {
        Game[] abooks = null;
        // Find books that match
        for (int i = 0; i < games.size(); i++) {
            Game sala = (Game) games.get(i);
            //System.out.println("DB - select(): book[" + i + "] = " + book.getTitle() + ", " + book.getAuthor());
            if (sala.getId() == id) {
                //System.out.println("DB - select(): add book[" + i + "] = " + sala.getTitle() + ", " + sala.getAuthor());
                return sala;
            }
        }

        return null;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
