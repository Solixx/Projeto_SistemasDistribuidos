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

            if(str[0].isEmpty()){
               continue;
            }

            if(str[0].equals("StartGame")){
               new Window(user.getGame(), user.getGame().findObserver(user.getId()));
               continue;
            }

            System.out.println("str[0]: " + str[0]);

            this.p = fromWhichPlayerIs(Integer.parseInt(str[0]));


            if (str[1].equals("mapUpdate")) { //p null
               game.setSpriteMap(str[2], Integer.parseInt(str[3]), Integer.parseInt(str[4]), user);
               //Game.setSpriteMap(Client.in.next(), Client.in.nextInt(), Client.in.nextInt(), user);
               game.findPlayer(user.getId()).panel.repaint();
               //game.you.panel.repaint();
            }
            else if (str[1].equals("newCoordinate")) {
               p.x = Integer.parseInt(str[2]);
               p.y = Integer.parseInt(str[3]);
               game.findPlayer(user.getId()).panel.repaint();
               //game.you.panel.repaint();
            }
            else if (str[1].equals("newStatus")) {
               p.sc.setLoopStatus(str[2]);
            }
            else if (str[1].equals("stopStatusUpdate")) {
               p.sc.stopLoopStatus();
            }
            else if (str[1].equals("playerJoined")) {
               p.alive = true;
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
}