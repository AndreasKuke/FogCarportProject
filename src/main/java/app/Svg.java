package app;

public class Svg {
    private StringBuilder svg;
    private final String viewBox;
    private final String width;
    private final String height;
    private final int x;
    private final int y;

    public Svg (int x, int y, String viewBox, String width, String height){
        this.x = x;
        this.y = y;
        this.viewBox = viewBox;
        this.width = width;
        this.height = height;
        this.svg = new StringBuilder();
    }

    public void addRect(int x, int y,int height, int width, String stroke, String fill){
        svg.append(String.format("<rect x='%d' y='%d' height='%d' width='%d' stroke='%s' fill='%s'",
                x, y, height, width, stroke, fill));
    }

    public void addLine()
}
