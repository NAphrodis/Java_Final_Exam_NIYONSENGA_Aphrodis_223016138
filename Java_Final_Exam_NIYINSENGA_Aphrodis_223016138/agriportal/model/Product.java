package com.agriportal.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a product (produce or input) listed by a Farmer for sale.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;
    private double price; // unit price
    private int quantity; // available quantity (units depend on product)
    private Farmer seller;
    private String category; // e.g., "Maize", "Fertilizer"

    public Product() { }

    public Product(int id, String name, String description, double price, int quantity, Farmer seller, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.seller = seller;
        this.category = category;
    }

    // business helpers
    public double totalValue() {
        return price * quantity;
    }

    public boolean reduceQuantity(int amount) {
        if (amount <= 0 || amount > quantity) return false;
        quantity -= amount;
        return true;
    }

    public boolean increaseQuantity(int amount) {
        if (amount <= 0) return false;
        quantity += amount;
        return true;
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("price cannot be negative");
        this.price = price;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("quantity cannot be negative");
        this.quantity = quantity;
    }

    public Farmer getSeller() { return seller; }
    public void setSeller(Farmer seller) { this.seller = seller; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seller=" + (seller != null ? seller.getName() : "null") +
                ", price=" + price +
                ", quantity=" + quantity +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return id == product.id && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
