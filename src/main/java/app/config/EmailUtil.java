package app.config;

import app.entities.Order;
import app.entities.PartsList;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.PartsListMapper;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.javalin.http.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class EmailUtil {

    private static final String API_KEY = System.getenv("SENDGRID_API_KEY");
    private static final SendGrid sg = new SendGrid(API_KEY);
    private static final Email from = new Email("emilkriegel@gmail.com", "Johannes Fog Byggemarked");

    //connectionPool to be able to use mapper. Is there a smarter way??
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    //Template ID's taken from SendGrid.com
    private static final String OrderConfirmationID = "d-1b6aefc418c3427880a7df567316899d";
    private static final String FinalConfirmationID = "d-11983f5f46e74b7eb645130c19ca529c";
    private static final String PaymentConfirmationID = "d-f1849a755bc8425faaabcd7140eb220e";

    public static void SendOrderConfirmation(Context ctx, Order order) {
        User user = ctx.sessionAttribute("currentUser");
        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();

        personalization.addTo(new Email(user.getEmail(), user.getUsername()));
        personalization.addDynamicTemplateData("name", user.getUsername());
        personalization.addDynamicTemplateData("login", user.getEmail());
        personalization.addDynamicTemplateData("number", user.getPhoneNumber());

        Attachments attachment = createSvgAttachment(order);

        mail.addPersonalization(personalization);
        mail.addAttachments(attachment);
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

    public static void sendFinalConfirmation(Context ctx, Order order){
        User user = ctx.sessionAttribute("currentUser");
        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();

        personalization.addTo(new Email(user.getEmail(), user.getUsername()));
        personalization.addDynamicTemplateData("name", user.getUsername());
        personalization.addDynamicTemplateData("orderNumber", order.getOrder_ID());

        Attachments attachment = createSvgAttachment(order);

        mail.addPersonalization(personalization);
        mail.addAttachments(attachment);
        mail.addCategory("carport");
        mail.setTemplateId(FinalConfirmationID);

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

    public static void sendPaymentConfirmation(Context ctx, Order order){
        User user = ctx.sessionAttribute("currentUser");
        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();

        personalization.addTo(new Email(user.getEmail(), user.getUsername()));
        personalization.addDynamicTemplateData("name", user.getUsername());
        personalization.addDynamicTemplateData("orderNumber", order.getOrder_ID());

        //Nedenstående createSvgAttachment kan erstattes med metode for stykliste
        Attachments attachment1 = createSvgAttachment(order);
        Attachments attachment2 = createPartListAttachment(order);

        mail.addPersonalization(personalization);
        mail.addAttachments(attachment1);
        mail.addAttachments(attachment2);
        mail.addCategory("carport");
        mail.setTemplateId(PaymentConfirmationID);

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

    public static Attachments createSvgAttachment(Order order){
        SvgUtil svg = new SvgUtil();
        svg.appendFromOrder(order);
        String svgContent = svg.buildSvg();

        //Gets the .svg file as bytes using UTF_8-encoding, then uses Java's Base64 encoder to encode the bytes to Base64
        String encodedSvg = Base64.getEncoder().encodeToString(svgContent.getBytes(StandardCharsets.UTF_8));

        Attachments attachment = new Attachments();
        attachment.setContent(encodedSvg);
        //"image/svg+xml" is not just a name. It tells the browser/email client etc what kind of file it is.
        attachment.setType("image/svg+xml");
        attachment.setFilename("carport.svg");
        //"attachment" tells SendGrid that it is an attachment, not to be displayed inline in the e-mail.
        attachment.setDisposition("attachment");
        attachment.setContentId("carportDrawing");

        return attachment;
    }

    public static Attachments createPartListAttachment(Order order){
        PartsListMapper partsListMapper = new PartsListMapper(connectionPool);
        try {
            List<PartsList> partsList = partsListMapper.getPartList(order.getOrder_ID());
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);

            writer.println("Beskrivelse;Længde;Antal;Beskrivelse");
            for(PartsList p : partsList){
                writer.printf("%s;%d;%d;%s;%n",
                        p.getPartName(),
                        p.getLength(),
                        p.getAmount(),
                        p.getDescription());
            }
            writer.flush();

            byte[] csvBytes = stringWriter.toString().getBytes(StandardCharsets.UTF_8);
            String base64csv = Base64.getEncoder().encodeToString(csvBytes);

            Attachments attachment = new Attachments();
            //setContent holds the actual attachment that goes into the email(the file)
            attachment.setContent(base64csv);
            //setType defines what filetype the attachment should be
            attachment.setType("text/csv");
            attachment.setFilename("Stykliste_" + order.getOrder_ID()+".csv");
            attachment.setDisposition("attachment");

            return attachment;
        } catch (DatabaseException e) {
            throw new RuntimeException("Could not create CSV attachment", e);
        }
    }
}
