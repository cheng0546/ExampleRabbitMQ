package com.shopping.rabbitmq.controller;

import com.shopping.rabbitmq.Order;
import com.shopping.rabbitmq.producer.OrderSystem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("shopping")
@CrossOrigin
public class ShoppingApi {
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @ResponseBody
    public String order(HttpServletRequest request) throws InterruptedException {
        String user = request.getParameter("user");
        String product = request.getParameter("product");
        String email = request.getParameter("email");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(df.format(new Date()) + ": new succeed order of " + user);

        Thread.sleep(1000);

        Order order = new Order(user, product, email);
        OrderSystem orderSystem = new OrderSystem();
        orderSystem.startOrderSystem(order);
        return "Your order is succeed";
    }
}
