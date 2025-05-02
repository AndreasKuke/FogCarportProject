package app.controllers;

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
        String email = ctx.formParam("username");
        String password = ctx.formParam("password");

        User user = userMapper.getUserByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            ctx.sessionAttribute("user", user);

        } else {
            ctx.sessionAttribute("Error", "Invalid username or password.");
            ctx.redirect("/login");
        }
    }

    public static void registerUser(Context ctx) {
        String email = ctx.formParam("username");
        String password = ctx.formParam("password");
        String confirmPassword = ctx.formParam("confirm-password");
        String phoneNumber = ctx.formParam("phoneNumber");
        String name = ctx.formParam("name");


        User existingUser = UserMapper.getUserByEmail(email);
        if (existingUser != null) {
            ctx.sessionAttribute("Error", "Username already exists.");
            ctx.redirect("/register");
            return;
        } else if (!password.equals(confirmPassword)){
            ctx.sessionAttribute("Error", "Passwords do not match.");
            ctx.redirect("/register");
        }
        else {
            userMapper.createUser(email, password, phoneNumber, name);
            ctx.redirect("/login");
        }
    }

    public static void logout(Context ctx) {
        ctx.sessionAttribute("user", null);
        ctx.redirect("/index");
    }
}
