package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;
import edu.ufp.inf.sd.rmi.Proj.server.User;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Game extends JPanel implements Serializable {
   private static final long serialVersionUID = 1L;
   private Player you, enemy1, enemy2, enemy3;
    private ArrayList<Client> clients;
    private SubjectRI subjectRI;
    private Client youClient;

   Game(int width, int height, SubjectRI subjectRI, Client youClient) throws RemoteException {
      this.subjectRI = subjectRI;
      this.clients = subjectRI.getClients();
      this.youClient = youClient;

      setPreferredSize(new Dimension(width, height));
      try {
         System.out.print("Inicializando jogadores...");

         System.out.println("Clients size: " + clients.size());

         int id = youClient.getId();

         System.out.println("YouClient id: " + id);
         you = new Player(id%clients.size(), this, youClient, this);
         System.out.println("You Player game: " + you.id);
         enemy1 = new Player((id+1)%clients.size(), this, youClient, this);
         System.out.println("Enemy1 Player game: " + enemy1.id);
         enemy2 = new Player((id+2)%clients.size(), this, youClient, this);
         enemy3 = new Player((id+3)%clients.size(), this, youClient, this);

//         for(int i = 0; i < clients.size(); i++){
//            Client c = clients.get(i);
//            System.out.println("Client id: " + c.getId());
//            Player p = new Player(id++%clients.size(), this, c, this);
//            if(c.getId() == youClient.getId()){
//               you = p;
//               System.out.println("You Player game: " + you.id);
//            }else{
//               if(enemy1 == null){
//                  enemy1 = p;
//                  System.out.println("Enemy1 Player game: " + enemy1.id);
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

      System.out.println("Meu jogador: " + Sprite.personColors[yourUserIndex(youClient.getId())]);
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
      
      // System.out.format("%s: %s [%04d, %04d]\n", Game.you.color, Game.you.status, Game.you.x, Game.you.y);;
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

   public Client findUser(int id){
        for(Client c : clients){
             if(c.getId() == id){
                return c;
             }
        }
        return null;
   }

   public int yourUserIndex(int id){
      for(int i = 0; i < clients.size(); i++){
         if(clients.get(i).getId() == id){
            return i;
         }
      }
      return -1;
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

   public ArrayList<Client> getClientes() {
      return clients;
   }

   public void setClientes(ArrayList<Client> clients) {
      this.clients = clients;
   }

   public SubjectRI getSubjectRI() {
      return subjectRI;
   }

   public void setSubjectRI(SubjectRI subjectRI) {
      this.subjectRI = subjectRI;
   }

   public Client getYouClient() {
      return youClient;
   }

   public void setYouClient(Client youClient) {
      this.youClient = youClient;
   }
}