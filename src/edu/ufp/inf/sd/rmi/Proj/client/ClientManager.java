package edu.ufp.inf.sd.rmi.Proj.client;

import edu.ufp.inf.sd.rmi.Proj.server.State;
import edu.ufp.inf.sd.rmi.Proj.server.SubjectRI;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.*;

//a cada edu.ufp.inf.sd.rmi.Proj.cliente que entra no servidor, uma nova thread é instanciada para tratá-lo
class ClientManager extends Thread implements Serializable {
   //static List<PrintStream> listOutClients = new ArrayList<PrintStream>();
   private Game game;

   //private Socket clientSocket = null;
   private ObserverRI observer = null;
   //private Scanner in = null;
   //private PrintStream out = null;
   private edu.ufp.inf.sd.rmi.Proj.server.State in = null;
   private int id;

   CoordinatesThrower ct;
   MapUpdatesThrower mt;

//   ClientManager(Socket clientSocket, int id) {
//      this.id = id;
//      this.clientSocket = clientSocket;
//      (ct = new CoordinatesThrower(this.id)).start();
//      (mt = new MapUpdatesThrower(this.id)).start();
//
//      try {
//         System.out.print("Iniciando conexão com o jogador " + this.id + "...");
//         this.in = new Scanner(clientSocket.getInputStream()); // para receber do edu.ufp.inf.sd.rmi.Proj.cliente
//         this.out = new PrintStream(clientSocket.getOutputStream(), true); // para enviar ao edu.ufp.inf.sd.rmi.Proj.cliente
//      } catch (IOException e) {
//         System.out.println(" erro: " + e + "\n");
//         System.exit(1);
//      }
//      System.out.print(" ok\n");
//
//      listOutClients.add(out);
//      Server.player[id].logged = true;
//      Server.player[id].alive = true;
//      sendInitialSettings(); // envia uma única string
//
//      //notifica aos clientes já logados
//      for (PrintStream outClient: listOutClients)
//         if (outClient != this.out)
//            outClient.println(id + " playerJoined");
//   }

   ClientManager(ObserverRI observer, int id, Game game) throws RemoteException, InterruptedException {
      this.id = id;
      this.observer = observer;
      this.game = game;
      //(ct = new CoordinatesThrower(this.id, this.observer, this.game).start());
      ct = new CoordinatesThrower(this.id, this.observer, this.game);
      ct.start();
      (mt = new MapUpdatesThrower(this.id, this.observer, this.game)).start();

      try {
         System.out.print("Iniciando conexão com o jogador " + this.id + "...");
         //System.out.println("Observer CM:" + observer.getId());
         this.in = observer.getLastObserverState(); // para receber do edu.ufp.inf.sd.rmi.Proj.cliente
      } catch (IOException e) {
         System.out.println(" erro: " + e + "\n");
         System.exit(1);
      }
      System.out.print(" ok\n");

      game.findPlayerData(id).logged = true;
      game.findPlayerData(id).alive = true;
      sendInitialSettings(game); // envia uma única string

      //notifica aos clientes já logados
      for (ObserverRI obs: this.game.subjectRI.getObservers()){
         if (obs.getUser().equals(observer.getUser())){
            obs.getSubjectRI().setState(new edu.ufp.inf.sd.rmi.Proj.server.State(obs.getUser().getId(), obs.getUser().getId()+" playerJoined"));
            game.loggedIsFull();
         }
      }
   }

   public void run() {
      while (true) {
         try {
            if (observer.getLastObserverState().getMsg() == null || Objects.equals(observer.getLastObserverState().getMsg(), "")) break;

            String str[] = observer.getLastObserverState().getMsg().split(" ");

            if (str[0].equals("keyCodePressed") && game.findPlayerData(id).alive) {
               ct.keyCodePressed(Integer.parseInt(str[1]));
            }
            else if (str[0].equals("keyCodeReleased") && game.findPlayerData(id).alive) {
               ct.keyCodeReleased(Integer.parseInt(str[1]));
            }
            else if (str[0].equals("pressedSpace") && game.findPlayerData(id).numberOfBombs >= 1) {
               game.findPlayerData(id).numberOfBombs--;
               mt.setBombPlanted(Integer.parseInt(str[1]), Integer.parseInt(str[2]));
            }
         } catch (RemoteException | InterruptedException e) {
            throw new RuntimeException(e);
         }
      }
      try {
         clientDesconnected();
      } catch (RemoteException e) {
         throw new RuntimeException(e);
      }
   }

//   public void sendToAllClients(String outputLine) {
//      this.game.subjectRI.notifyAll();
//      for (PrintStream outClient : listOutClients)
//         outClient.println(outputLine);
//   }

   void sendInitialSettings(Game game) throws RemoteException, InterruptedException {
      //out.print(id);
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            this.observer.getSubjectRI().setState(new edu.ufp.inf.sd.rmi.Proj.server.State(observer.getId(), " " + game.map[i][j].img));
            //out.print(" " + Server.map[i][j].img);

      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         this.observer.getSubjectRI().setState(new edu.ufp.inf.sd.rmi.Proj.server.State(observer.getId()," " + game.playerData[i].alive));
         //out.print(" " + Server.player[i].alive);

      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         this.observer.getSubjectRI().setState(new edu.ufp.inf.sd.rmi.Proj.server.State(observer.getId(), " "+game.playerData[i].x + " "+game.playerData[i].y));
         //out.print(" " + Server.player[i].x + " " + Server.player[i].y);
      //out.print("\n");
   }

   void clientDesconnected() throws RemoteException {
      this.game.subjectRI.detach(observer);
      //listOutClients.remove(out);
      this.game.findPlayerData(id).logged = false;
      //Server.player[id].logged = false;
      //try {
         System.out.print("Encerrando conexão com o jogador " + this.id + "...");
         //in.close();
         //out.close();
         //clientSocket.close();
      //} catch (IOException e) {
         //System.out.println(" erro: " + e + "\n");
         //System.exit(1);
      //}
      System.out.print(" ok\n");
   }
}