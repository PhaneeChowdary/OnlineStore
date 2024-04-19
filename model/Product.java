package model;

import java.io.Serializable;

public class Product implements Serializable {
    private String productId;
    private String name;
    private String description;
    private double price;
    private int quantityAvailable;

    public Product(String productId, String name, String description, double price, int quantityAvailable) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setPrice(double price) { this.price = price; }

    public double getPrice() {
        return this.price;
    }

    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public int getQuantityAvailable() {
        return this.quantityAvailable;
    }
}