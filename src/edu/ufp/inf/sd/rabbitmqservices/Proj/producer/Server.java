package edu.ufp.inf.sd.rabbitmqservices.Proj.producer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;


import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    public static void main(String[] args) {
        RabbitUtils.printArgs(args);

        String host=args[0];
        int port=Integer.parseInt(args[1]);
        String exchangeName=args[2];

        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel= RabbitUtils.createChannel2Server(connection)) {

            channel.exchangeDeclare(exchangeName,BuiltinExchangeType.TOPIC);
            System.out.println(" [x] Declare exchange '" + exchangeName + "' of type" + BuiltinExchangeType.TOPIC);

            String queueName = channel.queueDeclare().getQueue();
            System.out.println(" [x] Declare queue '" + queueName);
            String routingKey = "client.";
            channel.queueBind(queueName, exchangeName, routingKey);
            //System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received message: '" + message + "'");

                // Republish the message with routing key "server."
                channel.basicPublish(exchangeName, "server.", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
                System.out.println(" [x] Republished message with routing key 'server.'");
            };

            CancelCallback cancelCallback = consumerTag -> {
                System.out.println(" [x] Consumer Tag ['" + consumerTag + "] - Cancel callback invoked!'");
            };
            channel.basicConsume(queueName, true, deliverCallback, cancelCallback);

            // Keep the main thread running to listen for messages
            synchronized (Server.class) {
                try {
                    Server.class.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException | TimeoutException e) {
            Logger.getLogger(Server.class.getName()).log(Level.INFO, e.toString());
        }
    }
}

