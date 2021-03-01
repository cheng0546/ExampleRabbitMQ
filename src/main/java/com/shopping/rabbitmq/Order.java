package com.shopping.rabbitmq;

public class Order {
    public String user;
    public String product;
    public String email;

    public Order(String user, String product, String email) {
        this.user = user;
        this.product = product;
        this.email = email;
    }

    public String getUser() {
        return this.user;
    }

    public String getProduct() {
        return this.product;
    }

    public String getEmail() {
        return this.email;
    }
}
