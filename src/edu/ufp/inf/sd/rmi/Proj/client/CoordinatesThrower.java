package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.rmi.RemoteException;

//thread que dispara as coordenadas seguintes aos clientes enquanto W/A/S/D não é solto
class CoordinatesThrower extends Thread implements Serializable {
   boolean up, right, left, down;
   int id;
   Client client;
   SubjectRI subjectRI;

   CoordinatesThrower(int id, Client client) throws RemoteException {
      this.id = id;
      this.client = client;
      up = down = right = left = false;
      this.subjectRI = client.getObserverRI().getSubjectRI();
   }

   public void run() {
      int newX = client.getPlayer()[id].x;
      int newY = client.getPlayer()[id].y;
      
      while (true) {
         if (up || down || right || left) {
            if (up)           newY = client.getPlayer()[id].y - Const.RESIZE;
            else if (down)    newY = client.getPlayer()[id].y + Const.RESIZE;
            else if (right)   newX = client.getPlayer()[id].x + Const.RESIZE;
            else if (left)    newX = client.getPlayer()[id].x - Const.RESIZE;
            //System.out.println("CT: newX: " + newX + " newY: " + newY);
            try {
               if (coordinateIsValid(newX, newY)) {
                  try {
                     subjectRI.setState(new edu.ufp.inf.sd.rmi.Proj.server.State(client.getId(), id + " newCoordinate " + newX + " " + newY));
                     //System.out.println("CT: " + id + " newCoordinate " + newX + " " + newY);
                  } catch (RemoteException e) {
                     throw new RuntimeException(e);
                  } catch (InterruptedException e) {
                     throw new RuntimeException(e);
                  }
                  //ClientManager.sendToAllClients(id + " newCoordinate " + newX + " " + newY);

                  client.getPlayer()[id].x = newX;
                  client.getPlayer()[id].y = newY;
               } else {
                  newX = client.getPlayer()[id].x;
                  newY = client.getPlayer()[id].y;
               }
            } catch (RemoteException | InterruptedException e) {
               throw new RuntimeException(e);
            }
            try {
               sleep(Const.RATE_COORDINATES_UPDATE);
            } catch (InterruptedException e) {}
         }
         try {sleep(0);} catch (InterruptedException e) {}
      }
   }

   int getColumnOfMap(int x) {
      return x/Const.SIZE_SPRITE_MAP;
   }
   int getLineOfMap(int y) {
      return y/Const.SIZE_SPRITE_MAP;
   }

