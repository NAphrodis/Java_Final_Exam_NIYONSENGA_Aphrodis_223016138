package com.agriportal.model;

import java.time.LocalDate;

public class Order {
    private int id;
    private Product product;
    private Customer buyer;       // ✅ add this
    private String buyerName;     // optional if buyer_id = null
    private int quantityOrdered;
    private LocalDate orderDate;
    private String status;

    public Order() {}

    // ----- Getters and Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Customer getBuyer() { return buyer; }             // ✅ new
    public void setBuyer(Customer buyer) { this.buyer = buyer; } // ✅ new

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public int getQuantityOrdered() { return quantityOrdered; }
    public void setQuantityOrdered(int quantityOrdered) { this.quantityOrdered = quantityOrdered; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ----- Utility: compute total cost -----
    public double totalCost() {
        if (product != null)
            return quantityOrdered * product.getPrice();
        return 0.0;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", buyer=" + (buyer != null ? buyer.getName() : buyerName) +
                ", qty=" + quantityOrdered +
                ", status='" + status + '\'' +
                '}';
    }
}
