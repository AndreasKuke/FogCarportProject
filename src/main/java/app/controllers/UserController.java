package app.controllers;

import app.config.PasswordUtil;
import app.config.SvgUtil;
import app.entities.Order;
import app.entities.PartsList;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.PartsListMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.List;

public class UserController {
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(
            "postgres", "postgres", "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "carport"
    );
    private static final UserMapper userMapper = new UserMapper(connectionPool);
    private static final OrderMapper orderMapper = new OrderMapper(connectionPool);

    public static void routes(Javalin app) {
        app.get("/", ctx -> ctx.redirect("/index"));
        app.get("/index", UserController::frontPage);
        app.get("/login", UserController::loginPage);
        app.post("/login", UserController::loginUser);
        app.get("/register", UserController::registerPage);
        app.post("/register", UserController::registerUser);
        app.get("/logout", UserController::logout);
        app.post("/logout", UserController::logout);
        app.get("/profilePage", UserController::profilePage);
        app.get("/adminPage", UserController::adminPage);

        app.get("/admin/partsListPage/{orderId}", ctx ->{
            showPartsListPage(ctx);
        });

        app.post("/admin/orderStatus/toggle/{orderId}", ctx -> {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));
            Order order = orderMapper.getOrderById(orderId);
            order.setStatus(!order.isStatus());
            orderMapper.updateOrderStatus(order);
            ctx.redirect("/adminPage");
        });
    }



    public static void loginUser(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        String hashedFromDB = userMapper.getUserPasswordFromDB(email);

        try {
            User user = userMapper.getUserByEmail(email);

            if (PasswordUtil.checkPassword(password, hashedFromDB)) {
                ctx.sessionAttribute("currentUser", user);
                if (user.isAdmin()) {
                    ctx.redirect("/adminPage");
                } else {
                    ctx.redirect("/index");
                }

            } else {
                ctx.sessionAttribute("Error", "Invalid username or password.");
                ctx.redirect("/login");
            }
        } catch (Exception e) {
            ctx.sessionAttribute("Error", e.getMessage());
            ctx.render("loginPage.html");
        }
    }

    public static void registerUser(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String confirmPassword = ctx.formParam("confirm-password");
        String phoneNumber = ctx.formParam("phone");
        String name = ctx.formParam("username");

        String hashedPassword = PasswordUtil.hashPassword(password);

        User existingUser = userMapper.getUserByEmail(email);
        if (existingUser != null) {
            ctx.sessionAttribute("Error", "Username already exists.");
            ctx.redirect("/register");
        } else {
            assert password != null;
            if (!password.equals(confirmPassword)) {
                ctx.sessionAttribute("Error", "Passwords do not match.");
                ctx.redirect("/register");
            } else {
                userMapper.createUser(email, hashedPassword, phoneNumber, name);
                ctx.redirect("/login");
            }
        }
    }

    public static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/login");
    }

    public static void profilePage(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        if (user != null) {
                ctx.render("profilePage.html");
        } else {
            ctx.redirect("/login");
        }
    }

    // methods for routes
    public static void loginPage(Context ctx) {
        ctx.render("loginPage.html");
    }

    public static void registerPage(Context ctx) {
        ctx.render("registerPage.html");
    }

    public static void frontPage(Context ctx) {
        ctx.render("index.html");
    }

    public static void adminPage(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        if (user != null && user.isAdmin()) {
            try {
                List<Order> orders = orderMapper.getAllOrders();
                ctx.attribute("orders", orders);
                ctx.render("adminPage.html");
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        } else {
            ctx.redirect("/login");
        }
    }

    public static void showPartsListPage(Context ctx){
        try {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));
            PartsListMapper partsListMapper = new PartsListMapper(connectionPool);
            List<PartsList> parts = partsListMapper.getPartList(orderId);


            ctx.attribute("orderId", orderId);
            ctx.attribute("parts", parts);

            //SVG
            OrderMapper orderMapper = new OrderMapper(connectionPool);
            Order order = orderMapper.getOrderById(orderId);
            SvgUtil svgUtil = new SvgUtil();
            svgUtil.appendFromOrder(order);
            String svgContent = svgUtil.buildSvg();
            ctx.attribute("svg", svgContent);

            ctx.render("partsListPage.html");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
