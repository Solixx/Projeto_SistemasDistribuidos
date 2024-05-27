package edu.ufp.inf.sd.rabbitmqservices.Proj.consumer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;


import javax.swing.*;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Logger;

class PlayerData implements Serializable {
   boolean logged, alive;
   int x, y; //coordenada atual
   int numberOfBombs;

   PlayerData(int x, int y) {
      this.x = x;
      this.y = y;
      this.logged = false;
      this.alive = true;
      this.numberOfBombs = 1; // para 2 bombas, é preciso tratar cada bomba em uma thread diferente
   }
}

public class Client implements Serializable {
   private Receiver receiver;
   private int id;
   static int currId = 0;

   final static int rateStatusUpdate = 115;
   public Coordinate map[][] = new Coordinate[Const.LIN][Const.COL];

   private Coordinate spawn[] = new Coordinate[Const.QTY_PLAYERS];
   private boolean alive[] = new boolean[Const.QTY_PLAYERS];

   private PlayerData player[] = new PlayerData[Const.QTY_PLAYERS];
   private Game game;

   private Channel channelToRabbitMq;
   private String exchangeName;
   private BuiltinExchangeType exchangeType;
   private String exchangeBindingKeys;
   private String messageFormat;
   private HashMap<String, Integer> waitingList = new HashMap<>();
   public String nameQueue;

   Client(String args[]) {
      this.id = Integer.parseInt(args[3]);
      System.out.print("Estabelecendo conexão com o servidor...");

      System.out.print(" ok\n");

      setMap();
      setPlayerData();

      this.spawn[0] = new Coordinate(0, 0);
      this.spawn[1] = new Coordinate(0, Const.COL - 1);
      this.spawn[2] = new Coordinate(Const.LIN - 1, 0);
      this.spawn[3] = new Coordinate(Const.LIN - 1, Const.COL - 1);

      
      receiveInitialSettings();

      startQueue(args);

      Runnable r = () -> {
         try {
            new Window(this);
         } catch (InterruptedException | IOException e) {
            e.printStackTrace();
         }
      };
      new Thread(r).start();
   }

   void receiveInitialSettings() {
      //mapa
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, map[i][j].img);

      //situação (vivo ou morto) inicial de todos os jogadores
      for (int i = 0; i < player.length; i++)
         this.alive[i] = player[i].alive;

