package app.services;

import app.entities.Order;

import java.util.Locale;

public class SvgUtil {
    private StringBuilder svg;
//    private final String viewBox;
    private final double width = 780;
    private final double height = 600;
    private final int x = 0;
    private final int y = 0;

    public SvgUtil(){
        this.svg = new StringBuilder();
    }

    public void addRect(int x, int y,double height, double width, String stroke, String fill){
        svg.append(String.format(Locale.US,"<rect x='%d' y='%d' height='%1f' width='%1f' stroke='%s' fill='%s' />\n",
                x, y, height, width, stroke, fill));
    }

    public void addLine(int x1, int y1, int x2, int y2, String style){
        svg.append(String.format(Locale.US,"<rect x1='%d' y1='%d' x2='%d' y2='%d' style='%s' />\n",
                x1, y1, x2, y2, style));
    }

    public String buildSvg(){
        //Builds the .svg file by setting up the <svg> element and using svg.toString() to add the rect/line elements added to the StringBuilder
        return String.format(Locale.US,"<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
                "width='%1f' height='%1f' viewBox=\"0 0 800 600\">\n%s</svg>",width, height, svg.toString());
    }

    public void appendFromOrder(Order order){
        addFrame(order.getCarport_width(), order.getCarport_length());
        addBeams(order.getCarport_length(), order.getCarport_width());
        addRafters(order.getCarport_width(), order.getCarport_length());
        addPoles(order.getCarport_length(), order.getCarport_width());
    }

    public void addFrame(double carportWidth, double carportLength){
        //No x & y because frame always starts at 0, 0
        addRect(0, 0, carportWidth, carportLength, "#000000", "#ffffff");
    }

    public void addBeams(double carportLength, double carportWidth){
        //height = 45 because 4,5 cm is the standard width of the beams
        double height = 4.5;
        int y = (int)carportWidth-35;
        //adds top beam 35 cm in from the top and 35 cm in from the bottom
        addRect(0, 35, height, carportLength, "#000000", "#ffffff");
        addRect(0, y, height, carportLength, "#000000", "#ffffff");
    }

    public void addRafters(double carportWidth, double carportLength){
        int rafterAmount = (int)carportLength/55 + 1;
        int rafterSpacing = 55;
        double rafterWidth = 4.5;
        for(int i = 0; i < rafterAmount; i++){
            int x = i * rafterSpacing;
            addRect(x,0, carportWidth, rafterWidth, "#000000", "#ffffff");
        }
    }

    public void addPoles(double carportLength, double carportWidth){
        int numberOfPoles = 2;
        int length = (int)carportLength;
        int maxPoleDistance = 310;
        int initialSpacing = 107;
        int fullLength = length - (int)initialSpacing;
        int y = (int)carportWidth-38;
        int x = initialSpacing;


        if (fullLength > 0) {
            int extraPoles = fullLength / maxPoleDistance;
            numberOfPoles += extraPoles * 2;
        } else {
            numberOfPoles = 4;
        }

        for(int i = 0; i < (numberOfPoles) / 2; i++){

            if(x > carportLength){
                break;
            }
            addRect(x, 32, 9.7, 9.7, "#000000", "#ffffff");
            addRect(x, y, 9.7, 9.7, "#000000", "#ffffff");
            x += maxPoleDistance-10;
        }
    }
}
