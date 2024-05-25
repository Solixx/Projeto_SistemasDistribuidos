package edu.ufp.inf.sd.rmi.Proj.client;

import javax.sound.midi.Soundbank;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Objects;

//recebe informações de todos os clientes
public class Receiver extends Thread implements Serializable {
   public Player p;
   public Game game;
   public ObserverRI observer;
   public User user;

   Player fromWhichPlayerIs(int id) throws RemoteException {
      return observer.findPlayer(id);
   }

   public void run() {
      String[] str = new String[0];
      while (true) {
         try {
            if (observer.getLastObserverState().getMsg() == null || Objects.equals(observer.getLastObserverState().getMsg(), "")) break;

            //System.out.println("rec obser: " + observer.getId());
            //System.out.println("rec obser: " + observer.getLastObserverState().getMsg());

            str = observer.getLastObserverState().getMsg().split(" ");

            if (str[0].isEmpty()) return;

            if (str[0].equals("StartGame")) {
               System.out.println("Userid: " + user.getId() + " State: " + observer.getLastObserverState().getMsg());
               Runnable runer = () -> {
                  try {
                     new Window(user.getGame(), observer);
                  } catch (InterruptedException e) {
                     throw new RuntimeException(e);
                  } catch (RemoteException e) {
                     throw new RuntimeException(e);
                  }
               };
               Thread t = new Thread(runer);
               t.start();
               //new Window(user.getGame(), this);
               return;
            }

            //System.out.println("lastState: " + lastObserverState.getMsg() + "userID: " + user.getId());

            if (!isNumeric(str[0])) return;

            Player p = user.getGame().findPlayer(Integer.parseInt(str[0]));


            switch (str[1]) {
               case "mapUpdate":  //p null
                  user.getGame().setSpriteMap(str[2], Integer.parseInt(str[3]), Integer.parseInt(str[4]), user);
                  //Game.setSpriteMap(Client.in.next(), Client.in.nextInt(), Client.in.nextInt(), user);
                  user.getGame().findPlayer(user.getId()).panel.repaint();
                  //game.you.panel.repaint();
                  break;
               case "newCoordinate":
                  p.x = Integer.parseInt(str[2]);
                  p.y = Integer.parseInt(str[3]);

                  user.getGame().findPlayer(user.getId()).panel.repaint();
                  //game.you.panel.repaint();
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

         } catch (Exception e) {

               System.out.println("rec obser: " + game.players);

            throw new RuntimeException(e);
         }
      }
       try {
           user.getObserver().getSubjectRI().detach(observer);
       } catch (RemoteException e) {
           throw new RuntimeException(e);
       }
       //Client.in.close();
   }

   public Receiver(Game game, ObserverRI observer, User user){
      this.game = game;
      this.observer = observer;
      this.user = user;
   }

   public static boolean isNumeric(String str) {
      if (str == null || str.isEmpty()) {
         return false;
      }
      try {
         Integer.parseInt(str);
         return true;
      } catch (NumberFormatException e) {
         return false;
      }
   }
}