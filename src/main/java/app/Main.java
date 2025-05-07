package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.OrderController;
import app.controllers.UserController;
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

import java.io.IOException;
import java.util.logging.Logger;


public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    // Controllers
    private static final OrderController orderController = new OrderController(connectionPool);

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        app.before(ctx -> {
            ctx.attribute("session", ctx.sessionAttributeMap());
        });

        app.get("/", ctx -> ctx.redirect("/index"));
        app.get("/index", ctx -> ctx.render("index.html"));

        // login routes
        app.get("/login", ctx -> ctx.render("loginPage.html"));
        app.get("/register", ctx -> ctx.render("registerPage.html"));
        app.post("/register", UserController::registerUser);
        app.post("/login", UserController::loginUser);


        // order routes
        app.post("/create-order", ctx -> {
            orderController.OrderCreate(ctx);

        });
        Email from = new Email("emilkriegel@gmail.com");
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);

        String API_KEY = System.getenv("SENDGRID_API_KEY");

        Personalization personalization = new Personalization();

        /* Erstat kunde@gmail.com, name, email og zip med egne værdier ****/
        /* I test-fasen - brug din egen email, så du kan modtage beskeden */
        personalization.addTo(new Email("mbjerrumj@gmail.com"));
        personalization.addDynamicTemplateData("name", "Anders And");
        personalization.addDynamicTemplateData("login", "anders@and.dk");
        personalization.addDynamicTemplateData("number", "12345678");
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();
        try
        {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-1b6aefc418c3427880a7df567316899d";
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        }
        catch (IOException ex)
        {
            System.out.println("Error sending mail");
            ex.printStackTrace();
        }
    }

}