   // encontra sobre quais sprites do mapa o jogador está e verifica se são válidos
   boolean coordinateIsValid(int newX, int newY) throws RemoteException, InterruptedException {
      if (!client.getPlayer()[id].alive)
         return false;

      //verifica se o jogador foi para o fogo (coordenada do centro do corpo)
      int xBody = newX + Const.WIDTH_SPRITE_PLAYER/2;
      int yBody = newY + 2*Const.HEIGHT_SPRITE_PLAYER/3;

      if (client.map[getLineOfMap(yBody)][getColumnOfMap(xBody)].img.contains("explosion")) {
         client.getPlayer()[id].alive = false;
         subjectRI.setState(new edu.ufp.inf.sd.rmi.Proj.server.State(client.getId(), id + " newStatus dead"));
         //ClientManager.sendToAllClients(id + " newStatus dead");
         return true;
      }
      
      int x[] = new int[4], y[] = new int[4];
      int c[] = new int[4], l[] = new int[4];


      // EM RELAÇÃO À NOVA COORDENADA

      // 0: ponto do canto superior esquerdo
      x[0] = Const.VAR_X_SPRITES + newX + Const.RESIZE;
      y[0] = Const.VAR_Y_SPRITES + newY + Const.RESIZE;
      // 1: ponto do canto superior direito
      x[1] = Const.VAR_X_SPRITES + newX + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      y[1] = Const.VAR_Y_SPRITES + newY + Const.RESIZE;
      // 2: ponto do canto inferior esquerdo
      x[2] = Const.VAR_X_SPRITES + newX + Const.RESIZE;
      y[2] = Const.VAR_Y_SPRITES + newY + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      // 3: ponto do canto inferior direito
      x[3] = Const.VAR_X_SPRITES + newX + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      y[3] = Const.VAR_Y_SPRITES + newY + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      
      for (int i = 0; i < 4; i++) { 
         c[i] = getColumnOfMap(x[i]);
         l[i] = getLineOfMap(y[i]);
      }

      if (
         (client.map[l[0]][c[0]].img.equals("floor-1") || client.map[l[0]][c[0]].img.contains("explosion")) &&
         (client.map[l[1]][c[1]].img.equals("floor-1") || client.map[l[1]][c[1]].img.contains("explosion")) &&
         (client.map[l[2]][c[2]].img.equals("floor-1") || client.map[l[2]][c[2]].img.contains("explosion")) &&
         (client.map[l[3]][c[3]].img.equals("floor-1") || client.map[l[3]][c[3]].img.contains("explosion"))
      ) 
         return true; //estará em uma coordenada válida

      if (
         (client.map[l[0]][c[0]].img.contains("block") || client.map[l[0]][c[0]].img.contains("wall")) ||
         (client.map[l[1]][c[1]].img.contains("block") || client.map[l[1]][c[1]].img.contains("wall")) ||
         (client.map[l[2]][c[2]].img.contains("block") || client.map[l[2]][c[2]].img.contains("wall")) ||
         (client.map[l[3]][c[3]].img.contains("block") || client.map[l[3]][c[3]].img.contains("wall"))
      ) 
         return false; //estará sobre uma parede



      // EM RELAÇÃO À COORDENADA ANTERIOR

      // 0: ponto do canto superior esquerdo
      x[0] = Const.VAR_X_SPRITES + client.getPlayer()[id].x + Const.RESIZE;
      y[0] = Const.VAR_Y_SPRITES + client.getPlayer()[id].y + Const.RESIZE;
      // 1: ponto do canto superior direito
      x[1] = Const.VAR_X_SPRITES + client.getPlayer()[id].x + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      y[1] = Const.VAR_Y_SPRITES + client.getPlayer()[id].y + Const.RESIZE;
      // 2: ponto do canto inferior esquerdo
      x[2] = Const.VAR_X_SPRITES + client.getPlayer()[id].x + Const.RESIZE;
      y[2] = Const.VAR_Y_SPRITES + client.getPlayer()[id].y + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      // 3: ponto do canto inferior direito
      x[3] = Const.VAR_X_SPRITES + client.getPlayer()[id].x + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      y[3] = Const.VAR_Y_SPRITES + client.getPlayer()[id].y + Const.SIZE_SPRITE_MAP - 2 * Const.RESIZE;
      
      for (int i = 0; i < 4; i++) { 
         c[i] = getColumnOfMap(x[i]);
         l[i] = getLineOfMap(y[i]);
      }

      if (
         client.map[l[0]][c[0]].img.contains("bomb-planted") ||
         client.map[l[1]][c[1]].img.contains("bomb-planted") ||
         client.map[l[2]][c[2]].img.contains("bomb-planted") ||
         client.map[l[3]][c[3]].img.contains("bomb-planted")
      ) 
         return true; //estava sobre uma bomba que acabou de platar, precisa sair
      
      return false;
   }

   void keyCodePressed(int keyCode) throws RemoteException, InterruptedException {
      switch (keyCode) {
         case KeyEvent.VK_W: 
            up = true; down = right = left = false;
            subjectRI.setState(new edu.ufp.inf.sd.rmi.Proj.server.State(client.getId(), this.id + " newStatus up"));
            //ClientManager.sendToAllClients(this.id + " newStatus up");
            break;
         case KeyEvent.VK_S: 
            down = true; up = right = left = false;
            subjectRI.setState(new edu.ufp.inf.sd.rmi.Proj.server.State(client.getId(), this.id + " newStatus down"));
            //ClientManager.sendToAllClients(this.id + " newStatus down");
            break;
         case KeyEvent.VK_D: 
            right = true; up = down = left = false;
            subjectRI.setState(new edu.ufp.inf.sd.rmi.Proj.server.State(client.getId(), this.id + " newStatus right"));
            //ClientManager.sendToAllClients(this.id + " newStatus right");
            break;
         case KeyEvent.VK_A: 
            left = true; up = down = right = false;
            subjectRI.setState(new edu.ufp.inf.sd.rmi.Proj.server.State(client.getId(), this.id + " newStatus left"));
            //ClientManager.sendToAllClients(this.id + " newStatus left");
            break;
      }
   }

   void keyCodeReleased(int keyCode) throws RemoteException, InterruptedException {
      if (keyCode != KeyEvent.VK_W && keyCode != KeyEvent.VK_S && keyCode != KeyEvent.VK_D && keyCode != KeyEvent.VK_A)
         return;

      subjectRI.setState(new edu.ufp.inf.sd.rmi.Proj.server.State(client.getId(), this.id + " stopStatusUpdate"));
      //ClientManager.sendToAllClients(this.id + " stopStatusUpdate");
      switch (keyCode) {
         case KeyEvent.VK_W: up = false; break;
         case KeyEvent.VK_S: down = false; break;
         case KeyEvent.VK_D: right = false; break;
         case KeyEvent.VK_A: left = false; break;
      }
   }
}