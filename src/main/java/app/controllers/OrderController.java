package app.controllers;

import app.config.EmailUtil;
import app.config.SvgUtil;
import app.entities.Order;
import app.entities.PartsList;
import app.entities.User;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.PartsListMapper;
import app.services.Calculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;

public class OrderController {

    private static ConnectionPool connectionPool;
    private static OrderMapper orderMapper;
    private static Calculator calculator;

    public OrderController(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

public static void OrderCreate(Context ctx) {

    try {
        User currentUser = ctx.sessionAttribute("currentUser");
        if (currentUser == null) {
            ctx.attribute("message", "Du skal logge ind for at kunne fuldføre din bestilling.");
            ctx.render("loginPage.html");
        }

        int width = Integer.parseInt(ctx.formParam("carport-width-selection"));
        int length = Integer.parseInt(ctx.formParam("carport-length-selection"));
        Date date = new Date(System.currentTimeMillis());
        String status = "pending";
        int price; // Not final. Den skal tage fat i stykliste prisen.

        int userID = currentUser.getUser_ID();

        Order order = new Order(0, userID, width, length, date, 0, status);

        //Creates an .svg file from the order and adds it to the session to be displayed
        SvgUtil svg = new SvgUtil();
        svg.appendFromOrder(order);
        String svgContent = svg.buildSvg();
        ctx.attribute("svg", svgContent);

        OrderMapper orderMapper = new OrderMapper(connectionPool);

        orderMapper.insertOrder(order);
        int orderID = orderMapper.getNewestOrderID();
        order.setOrder_ID(orderID);

        // Calculator stuff
        Calculator calculator = new Calculator(connectionPool);
        calculator.calcPoles(order);
        calculator.calcBeams(order);
        calculator.calcRafters(order);

        price = calculator.calcPrice(order);
        order.setPrice(price);

        orderMapper.updateOrderPrice(order);

        ctx.attribute("message","Din ordre er nu blevet afsendt og vi vil få en til at kigge på den!");
        ctx.render("orderConfirmationPage.html"); // Bare en idé til en ny HTML side.


        EmailUtil.SendOrderConfirmation(ctx, order);
        //"Af"-kommenteres for at begynde at sende mails af sted ved oprettelse af ordre
    } catch (Exception e) {
        e.printStackTrace();
        ctx.attribute("message","Der er sket en fejl " + e.getMessage());
        ctx.render("index.html");
    }

    }

    public static void showPartsListSVG(Context ctx) {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));

            OrderMapper orderMapper = new OrderMapper(connectionPool);
            Order order = orderMapper.getOrderById(orderId);

            PartsListMapper partsListMapper = new PartsListMapper(connectionPool);
            List<PartsList> partsList = partsListMapper.getPartList(orderId);

            SvgUtil svgUtil = new SvgUtil();
            svgUtil.appendFromOrder(order);
            String svgContent = svgUtil.buildSvg();


            ctx.attribute("orderId",orderId);
            ctx.attribute("partsList",partsList);
            ctx.attribute("svg",svgContent);

            ctx.render("partsListPage.html");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStatus(Context ctx){


    }
}
