package edu.ufp.inf.sd.rabbitmqservices.Proj.producer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;


import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    public void startQueue(String host, int port, String username, String passwrd, String exchangeName) {
        try {
            Connection connection=RabbitUtils.newConnection2Server(host, port, username, passwrd);
            Channel channel= RabbitUtils.createChannel2Server(connection);

            channel.exchangeDeclare(exchangeName,BuiltinExchangeType.TOPIC);
            System.out.println(" [x] Declare exchange '" + exchangeName + "' of type" + BuiltinExchangeType.TOPIC);

            String nameQueue = channel.queueDeclare().getQueue();
            System.out.println(" [x] Declare queue '" + nameQueue);

            String routingKey = "client.";
            channel.queueBind(nameQueue, exchangeName, routingKey);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String msgRoutingKey = delivery.getEnvelope().getRoutingKey();
                System.out.println(" [x] Received message: '" + message + "'");

                if(msgRoutingKey.equals("client.")){
                    channel.basicPublish(exchangeName, "server.", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
                    System.out.println(" [x] Republished message with routing key 'server.'");
                } else if(msgRoutingKey.equals("server.")){
                    System.out.println(" [x] Ignoring message with routing key 'server.'");
                } else{
                    System.out.println(" [x] Received '" + message + "'");
                }
            };

            CancelCallback cancelCallback = consumerTag -> {
                System.out.println(" [x] Consumer Tag ['" + consumerTag + "] - Cancel callback invoked!'");
            };
            channel.basicConsume(nameQueue, true, deliverCallback, cancelCallback);
        } catch (IOException | TimeoutException e) {
            Logger.getLogger(Server.class.getName()).log(Level.INFO, e.toString());
        }
    }

    public static void main(String[] args) {
        RabbitUtils.printArgs(args);

        String host=args[0];
        int port=Integer.parseInt(args[1]);
        String exchangeName=args[2];

        Server server = new Server();
        server.startQueue(host, port, "guest", "guest", exchangeName);

    }
}

