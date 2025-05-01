package app.entities;

public class Part {

    int part_id;
    String part_name;
    String part_unit;
    // String part_description;
    int part_amount;
    int part_length;

    public Part(int part_id, String part_name, String part_unit, int part_amount, int part_length) {
        this.part_id = part_id;
        this.part_name = part_name;
        this.part_unit = part_unit;
        this.part_amount = part_amount;
        this.part_length = part_length;
    }

    public Part(int part_id){
        this.part_id = part_id;
    }

    public Part(int part_id, String part_name, String part_unit){
        this.part_id = part_id;
        this.part_name = part_name;
        this.part_unit = part_unit;
    }

    public int getPart_id() {
        return part_id;
    }

    public void setPart_id(int part_id) {
        this.part_id = part_id;
    }

    public String getPart_name() {
        return part_name;
    }

    public void setPart_name(String part_name) {
        this.part_name = part_name;
    }

    public String getPart_unit() {
        return part_unit;
    }

    public void setPart_unit(String part_unit) {
        this.part_unit = part_unit;
    }

    public int getPart_amount() {
        return part_amount;
    }

    public void setPart_amount(int part_amount) {
        this.part_amount = part_amount;
    }

    public int getPart_length() {
        return part_length;
    }

    public void setPart_length(int part_length) {
        this.part_length = part_length;
    }
}
