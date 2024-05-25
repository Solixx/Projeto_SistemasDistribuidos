package edu.ufp.inf.sd.rmi.Proj.client;

import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.JPanel;

//tanto para you quanto para enemy
public class Player implements Serializable {
   int x, y;
   String status, color;
   JPanel panel;
   boolean alive;
   int id;
   Game game;

   StatusChanger sc;

   Player(JPanel panel, int id, Game game) throws InterruptedException {
//      this.x = user.getSpawn()[game.players.size()].x;
//      this.y = user.getSpawn()[game.players.size()].y;
      this.x = game.playerData[game.playerData.length-1].x;
      this.y = game.playerData[game.playerData.length-1].y;
      this.color = Sprite.personColors[game.players.size()];
      this.panel = panel;
      for (PlayerData pd: game.playerData) {
         if(pd.userID == id){
            this.alive = pd.alive;
         }
      }

      Sprite.setMaxLoopStatus();

      this.id = id;
      this.game = game;

      (sc = new StatusChanger(this, "wait")).start();
   }

   public void draw(Graphics g) {
      if (alive)
         g.drawImage(Sprite.ht.get(color + "/" + status), x, y, Const.WIDTH_SPRITE_PLAYER, Const.HEIGHT_SPRITE_PLAYER, null);
   }

   public int getId() {
      return id;
   }

   public int getX() {
      return x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public Game getGame() {
      return game;
   }

   public void setGame(Game game) {
      this.game = game;
   }
}

class StatusChanger extends Thread implements Serializable {
   Player p;
   String status;
   int index;
   boolean playerInMotion;

   StatusChanger(Player p, String initialStatus) {
      this.p = p;
      this.status = initialStatus;
      index = 0;
      playerInMotion = true;
   }
   public void run() {
      while (true) {
         p.status = status + "-" + index;
         if (playerInMotion) {
            index = (++index) % Sprite.maxLoopStatus.get(status);
            p.panel.repaint();
         }

         try {
            sleep(Const.RATE_PLAYER_STATUS_UPDATE);
         } catch (InterruptedException e) {}

         if (p.status.equals("dead-4")) {
            p.alive = false;
            if (this.p.getGame().players.contains(this.p))
               System.exit(1);
         }
      }
   }
   void setLoopStatus(String status) {
      this.status = status;
      index = 1;
      playerInMotion = true;
   }
   void stopLoopStatus() {
      playerInMotion = false;
      index = 0;
   }
}