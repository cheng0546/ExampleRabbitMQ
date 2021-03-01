package com.shopping.rabbitmq.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.shopping.rabbitmq.Order;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderSystem {
    private final static String EXCHANGE_NAME = "shopping_logs";//交换机名称为log

    public void startOrderSystem(Order order) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();//建立连接
            Channel channel = connection.createChannel();//建立频道
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//在频道里声明一个交换机，类型定位direct

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            System.out.println(df.format(new Date()) + ": " + channel + " Send stock message");
            JSONObject stockJsonObj = new JSONObject();
            stockJsonObj.put("product", order.getProduct());//该消息是针对发送验证邮件的。
            String stockMessage = stockJsonObj.toString();
            System.out.println(df.format(new Date()) + ": message content " + stockMessage);
            channel.basicPublish(EXCHANGE_NAME, "stock", null, stockMessage.getBytes());//发布消息，发布到EXCHANGE_NAME,并且routeKey标为info

            System.out.println(df.format(new Date()) + ": " + channel + " Send email message");
            JSONObject emailJsonObj = new JSONObject();
            emailJsonObj.put("user", order.getUser());//该消息是针对发送验证邮件的。
            emailJsonObj.put("email", order.getEmail());//该消息是针对发送验证邮件的。
            String emailMessage = emailJsonObj.toString();
            System.out.println(df.format(new Date()) + ": message content " + emailMessage);
            channel.basicPublish(EXCHANGE_NAME, "email", null, emailMessage.getBytes());//发布消息，发布到EXCHANGE_NAME,并且routeKey标为info

            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
