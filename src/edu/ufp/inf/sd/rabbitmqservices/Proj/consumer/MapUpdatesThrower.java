package edu.ufp.inf.sd.rabbitmqservices.Proj.consumer;

import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;

//thread que lança mudanças graduais no mapa que ocorrem logo após a bomba ser plantada
class MapUpdatesThrower extends Thread implements Serializable {
   boolean bombPlanted;
   int id, l, c;

   Client client;

   MapUpdatesThrower(int id, Client client) throws RemoteException {
      this.id = id;
      this.bombPlanted = false;
      this.client = client;
   }

   void setBombPlanted(int x, int y) {
      x += Const.WIDTH_SPRITE_PLAYER / 2;
      y += 2 * Const.HEIGHT_SPRITE_PLAYER / 3;

      this.c = x/ Const.SIZE_SPRITE_MAP;
      this.l = y/ Const.SIZE_SPRITE_MAP;

      this.bombPlanted = true;
   }

   //muda o mapa no servidor e no cliente
   public void changeMap(String keyWord, int l, int c) throws IOException, InterruptedException {
      client.map[l][c].img = keyWord;
      client.sendMensage("-1 mapUpdate " + keyWord + " " + l + " " + c, "client.");
      //ClientManager.sendToAllClients("-1 mapUpdate " + keyWord + " " + l + " " + c);
   }

   int getColumnOfMap(int x) {
      return x / Const.SIZE_SPRITE_MAP;
   }
   int getLineOfMap(int y) {
      return y / Const.SIZE_SPRITE_MAP;
   }

   // verifica se o fogo atingiu algum jogador parado (coordenada do centro do corpo)
   void checkIfExplosionKilledSomeone(int linSprite, int colSprite) throws IOException, InterruptedException {
      int linPlayer, colPlayer, x, y;

      for (int id = 0; id < Const.QTY_PLAYERS; id++)
         if (client.getPlayer()[id].alive) {
            x = client.getPlayer()[id].x + Const.WIDTH_SPRITE_PLAYER / 2;
            y = client.getPlayer()[id].y + 2 * Const.HEIGHT_SPRITE_PLAYER / 3;
   
            colPlayer = getColumnOfMap(x);
            linPlayer = getLineOfMap(y);
   
            if (linSprite == linPlayer && colSprite == colPlayer) {
               client.getPlayer()[id].alive = false;
               client.sendMensage(id + " newStatus dead", "client.");
               //ClientManager.sendToAllClients(id + " newStatus dead");
            }
         }
   }

   public void run() {
      while (true) {
         if (bombPlanted) {
            bombPlanted = false;

            for (String index: Const.indexBombPlanted) {
               try {
                  changeMap("bomb-planted-" + index, l, c);
               } catch (InterruptedException | IOException e) {
                  throw new RuntimeException(e);
               }
               try {
                  sleep(Const.RATE_BOMB_UPDATE);
               } catch (InterruptedException e) {}
            }

            try{
               //efeitos da explosão
               new Thrower("center-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c, this).start();
               checkIfExplosionKilledSomeone(l, c);

               //abaixo
               if (client.map[l+1][c].img.equals("floor-1")) {
                  new Thrower("down-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l+1, c, this).start();
                  checkIfExplosionKilledSomeone(l+1, c);
               }
               else if (client.map[l+1][c].img.contains("block"))
                  new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l+1, c, this).start();

               //a direita
               if (client.map[l][c+1].img.equals("floor-1")) {
                  new Thrower("right-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c+1, this).start();
                  checkIfExplosionKilledSomeone(l, c+1);
               }
               else if (client.map[l][c+1].img.contains("block"))
                  new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l, c+1, this).start();

               //acima
               if (client.map[l-1][c].img.equals("floor-1")) {
                  new Thrower("up-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l-1, c, this).start();
                  checkIfExplosionKilledSomeone(l-1, c);
               }
               else if (client.map[l-1][c].img.contains("block"))
                  new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l-1, c, this).start();

               //a esquerda
               if (client.map[l][c-1].img.equals("floor-1")) {
                  new Thrower("left-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c-1, this).start();
                  checkIfExplosionKilledSomeone(l, c-1);
               }
               else if (client.map[l][c-1].img.contains("block"))
                  new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l, c-1, this).start();

               client.getPlayer()[id].numberOfBombs++; //libera bomba
            } catch (InterruptedException | IOException e) {
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
   MapUpdatesThrower mt;

   Thrower(String keyWord, String index[], int delay, int l, int c, MapUpdatesThrower mp) {
      this.keyWord = keyWord;
      this.index = index;
      this.delay = delay;
      this.l = l;
      this.c = c;
      this.mt = mp;
   }

   public void run() {
      for (String i : index) {
         try {
            mt.changeMap(keyWord + "-" + i, l, c);
         } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
         }
         try {
            sleep(delay);
         } catch (InterruptedException e) {}
      }
      //situação pós-explosão
      try {
         mt.changeMap("floor-1", l, c);
      } catch (InterruptedException | IOException e) {
         throw new RuntimeException(e);
      }
   }
}