package app;

import app.entities.Order;

public class Svg {
    private StringBuilder svg;
//    private final String viewBox;
    private final double width = 780;
    private final double height = 600;
    private final int x = 0;
    private final int y = 0;

    public Svg (){
        this.svg = new StringBuilder();
    }

    public void addRect(int x, int y,double height, double width, String stroke, String fill){
        svg.append(String.format("<rect x='%d' y='%d' height='%d' width='%d' stroke='%s' fill='%s'",
                x, y, height, width, stroke, fill));
    }

    public void addLine(int x1, int y1, int x2, int y2, String style){
        svg.append(String.format("<rect x1='%d' y1='%d' x2='%d' y2='%d' style='%s'",
                x1, y1, x2, y2, style));
    }

    public String buildSvg(){
        //Builds the .svg file by setting up the <svg> element and using svg.toString() to add the rect/line elements added to the StringBuilder
        return String.format("<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
                "width='%s' height='%s' viewBox=\"0 0 800 600\">\n%s</svg>",width, height, svg.toString());
    }

    public void appendFromOrder(Order order){
        addFrame(order.getCarport_width(), order.getCarport_length());
        addBeams(order.getCarport_length());
        addRafters(order.getCarport_width(), order.getCarport_length());
        //addPosts(order.getCarport_length);
    }

    public void addFrame(double carportWidth, double carportLength){
        //No x & y because frame always starts at 0, 0
        addRect(0, 0, carportWidth, carportLength, "#000000", "#ffffff");
    }

    public void addBeams(double carportLength){
        //height = 45 because 4,5 cm is the standard width of the beams
        double height = 4.5;
        //adds top beam 35 cm in from the top
        addRect(0, 35, height, carportLength, "#000000", "#ffffff");
        addRect(0, 565, height, carportLength, "#000000", "#ffffff");
    }

    public void addRafters(double carportWidth, double carportLength){
        int rafterAmount = (int)carportLength/55 + 1;
        int rafterSpacing = 55;
        //height = 45 because 4,5 cm is the standard width of the beams
        double rafterWidth = 4.5;
        for(int i = 0; i < rafterAmount; i++){
            int x = 0;
            addRect(x,0, carportWidth, rafterWidth, "#000000", "#ffffff");
        }
    }

    public void addPosts(double carportLength){
        carportLength =+ 100;
        int numberOfPoles = 2 * (((int)carportLength + 340 - 1) / 340 + 1);

    }
}
