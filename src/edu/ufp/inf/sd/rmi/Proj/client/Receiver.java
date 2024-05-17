package edu.ufp.inf.sd.rmi.Proj.client;

//recebe informações de todos os clientes
public class Receiver extends Thread {
   Player p;
   Game game;
   
//   Player fromWhichPlayerIs(int id) {
//      if (id == Client.id)
//         return game.you;
//      else if (id == (Client.id+1)%Const.QTY_PLAYERS)
//         return game.enemy1;
//      else if (id == (Client.id+2)%Const.QTY_PLAYERS)
//         return game.enemy2;
//      else if (id == (Client.id+3)%Const.QTY_PLAYERS)
//         return game.enemy3;
//      return null;
//   }
//
//   public void run() {
//      String str;
//      while (Client.in.hasNextLine()) {
//         this.p = fromWhichPlayerIs(Client.in.nextInt()); //id do edu.ufp.inf.sd.rmi.Proj.cliente
//         str = Client.in.next();
//
//         if (str.equals("mapUpdate")) { //p null
//            Game.setSpriteMap(Client.in.next(), Client.in.nextInt(), Client.in.nextInt());
//            game.you.panel.repaint();
//         }
//         else if (str.equals("newCoordinate")) {
//            p.x = Client.in.nextInt();
//            p.y = Client.in.nextInt();
//            game.you.panel.repaint();
//         }
//         else if (str.equals("newStatus")) {
//            p.sc.setLoopStatus(Client.in.next());
//         }
//         else if (str.equals("stopStatusUpdate")) {
//            p.sc.stopLoopStatus();
//         }
//         else if (str.equals("playerJoined")) {
//            p.alive = true;
//         }
//      }
//      Client.in.close();
//   }

   public Receiver(Game ganme){
      this.game = game;
   }
}