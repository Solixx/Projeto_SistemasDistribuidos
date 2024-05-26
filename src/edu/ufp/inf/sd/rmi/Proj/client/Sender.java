package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.awt.event.*;
import java.io.Serializable;
import java.rmi.RemoteException;

//escuta enquanto a janela (JFrame) estiver em foco
public class Sender extends KeyAdapter implements Serializable {
   int lastKeyCodePressed;
   SubjectRI subjectRI;
   Client client;

   boolean bombPlanted;
   int l, c;
   int id;
   boolean up, right, left, down;
   Game game;
   CoordinatesThrower ct;
   MapUpdatesThrower mt;


   public Sender(SubjectRI subjectRI, Client client) throws RemoteException, InterruptedException {
      this.subjectRI = subjectRI;
      this.client = client;
      this.game = client.getGame();
      this.id = client.getId();
      up = right = left = down = false;
      bombPlanted = false;

      initClientmanager();

      System.out.println("Sender");
   }
   
   public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_SPACE) {
         try {
            clientManager("pressedSpace " + game.getYou().x + " " + game.getYou().y);
         } catch (RemoteException ex) {
            throw new RuntimeException(ex);
         } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
         }
      }
         //Client.out.println("pressedSpace " + Game.you.x + " " + Game.you.y);
      else if (isNewKeyCode(e.getKeyCode())) {
         try {
            clientManager("keyCodePressed " + e.getKeyCode());
         } catch (RemoteException ex) {
            throw new RuntimeException(ex);
         } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
         }
      }
         //Client.out.println("keyCodePressed " + e.getKeyCode());
   }
      
   public void keyReleased(KeyEvent e) {
      //Client.out.println("keyCodeReleased " + e.getKeyCode());
      try {
         clientManager("keyCodeReleased " + e.getKeyCode());
      } catch (RemoteException ex) {
         throw new RuntimeException(ex);
      } catch (InterruptedException ex) {
         throw new RuntimeException(ex);
      }
      lastKeyCodePressed = -1; //a próxima tecla sempre será nova
   }
   
   boolean isNewKeyCode(int keyCode) {
      boolean ok = (keyCode != lastKeyCodePressed) ? true : false;
      lastKeyCodePressed = keyCode;
      return ok;
   }

   public void initClientmanager() throws RemoteException, InterruptedException {
      (ct = new CoordinatesThrower(this.id, client)).start();
      (mt = new MapUpdatesThrower(this.id, client)).start();

      client.getPlayer()[id].logged = true;
      client.getPlayer()[id].alive = true;
      sendInitialSettings();

      for (ObserverRI obs: subjectRI.getObservers())
         if (obs.getId() != this.client.getObserverRI().getId())
            obs.getSubjectRI().setState(new State(id, id + " playerJoined"));
   }

   void sendInitialSettings() throws RemoteException, InterruptedException {
      StringBuilder msg = new StringBuilder();
      msg.append(id);
      //out.print(id);
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            //out.print(" " + Server.map[i][j].img);
            msg.append(" ").append(client.getMap()[i][j].img);

      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         //out.print(" " + Server.player[i].alive);
         msg.append(" ").append(client.getPlayer()[i].alive);

      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         //out.print(" " + Server.player[i].x + " " + Server.player[i].y);
         msg.append(" ").append(client.getPlayer()[i].x).append(" ").append(client.getPlayer()[i].y);
      //out.print("\n");
      msg.append("\n");

      subjectRI.setState(new State(0, msg.toString()));
   }

   public void clientManager(String msg) throws RemoteException, InterruptedException {
      String[] str = msg.split(" ");

      if (str[0].equals("keyCodePressed") && client.getPlayer()[id].alive) {
         ct.keyCodePressed(Integer.parseInt(str[1]));
      }
      else if (str[0].equals("keyCodeReleased") && client.getPlayer()[id].alive) {
         ct.keyCodeReleased(Integer.parseInt(str[1]));
      }
      else if (str[0].equals("pressedSpace") && client.getPlayer()[id].numberOfBombs >= 1) {
         client.getPlayer()[id].numberOfBombs--;
         mt.setBombPlanted(Integer.parseInt(str[1]), Integer.parseInt(str[2]));
      }
   }
}