      //coordenadas inicias de todos os jogadores
      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         spawn[i] = new Coordinate(player[i].x , player[i].y);
   }

   void setMap() {
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, "block");

      // paredes fixas das bordas
      for (int j = 1; j < Const.COL - 1; j++) {
         map[0][j].img = "wall-center";
         map[Const.LIN - 1][j].img = "wall-center";
      }
      for (int i = 1; i < Const.LIN - 1; i++) {
         map[i][0].img = "wall-center";
         map[i][Const.COL - 1].img = "wall-center";
      }
      map[0][0].img = "wall-up-left";
      map[0][Const.COL - 1].img = "wall-up-right";
      map[Const.LIN - 1][0].img = "wall-down-left";
      map[Const.LIN - 1][Const.COL - 1].img = "wall-down-right";

      // paredes fixas centrais
      for (int i = 2; i < Const.LIN - 2; i++)
         for (int j = 2; j < Const.COL - 2; j++)
            if (i % 2 == 0 && j % 2 == 0)
               map[i][j].img = "wall-center";

      // arredores do spawn
      map[1][1].img = "floor-1";
      map[1][2].img = "floor-1";
      map[2][1].img = "floor-1";
      map[Const.LIN - 2][Const.COL - 2].img = "floor-1";
      map[Const.LIN - 3][Const.COL - 2].img = "floor-1";
      map[Const.LIN - 2][Const.COL - 3].img = "floor-1";
      map[Const.LIN - 2][1].img = "floor-1";
      map[Const.LIN - 3][1].img = "floor-1";
      map[Const.LIN - 2][2].img = "floor-1";
      map[1][Const.COL - 2].img = "floor-1";
      map[2][Const.COL - 2].img = "floor-1";
      map[1][Const.COL - 3].img = "floor-1";
   }

   void setPlayerData() {
      player[0] = new PlayerData(
              map[1][1].x - Const.VAR_X_SPRITES,
              map[1][1].y - Const.VAR_Y_SPRITES
      );

      player[1] = new PlayerData(
              map[Const.LIN - 2][Const.COL - 2].x - Const.VAR_X_SPRITES,
              map[Const.LIN - 2][Const.COL - 2].y - Const.VAR_Y_SPRITES
      );
      player[2] = new PlayerData(
              map[Const.LIN - 2][1].x - Const.VAR_X_SPRITES,
              map[Const.LIN - 2][1].y - Const.VAR_Y_SPRITES
      );
      player[3] = new PlayerData(
              map[1][Const.COL - 2].x - Const.VAR_X_SPRITES,
              map[1][Const.COL - 2].y - Const.VAR_Y_SPRITES
      );
   }

   public void startQueue(String args[]) {
      try {
         RabbitUtils.printArgs(args);

         String host=args[0];
         int port=Integer.parseInt(args[1]);
         exchangeName=args[2];
         System.out.println("exchangeName: " + exchangeName);

         // Open a connection and a channel to rabbitmq broker/server
         Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
         channelToRabbitMq=RabbitUtils.createChannel2Server(connection);

         //Declare queue from which to consume (declare it also here, because consumer may start before publisher)
         //channel.queueDeclare(queueName, false, false, false, null);
         channelToRabbitMq.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);

         nameQueue = channelToRabbitMq.queueDeclare().getQueue();
         System.out.println("\nQueueName: " + nameQueue);

         String routingKey= "server.";
         channelToRabbitMq.queueBind(nameQueue,exchangeName,routingKey);
         System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//DeliverCallback is a handler callback (lambda method) to consume messages pushed by the sender.
         //Create a handler callback to receive messages from queue
         DeliverCallback deliverCallback=(consumerTag, delivery) -> {
            String message=new String(delivery.getBody(), "UTF-8");
            String routingKey2 = delivery.getEnvelope().getRoutingKey();
            System.out.println(" [x] Received message: '" + message + "'");
            // Check the routing key
            if (routingKey2.equals("client.")) {
               // Ignore the message
               System.out.println(" [x] Ignoring message with routing key 'client.'");
            } else if (routingKey2.equals("server.")) {
               receiver.run(message);
            }else {
               System.out.println(" [x] Received '" + message + "'");
               // Republish the message with routing key "server
            }
         };
         CancelCallback callback=(consumerTag) ->{
            System.out.println(" [x] Consumer Tag ['" + consumerTag + "] - Cancel callback invoked!'");
         };

         channelToRabbitMq.basicConsume(nameQueue, true, deliverCallback, callback);

      } catch (Exception e) {
         System.out.println("erro" + e);
      }
   }

   public void sendMensage(String messageContent, String routingKey) throws IOException {
      channelToRabbitMq.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, messageContent.getBytes("UTF-8"));
   }

   public Coordinate[][] getMap() {
      return map;
   }

   public void setMap(Coordinate[][] map) {
      this.map = map;
   }

   public Coordinate[] getSpawn() {
      return spawn;
   }

   public void setSpawn(Coordinate[] spawn) {
      this.spawn = spawn;
   }

   public boolean[] getAlive() {
      return alive;
   }

   public void setAlive(boolean[] alive) {
      this.alive = alive;
   }

   public PlayerData[] getPlayer() {
      return player;
   }

   public void setPlayer(PlayerData[] player) {
      this.player = player;
   }

   public int getId() {
      return id;
   }

   public Receiver getReceiver() {
      return receiver;
   }

   public void setReceiver(Receiver receiver) {
      this.receiver = receiver;
   }

   public Game getGame() {
      return game;
   }

   public void setGame(Game game) {
      this.game = game;
   }

   public static void main(String[] args) throws RemoteException {
      new Client(args);
   }
}

class Window extends JFrame implements Serializable {
   private static final long serialVersionUID = 1L;

   Window(Client client) throws IOException, InterruptedException {
      Sprite.loadImages();
      Sprite.setMaxLoopStatus();

      Game game = new Game(Const.COL*Const.SIZE_SPRITE_MAP, Const.LIN*Const.SIZE_SPRITE_MAP, client);
      client.setGame(game);
      client.setReceiver(new Receiver(game, client));
      add(game);
      setTitle("bomberman "+client.getId());
      pack();
      setVisible(true);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      addKeyListener(new Sender(client));
   }
}