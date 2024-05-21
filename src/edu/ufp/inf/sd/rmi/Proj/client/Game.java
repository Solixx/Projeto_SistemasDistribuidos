package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectImpl;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.rmi.RemoteException;
import java.util.ArrayList;

import java.io.Serializable;
import javax.swing.JPanel;

class PlayerData implements Serializable {
   boolean logged, alive;
   int x, y; //coordenada atual
   int numberOfBombs;
   int userID;

   PlayerData(int x, int y) {
      this.x = x;
      this.y = y;
      this.logged = false;
      this.alive = false;
      this.numberOfBombs = 1; // para 2 bombas, é preciso tratar cada bomba em uma thread diferente
   }

   public void setUserID(int id){
      this.userID = id;
   }
}

public class Game extends JPanel implements Serializable {
   private static int currId = 1;
   private int id = 0;
   public int maxPlayers = 2;
   private static final long serialVersionUID = 1L;
   public ArrayList<User> users = new ArrayList<>();
   public ArrayList<Player> players = new ArrayList<>();
   public SubjectRI subjectRI;

   public PlayerData[] playerData = new PlayerData[Const.QTY_PLAYERS];
   public Coordinate[][] map = new Coordinate[Const.LIN][Const.COL];
   public boolean gameStarted;

   public Game(int maxPlayers, User user, SubjectRI subject) {
      this.id = currId++;
      this.gameStarted = false;

      setPreferredSize(new Dimension(Const.COL*Const.SIZE_SPRITE_MAP, Const.LIN*Const.SIZE_SPRITE_MAP));
      try {
         setMap();
         setPlayerData();

         System.out.print("Inicializando jogadores...");

         if(maxPlayers > 4) this.maxPlayers = 4;
         else this.maxPlayers = Math.max(maxPlayers, 2);

         this.subjectRI = subject;

         this.addUser(user);

//         you = new Player(Client.id, this);
//         enemy1 = new Player((Client.id+1)%Const.QTY_PLAYERS, this);
//         enemy2 = new Player((Client.id+2)%Const.QTY_PLAYERS, this);
//         enemy3 = new Player((Client.id+3)%Const.QTY_PLAYERS, this);
         } catch (Exception e){
            System.out.println(" erro: " + e + "\n");
            System.exit(1);
      }
//      catch (InterruptedException e) {
//         System.out.println(" erro: " + e + "\n");
//         System.exit(1);
//      }
      System.out.print(" ok\n");

      System.out.println("Meu jogador: " + Sprite.personColors[user.getId()]);
   }

   public void startGame() throws InterruptedException, RemoteException {
      for(int i = 0; i < users.size(); i++){
         User user = users.get(i);
         //players.add(new Player(this, user, this));
      }
   }

   //desenha os componentes, chamada por paint() e repaint()
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawMap(g);
      for (Player p: players) {
         p.draw(g);
      }
      
