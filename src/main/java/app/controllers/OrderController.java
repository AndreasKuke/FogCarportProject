package app.controllers;

import app.entities.Order;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;

public class OrderController {

    private static ConnectionPool connectionPool;

    public OrderController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

public static void OrderCreate(Context ctx) {

    try {
        int width = Integer.parseInt(ctx.formParam("carport-width-selection"));
        int height = Integer.parseInt(ctx.formParam("carport-height-selection"));
        int length = Integer.parseInt(ctx.formParam("carport-length-selection"));
        Date date = new Date(System.currentTimeMillis());
        boolean status = false;

        Order order = new Order(0, date, width, height, length, status);

        OrderMapper orderMapper = new OrderMapper(connectionPool);
        orderMapper.insertOrder(order);

        ctx.attribute("message","Din ordre er nu blevet afsendt og vi vil få en til at kigge på den!");
        ctx.render("orderConfirmationPage.html"); // Bare en idé til en ny HTML side.
    } catch (Exception e) {
        e.printStackTrace();
        ctx.attribute("message","Der er sket en fejl " + e.getMessage());
        ctx.render("index.html");
    }

    }
}
