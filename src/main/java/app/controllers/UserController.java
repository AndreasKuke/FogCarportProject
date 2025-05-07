package app.controllers;

import app.config.PasswordUtil;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import app.entities.User;
import io.javalin.http.Context;

public class UserController {
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(
            "postgres", "postgres", "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "cupcake"
    );
    private static final UserMapper userMapper = new UserMapper(connectionPool);

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
            return;
        } else if (!password.equals(confirmPassword)){
            ctx.sessionAttribute("Error", "Passwords do not match.");
            ctx.redirect("/register");
        }
        else {
            userMapper.createUser(email, hashedPassword, phoneNumber, name);
            ctx.redirect("/login");
        }
    }

    public static void logout(Context ctx) {
        ctx.sessionAttribute("user", null);
        ctx.redirect("/index");
    }
}
