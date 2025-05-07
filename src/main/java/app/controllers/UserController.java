package app.controllers;

import app.config.PasswordUtil;
import app.exceptions.DatabaseException;
import app.entities.User;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(
            "postgres", "postgres", "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "cupcake"
    );
    private static final UserMapper userMapper = new UserMapper(connectionPool);

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
    }



    public static void loginUser(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        String hashedFromDB = userMapper.getUserPasswordFromDB(email);

        if (hashedFromDB != null && PasswordUtil.checkPassword(password, hashedFromDB)) {
            try {
                User user = userMapper.getUserByEmail(email);
                ctx.sessionAttribute("currentUser", user);

                ctx.redirect("/index");
            } catch (Exception e) {
                ctx.sessionAttribute("Error", "An error occurred during login.");
                ctx.render("loginPage.html");
            }
        } else {
            ctx.sessionAttribute("Error", "Invalid username or password.");
            ctx.redirect("/login");


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
                userMapper.createUser(email, password, phoneNumber, name);
                ctx.redirect("/login");
            }
        }

        else {
            userMapper.createUser(email, hashedPassword, phoneNumber, name);

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
        ctx.render("adminPage.html");
    }

}
