package app.config;

import app.entities.User;
import app.persistence.ConnectionPool;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.javalin.http.Context;

import java.io.IOException;

public class EmailUtil {

    private static final String API_KEY = System.getenv("SENDGRID_API_KEY");
    private static final SendGrid sg = new SendGrid(API_KEY);
    private static final Email from = new Email("emilkriegel@gmail.com", "Johannes Fog Byggemarked");
    private static final String OrderConfirmationID = "d-1b6aefc418c3427880a7df567316899d"; //Template ID from SendGrid
    private static final String FinalConfirmationID = null;
    private static final String PaymentConfirmationID = null;

    public static void SendOrderConfirmation(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();

        personalization.addTo(new Email(user.getEmail(), user.getUsername()));
        personalization.addDynamicTemplateData("name", user.getUsername());
        personalization.addDynamicTemplateData("login", user.getEmail());
        personalization.addDynamicTemplateData("number", user.getPhoneNumber());

        mail.addCategory("carport");
        mail.setTemplateId(OrderConfirmationID);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException e) {
            System.out.println("Error sending mail");
            e.printStackTrace();
        }
    }
}
