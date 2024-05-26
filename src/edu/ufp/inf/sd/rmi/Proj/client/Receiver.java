package edu.ufp.inf.sd.rmi.Proj.client;

import java.io.Serializable;

//recebe informações de todos os clientes
public class Receiver extends Thread implements Serializable {
   Player p;
   Game game;
   Client client;
   
   Player fromWhichPlayerIs(int id) {
      for (int i = 0; i < game.getClientes().size(); i++){
         Client c = game.getClientes().get(i);
         if (c.getId() == id)
            return game.getYou();
         else if(c.getId() == game.getEnemy1().id)
            return game.getEnemy1();
         if(game.getClientes().size() == 3){
            if(c.getId() == game.getEnemy2().id)
               return game.getEnemy2();
            if(game.getClientes().size() == 4){
               if(c.getId() == game.getEnemy3().id)
                  return game.getEnemy3();
            }
         }
      }
      return null;
   }

   public void run(String msg) {
      String[] str = msg.split(" ");

      this.p = fromWhichPlayerIs(Integer.parseInt(str[0]));

      switch (str[1]) {
         case "mapUpdate":
            this.game.setSpriteMap(str[2], Integer.parseInt(str[3]), Integer.parseInt(str[4]));
            this.game.getYou().panel.repaint();
            break;
         case "newCoordinate":
            p.x = Integer.parseInt(str[2]);
            p.y = Integer.parseInt(str[3]);
            this.game.getYou().panel.repaint();
            break;
         case "newStatus":
            p.sc.setLoopStatus(str[2]);
            break;
         case "stopStatusUpdate":
            p.sc.stopLoopStatus();
            break;
         case "playerJoined":
            p.alive = true;
            break;
      }
   }

    public Receiver(Game game, Client client) {
        this.game = game;
        this.client = client;
    }
}