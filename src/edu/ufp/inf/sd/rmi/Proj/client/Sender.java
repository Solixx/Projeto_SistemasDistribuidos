package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.State;

import java.awt.event.*;
import java.io.Serializable;
import java.rmi.RemoteException;

//escuta enquanto a janela (JFrame) estiver em foco
public class Sender extends KeyAdapter implements Serializable {
   int lastKeyCodePressed;
   public ObserverRI observer;

   public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_SPACE) {
         try {
            int x = observer.getUser().getGame().findPlayer(observer.getId()).getX();
            int y = observer.getUser().getGame().findPlayer(observer.getId()).getY();

            System.out.println("Sender bomba Uid: " + observer.getId() + " Pid " + observer.getUser().getGame().findPlayer(observer.getId()).getId());
            observer.getSubjectRI().setState(new State(observer.getId(), "pressedSpace " + x + " " + y));

         } catch (RemoteException | InterruptedException ex) {
            throw new RuntimeException(ex);
         }
      }
      else if (isNewKeyCode(e.getKeyCode())) {
         try {
            observer.getSubjectRI().setState(new State(observer.getId(), "keyCodePressed " + e.getKeyCode()));
         } catch (RemoteException | InterruptedException ex) {
            throw new RuntimeException(ex);
         }
      }
   }

   public void keyReleased(KeyEvent e) {
      try {
         observer.getSubjectRI().setState(new State(observer.getId(), "keyCodeReleased " + e.getKeyCode()));
      } catch (RemoteException | InterruptedException ex) {
         throw new RuntimeException(ex);
      }
      lastKeyCodePressed = -1; //a próxima tecla sempre será nova
   }

   boolean isNewKeyCode(int keyCode) {
      boolean ok = (keyCode != lastKeyCodePressed) ? true : false;
      lastKeyCodePressed = keyCode;
      return ok;
   }

   public Sender(ObserverRI observer){
       this.observer = observer;
   }
}