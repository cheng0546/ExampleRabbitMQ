package com.shopping.rabbitmq.consumer;

import com.rabbitmq.client.*;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class EmailSystem {
    private final static String EXCHANGE_NAME = "shopping_logs";

    public static void sendEmail(String user, String email) throws SendMailException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        OhMyEmail.config(OhMyEmail.SMTP_QQ(false), "EMAIL", "PASSWORD");
        OhMyEmail.subject("Order confirmation")
                .from("Shopping RabbitMQ")
                .to(email)
                .text(df.format(new Date()) + ": \n Hello " + user + ", \n your order has been confirmed.")
                .send();
        System.out.println(df.format(new Date()) + ": Sending a confirmation email to the email address " + email + " for " + user);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        // 下面的配置与生产者相对应
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        final String queueName = channel.queueDeclare().getQueue();// 生成一个独立的，非持久的，自动删除的queue
        channel.queueBind(queueName, EXCHANGE_NAME, "email");// 绑定queue和exchange,还有routekey。这样队列中就有通过EXCHANGE_NAME发布的消息。

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(df.format(new Date()) + ": messages from channel:" + channel + ",queue:" + queueName
                + ". To exit press CTRL+C");
        // defaultConsumer实现了Consumer，我们将使用它来缓存生产者发送过来储存在队列中的消息。当我们可以接收消息的时候，从中获取。
        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                try {
                    System.out.println(df.format(new Date()) + ": channel:" + channel + ",queue:" + queueName + ",consumer:" + this.getConsumerTag() + "  Received '" + message + "'");
                    JSONObject jsonObj = JSONObject.fromObject(message);
                    String user = jsonObj.get("user").toString();
                    String email = jsonObj.get("email").toString();
                    sendEmail(user, email);
                } catch (Exception e) {
                    channel.abort();
                }

                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        channel.basicConsume(queueName, true, consumer);//自动回复，消息发出后队列自动消除
    }
}
