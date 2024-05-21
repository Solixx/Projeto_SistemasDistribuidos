package edu.ufp.inf.sd.rmi.Proj.client;

import com.sun.javafx.collections.MappingChange;
import edu.ufp.inf.sd.rmi.Proj.server.State;

import java.io.Serializable;
import java.rmi.RemoteException;

//thread que lança mudanças graduais no mapa que ocorrem logo após a bomba ser plantada
class MapUpdatesThrower extends Thread implements Serializable {
   boolean bombPlanted;
   int id, l, c;
   public ObserverRI observer;
   public Game game;
   public PlayerData player;

   MapUpdatesThrower(int id, ObserverRI observer, Game game) {
      this.id = id;
      this.bombPlanted = false;
      this.observer = observer;
      this.game = game;
      this.player = game.findPlayerData(id);
   }

   void setBombPlanted(int x, int y) {
      x += Const.WIDTH_SPRITE_PLAYER / 2;
      y += 2 * Const.HEIGHT_SPRITE_PLAYER / 3;

      this.c = x/Const.SIZE_SPRITE_MAP;   
      this.l = y/Const.SIZE_SPRITE_MAP;

      this.bombPlanted = true;
   }

   //muda o mapa no servidor e no edu.ufp.inf.sd.rmi.Proj.cliente
   public void changeMap(String keyWord, int l, int c) throws RemoteException, InterruptedException {
      game.map[l][c].img = keyWord;
      observer.getSubjectRI().setState(new edu.ufp.inf.sd.rmi.Proj.server.State(observer.getId(), "-1 mapUpdate " + keyWord + " " + l + " " + c));
      //ClientManager.sendToAllClients("-1 mapUpdate " + keyWord + " " + l + " " + c);
   }

   int getColumnOfMap(int x) {
      return x / Const.SIZE_SPRITE_MAP;
   }
   int getLineOfMap(int y) {
      return y / Const.SIZE_SPRITE_MAP;
   }

   // verifica se o fogo atingiu algum jogador parado (coordenada do centro do corpo)
   void checkIfExplosionKilledSomeone(int linSprite, int colSprite) throws RemoteException, InterruptedException {
      int linPlayer, colPlayer, x, y;

      for (int id = 0; id < Const.QTY_PLAYERS; id++)
         if (player.alive) {
            x = player.x + Const.WIDTH_SPRITE_PLAYER / 2;
            y = player.y + 2 * Const.HEIGHT_SPRITE_PLAYER / 3;
   
            colPlayer = getColumnOfMap(x);
            linPlayer = getLineOfMap(y);
   
            if (linSprite == linPlayer && colSprite == colPlayer) {
               player.alive = false;
               observer.getSubjectRI().setState(new edu.ufp.inf.sd.rmi.Proj.server.State(observer.getId(), observer.getId() + " newStatus dead"));
               //ClientManager.sendToAllClients(id + " newStatus dead");
            }
         }
   }

   public void run() {
      while (true) {
         if (bombPlanted) {
            try{
               bombPlanted = false;

               for (String index: Const.indexBombPlanted) {
                  changeMap("bomb-planted-" + index, l, c);
                  try {
                     sleep(Const.RATE_BOMB_UPDATE);
                  } catch (InterruptedException e) {}
               }

               //efeitos da explosão
               new Thrower("center-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c, this).start();
               checkIfExplosionKilledSomeone(l, c);

               //abaixo
               if (game.map[l+1][c].img.equals("floor-1")) {
                  new Thrower("down-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l+1, c, this).start();
                  checkIfExplosionKilledSomeone(l+1, c);
               }
               else if (game.map[l+1][c].img.contains("block"))
                  new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l+1, c, this).start();

               //a direita
               if (game.map[l][c+1].img.equals("floor-1")) {
                  new Thrower("right-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c+1, this).start();
                  checkIfExplosionKilledSomeone(l, c+1);
               }
               else if (game.map[l][c+1].img.contains("block"))
                  new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l, c+1, this).start();

               //acima
               if (game.map[l-1][c].img.equals("floor-1")) {
                  new Thrower("up-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l-1, c, this).start();
                  checkIfExplosionKilledSomeone(l-1, c);
               }
               else if (game.map[l-1][c].img.contains("block"))
                  new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l-1, c, this).start();

               //a esquerda
               if (game.map[l][c-1].img.equals("floor-1")) {
                  new Thrower("left-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c-1, this).start();
                  checkIfExplosionKilledSomeone(l, c-1);
               }
               else if (game.map[l][c-1].img.contains("block"))
                  new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l, c-1, this).start();

               player.numberOfBombs++; //libera bomba
            } catch (RemoteException | InterruptedException e) {
               throw new RuntimeException(e);
            }

         }
         try {sleep(0);} catch (InterruptedException e) {}
      }
   }
}

//thread auxiliar
class Thrower extends Thread implements Serializable {
   String keyWord, index[];
   int l, c;
   int delay;
   MapUpdatesThrower mapUpdatesThrower;

   Thrower(String keyWord, String index[], int delay, int l, int c, MapUpdatesThrower mapUpdatesThrower) {
      this.keyWord = keyWord;
      this.index = index;
      this.delay = delay;
      this.l = l;
      this.c = c;
      this.mapUpdatesThrower = mapUpdatesThrower;
   }

   public void run() {
      try{

         for (String i : index) {
            this.mapUpdatesThrower.changeMap(keyWord + "-" + i, l, c);
            try {
               sleep(delay);
            } catch (InterruptedException e) {}
         }
         //situação pós-explosão
         this.mapUpdatesThrower.changeMap("floor-1", l, c);

      } catch (RemoteException | InterruptedException e) {
         throw new RuntimeException(e);
      }

   }
}