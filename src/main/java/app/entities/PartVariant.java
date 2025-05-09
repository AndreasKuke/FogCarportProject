package app.entities;

public class PartVariant {

    private int partVariant_id;
    private Part part;
    private int length;

    public PartVariant(int partVariant_id, Part part, int length) {
        this.partVariant_id = partVariant_id;
        this.part = part;
        this.length = length;
    }

    public int getPartVariant_id() {
        return partVariant_id;
    }

    public void setPartVariant_id(int partVariant_id) {
        this.partVariant_id = partVariant_id;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
