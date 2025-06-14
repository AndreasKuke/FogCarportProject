package app.entities;

public class PartsList {

    private int partsList_id;
    private int order_id;
    private int partVariant_id;
    private int amount;
    private String description;
    private String partName;
    private int length;

    public PartsList(int partsList_id, int order_id, int partVariant_id, int amount, String description) {
        this.partsList_id = partsList_id;
        this.order_id = order_id;
        this.partVariant_id = partVariant_id;
        this.amount = amount;
        this.description = description;
    }

    public PartsList(int partsList_id, int order_id, int partVariant_id, int amount) {
        this.partsList_id = partsList_id;
        this.order_id = order_id;
        this.partVariant_id = partVariant_id;
        this.amount = amount;
    }

    public PartsList(int order_id, int partVariant_id, int amount, String description) {
        this.order_id = order_id;
        this.partVariant_id = partVariant_id;
        this.amount = amount;
        this.description = description;
    }

    public PartsList(int order_id, int partVariant_id, int amount, String description, String partName, int length) {
        this.order_id = order_id;
        this.partVariant_id = partVariant_id;
        this.amount = amount;
        this.description = description;
        this.partName = partName;
        this.length = length;
    }

    public int getPartsList_id() {
        return partsList_id;
    }

    public void setPartsList_id(int partsList_id) {
        this.partsList_id = partsList_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getPartVariant_id() {
        return partVariant_id;
    }

    public void setPartVariant_id(int partVariant_id) {
        this.partVariant_id = partVariant_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