      // System.out.format("%s: %s [%04d, %04d]\n", Game.you.color, Game.you.status, Game.you.x, Game.you.y);;
      Toolkit.getDefaultToolkit().sync();
   }
   
   void drawMap(Graphics g) {
      for (User u : this.users) {
         for (int i = 0; i < Const.LIN; i++)
            for (int j = 0; j < Const.COL; j++)
               g.drawImage(
                       Sprite.ht.get(u.map[i][j].img),
                       u.map[i][j].x, u.map[i][j].y,
                       Const.SIZE_SPRITE_MAP, Const.SIZE_SPRITE_MAP, null
               );
      }
   }

   public void setSpriteMap(String keyWord, int l, int c, User u) {
      u.map[l][c].img = keyWord;
   }

   public void addUser(User user) throws RemoteException, InterruptedException {
      this.users.add(user);
      user.joinGame(this);
      players.add(new Player(this, user, this));
      for(int i = 0; i < maxPlayers; i++){
         if (!playerData[i].logged) {
            playerData[i].setUserID(user.getId());
            new ClientManager(user.getObserver(), user.getId(), this).start();
            break;
         }
      }
   }

   public int getMaxPlayers() {
      return maxPlayers;
   }

   public void setMaxPlayers(int maxPlayers) {
      this.maxPlayers = maxPlayers;
   }

   public ArrayList<User> getUsers() {
      return users;
   }

   public void setUsers(ArrayList<User> users) {
      this.users = users;
   }

   public int getId() {
      return id;
   }

   public Player findPlayer(int id){
      for (Player player: this.players) {
         if(player.getUser().getId() == id){
            return player;
         }
      }
      return null;
   }

   public PlayerData findPlayerData(int id){
      for (PlayerData player: this.playerData) {
         if(player.userID == id){
            return player;
         }
      }
      return null;
   }

   public ObserverRI findObserver(int id) throws RemoteException {
      for (ObserverRI observer: this.subjectRI.getObservers()) {
         if(observer.getUser().getId() == id){
            return observer;
         }
      }
      return null;
   }

   public boolean loggedIsFull() throws RemoteException, InterruptedException {
      for (int i = 0; i < maxPlayers; i++){
         if (!playerData[i].logged)
            return false;
      }

      if(!this.gameStarted)
      {
         this.subjectRI.setState(new State("StartGame"));
      }
      return true;
   }

   void setMap() {
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, "block");

      // paredes fixas das bordas
      for (int j = 1; j < Const.COL - 1; j++) {
         map[0][j].img = "wall-center";
         map[Const.LIN - 1][j].img = "wall-center";
      }
      for (int i = 1; i < Const.LIN - 1; i++) {
         map[i][0].img = "wall-center";
         map[i][Const.COL - 1].img = "wall-center";
      }
      map[0][0].img = "wall-up-left";
      map[0][Const.COL - 1].img = "wall-up-right";
      map[Const.LIN - 1][0].img = "wall-down-left";
      map[Const.LIN - 1][Const.COL - 1].img = "wall-down-right";

      // paredes fixas centrais
      for (int i = 2; i < Const.LIN - 2; i++)
         for (int j = 2; j < Const.COL - 2; j++)
            if (i % 2 == 0 && j % 2 == 0)
               map[i][j].img = "wall-center";

      // arredores do spawn
      map[1][1].img = "floor-1";
      map[1][2].img = "floor-1";
      map[2][1].img = "floor-1";
      map[Const.LIN - 2][Const.COL - 2].img = "floor-1";
      map[Const.LIN - 3][Const.COL - 2].img = "floor-1";
      map[Const.LIN - 2][Const.COL - 3].img = "floor-1";
      map[Const.LIN - 2][1].img = "floor-1";
      map[Const.LIN - 3][1].img = "floor-1";
      map[Const.LIN - 2][2].img = "floor-1";
      map[1][Const.COL - 2].img = "floor-1";
      map[2][Const.COL - 2].img = "floor-1";
      map[1][Const.COL - 3].img = "floor-1";
   }

   void setPlayerData() {
      playerData[0] = new PlayerData(
              map[1][1].x - Const.VAR_X_SPRITES,
              map[1][1].y - Const.VAR_Y_SPRITES
      );

      playerData[1] = new PlayerData(
              map[Const.LIN - 2][Const.COL - 2].x - Const.VAR_X_SPRITES,
              map[Const.LIN - 2][Const.COL - 2].y - Const.VAR_Y_SPRITES
      );
      playerData[2] = new PlayerData(
              map[Const.LIN - 2][1].x - Const.VAR_X_SPRITES,
              map[Const.LIN - 2][1].y - Const.VAR_Y_SPRITES
      );
      playerData[3] = new PlayerData(
              map[1][Const.COL - 2].x - Const.VAR_X_SPRITES,
              map[1][Const.COL - 2].y - Const.VAR_Y_SPRITES
      );
   }

}