package app.controllers;

import app.entities.Order;
import app.entities.Part;
import app.persistence.ConnectionPool;
import app.persistence.PartMapper;
import app.persistence.PartVariantMapper;
import app.services.Calculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class PriceController {

    private Calculator calculator;
    private PartMapper partMapper;
    private PartVariantMapper partVariantMapper;
    private final ConnectionPool connectionPool;

    public PriceController(ConnectionPool connectionPool) {
        this.calculator = new Calculator(connectionPool);
        this.partMapper = new PartMapper(connectionPool);
        this.connectionPool = connectionPool;
    }

    public void routes(Javalin app) {
        app.get("/calculate-price", ctx -> calculatePrice(ctx));
    }

    private void calculatePrice(Context ctx) {
        try {
            int length = Integer.parseInt(ctx.queryParam("carport-length-selection"));
            int width = Integer.parseInt(ctx.queryParam("carport-width-selection"));

            // Simulated order that currentUser will be using to see guiding price
            Order tempOrder = new Order(0, 0, width, length, null, 0, "pending");

            Calculator calculator = new Calculator(connectionPool);


            double totalPrice = calculator.calcPrice(tempOrder);

            ctx.json(totalPrice);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int calcPolesQuantity(Order order) {
        int numberOfPoles = 4;
        int length = order.getCarport_length();
        int maxPoleDistance = 340;
        int initialSpacing = 100;
        int fullLength = length - initialSpacing;

        if (fullLength > 0) {
            numberOfPoles = 2 * ((fullLength + maxPoleDistance - 1) / maxPoleDistance + 1);
        }
        if (numberOfPoles < 4 ) {
            numberOfPoles = 4;
        }
        return numberOfPoles;
    }

    private int calcBeamsQuantity(Order order) {
        return 2; // Initial start, not actual. Could be 4 poles etc.
    }

    private int calcRaftersQuantity(Order order) {
        int length = order.getCarport_length();
        int width = order.getCarport_width();
        int rafterSpacing = 55;
        return (length + rafterSpacing - 1) / rafterSpacing + 1;
    }

    private int getPriceByName(String partName) {
        Part part = partMapper.getPartByName(partName);
        if (part != null) {
            return  part.getPrice();
        }
        throw new RuntimeException("Price not found for: " + partName);
    }

}
