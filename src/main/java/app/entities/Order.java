package app.entities;

import java.sql.Date;

public class Order {

    int order_ID;
    int carport_width;
    int carport_length;
    Date date;
    boolean status;
    int user_ID;
    int price;

    public Order(int order_ID, Date date) {
        this.order_ID = order_ID;
        this.date = date;
    }

    public Order(int order_ID, Date date,  int carport_width, int carport_length, boolean status) {
        this.order_ID = order_ID;
        this.date = date;
        this.carport_width = carport_width;
        this.carport_length = carport_length;
        this.status = status;
    }

    public Order(int user_ID, int order_ID, Date date, int carport_width, int carport_length, boolean status, int price) {
        this.user_ID = user_ID;
        this.order_ID = order_ID;
        this.date = date;
        this.carport_width = carport_width;
        this.carport_length = carport_length;
        this.status = status;
        this.price = price;
    }

    public Order(int order_ID) {
        this.order_ID = order_ID;
    }

    public int getOrder_ID() {
        return order_ID;
    }

    public void setOrder_ID(int order_ID) {
        this.order_ID = order_ID;
    }

    public int getCarport_width() {
        return carport_width;
    }

    public void setCarport_width(int carport_width) {
        this.carport_width = carport_width;
    }

    public int getCarport_length() {
        return carport_length;
    }

    public void setCarport_length(int carport_length) {
        this.carport_length = carport_length;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(int user_ID) {
        this.user_ID = user_ID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
