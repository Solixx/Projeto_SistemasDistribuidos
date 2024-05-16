package edu.ufp.inf.sd.rmi.Proj.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.ArrayList;

import java.io.Serializable;
import javax.swing.JPanel;

public class Game extends JPanel implements Serializable {
   private int id = 0;
   public int maxPlayers = 2;
   private static final long serialVersionUID = 1L;
   public ArrayList<User> users = new ArrayList<>();
   public ArrayList<Player> players;

   public Game(int maxPlayers, User user) {
      id++;
      setPreferredSize(new Dimension(Const.COL*Const.SIZE_SPRITE_MAP, Const.LIN*Const.SIZE_SPRITE_MAP));
      try {
         System.out.print("Inicializando jogadores...");

         if(maxPlayers > 4) this.maxPlayers = 4;
         else this.maxPlayers = Math.max(maxPlayers, 2);

         this.addUser(user);
         players.add(new Player(this, user));

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

      System.out.println("Meu jogador: " + Sprite.personColors[RMIClient.meuUser.getId()]);
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
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            g.drawImage(
               Sprite.ht.get(RMIClient.meuUser.map[i][j].img),
                    RMIClient.meuUser.map[i][j].x, RMIClient.meuUser.map[i][j].y,
               Const.SIZE_SPRITE_MAP, Const.SIZE_SPRITE_MAP, null
            );
   }

   static void setSpriteMap(String keyWord, int l, int c) {
      RMIClient.meuUser.map[l][c].img = keyWord;
   }

   public void addUser(User user){
      this.users.add(user);
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
}