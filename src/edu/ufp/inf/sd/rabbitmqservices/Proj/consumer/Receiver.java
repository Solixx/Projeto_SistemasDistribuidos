package edu.ufp.inf.sd.rabbitmqservices.Proj.consumer;

import java.io.Serializable;

//recebe informações de todos os clientes
public class Receiver extends Thread implements Serializable {
   Player p;
   Game game;
   Client client;
   
   Player fromWhichPlayerIs(int id) {
      System.out.println("fromWhichPlayerIs: " + id);
      System.out.println("You: " + game.getYou().id);
      System.out.println("Enemy1: " + game.getEnemy1().id);
      System.out.println("Enemy2: " + game.getEnemy2().id);
      System.out.println("Enemy3: " + game.getEnemy3().id);
      if(game.getYou().id == id) {
         System.out.println("escolheu YOU");
         return game.getYou();
      }
      else if(game.getEnemy1().id == id) {
            System.out.println("escolheu Enemy1");
         return game.getEnemy1();
      }
      else if(game.getEnemy2().id == id) {
         return game.getEnemy2();
      }
      else{
         return game.getEnemy3();
      }
   }

   public void run(String msg) {
      System.out.println("edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Receiver: " + msg);
      String[] str = msg.split(" ");
      System.out.println("Sout[0]: " + str[0]);

      System.out.println("isNumeric: " + isNumeric(str[0]));
      //if(!isNumeric(str[0]))   return;
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

   public static boolean isNumeric(String str) {
      if (str == null || str.isEmpty()) {
         return false;
      }
      try {
         if(Integer.parseInt(str) >= 0){
            return true;
         }
         return false;
      } catch (NumberFormatException e) {
         return false;
      }
   }
}