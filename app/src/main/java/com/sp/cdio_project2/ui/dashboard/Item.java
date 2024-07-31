package com.sp.cdio_project2.ui.dashboard;

// Add necessary imports here if needed

public class Item {
    private String id;
    private String title;
    private String description;
    private String code; // Ensuring this field exists
    private int quantity;

    public Item() {
        // Default constructor required for calls to DataSnapshot.getValue(Item.class)
    }

    // Ensure this constructor matches the parameters you intend to use
    public Item(String id, String title, String description, String code, int quantity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.code = code;
        this.quantity = quantity;
    }

    // Getter and Setter methods for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
