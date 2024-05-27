package edu.ufp.inf.sd.rabbitmqservices.Proj.consumer;

import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Game extends JPanel implements Serializable {
   private static final long serialVersionUID = 1L;
   private Player you, enemy1, enemy2, enemy3;
    private Client youClient;

   Game(int width, int height, Client youClient) throws RemoteException {
      this.youClient = youClient;

      setPreferredSize(new Dimension(width, height));
      try {
         System.out.print("Inicializando jogadores...");

         int id = youClient.getId();

         you = new Player(id%Const.QTY_PLAYERS, this, youClient, this);
         enemy1 = new Player((id+1)%Const.QTY_PLAYERS, this, youClient, this);
         enemy2 = new Player((id+2)%Const.QTY_PLAYERS, this, youClient, this);
         enemy3 = new Player((id+3)%Const.QTY_PLAYERS, this, youClient, this);

//         for(int i = 0; i < clients.size(); i++){
//            edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Client c = clients.get(i);
//            System.out.println("edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Client id: " + c.getId());
//            edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Player p = new edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Player(id++%clients.size(), this, c, this);
//            if(c.getId() == youClient.getId()){
//               you = p;
//               System.out.println("You edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Player game: " + you.id);
//            }else{
//               if(enemy1 == null){
//                  enemy1 = p;
//                  System.out.println("Enemy1 edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Player game: " + enemy1.id);
//               }else if(enemy2 == null){
//                  enemy2 = p;
//               }else if(enemy3 == null){
//                  enemy3 = p;
//               }
//            }
//         }
//         if(enemy2 == null){
//            System.out.println("You: " + you.id + " Enemy1: " + enemy1.id);
//         } else if(enemy3 == null){
//            System.out.println("You: " + you.id + " Enemy1: " + enemy1.id + " Enemy2: " + enemy2.id);
//         } else{
//            System.out.println("You: " + you.id + " Enemy1: " + enemy1.id + " Enemy2: " + enemy2.id + " Enemy3: " + enemy3.id);
//         }
      } catch (InterruptedException e) {
         System.out.println(" erro: " + e + "\n");
         System.exit(1);
      }
      System.out.print(" ok\n");

      System.out.println("Meu jogador: " + Sprite.personColors[youClient.getId()]);
   }

   //desenha os componentes, chamada por paint() e repaint()
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawMap(g);
      enemy1.draw(g);
      if(enemy2 != null){
         enemy2.draw(g);
      }
      if(enemy3 != null){
         enemy3.draw(g);
      }
      you.draw(g);
      
      // System.out.format("%s: %s [%04d, %04d]\n", edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Game.you.color, edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Game.you.status, edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Game.you.x, edu.ufp.inf.sd.rabbitmqservices.Proj.producer.Game.you.y);;
      Toolkit.getDefaultToolkit().sync();
   }
   
   void drawMap(Graphics g) {
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            g.drawImage(
               Sprite.ht.get(youClient.getMap()[i][j].img),
                    youClient.getMap()[i][j].x, youClient.getMap()[i][j].y,
               Const.SIZE_SPRITE_MAP, Const.SIZE_SPRITE_MAP, null
            );
   }

   public void setSpriteMap(String keyWord, int l, int c) {
      youClient.map[l][c].img = keyWord;
   }

   public Player getYou() {
      return you;
   }

   public void setYou(Player you) {
      this.you = you;
   }

   public Player getEnemy1() {
      return enemy1;
   }

   public void setEnemy1(Player enemy1) {
      this.enemy1 = enemy1;
   }

   public Player getEnemy2() {
      return enemy2;
   }

   public void setEnemy2(Player enemy2) {
      this.enemy2 = enemy2;
   }

   public Player getEnemy3() {
      return enemy3;
   }

   public void setEnemy3(Player enemy3) {
      this.enemy3 = enemy3;
   }

   public Client getYouClient() {
      return youClient;
   }

   public void setYouClient(Client youClient) {
      this.youClient = youClient;
   }
}