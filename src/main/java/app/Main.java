package app;

import app.config.SessionConfig;
import app.config.SvgUtil;
import app.config.ThymeleafConfig;
import app.controllers.OrderController;
import app.controllers.PriceController;
import app.controllers.UserController;
import app.entities.Order;
import app.persistence.ConnectionPool;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Logger;


public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "pxo73qna";
    private static final String URL = "jdbc:postgresql://46.101.133.216:5432/%s?currentSchema=public";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    // Controllers
    private static final OrderController orderController = new OrderController(connectionPool);
    private static final PriceController priceController = new PriceController(connectionPool);

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        app.before(ctx -> {
            ctx.attribute("session", ctx.sessionAttributeMap());
        });

        UserController.routes(app);

        // order routes
        app.post("/create-order", ctx -> {
            orderController.OrderCreate(ctx);

        });

        // price routes
        priceController.routes(app);

    }
}