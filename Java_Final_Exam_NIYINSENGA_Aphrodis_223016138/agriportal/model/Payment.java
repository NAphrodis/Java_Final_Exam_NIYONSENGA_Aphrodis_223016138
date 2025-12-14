package com.agriportal.model;

import java.sql.Timestamp;

public class Payment {
    private int id;
    private Integer orderId;
    private double amount;
    private String method; // e.g. "CARD", "MOMO", "CASH"
    private String status; // "PENDING", "COMPLETED", "FAILED"
    private Timestamp paidAt;

    public Payment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getPaidAt() { return paidAt; }
    public void setPaidAt(Timestamp paidAt) { this.paidAt = paidAt; }

    @Override
    public String toString() {
        return "Payment{id=" + id + ", orderId=" + orderId + ", amount=" + amount +
                ", method='" + method + "', status='" + status + "', paidAt=" + paidAt + "}";
    }
}
