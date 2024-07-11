package com.sp.cdio_project2.ui.dashboard;



public class Item {
    private int id;
    private String title;
    private String description;
    private int quantity;

    public Item(int id, String title, String description, int quantity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
