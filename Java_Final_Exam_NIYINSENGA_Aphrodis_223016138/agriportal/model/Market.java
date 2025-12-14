package com.agriportal.model;

import java.time.LocalDateTime;

public class Market {
    private int id;
    private Harvest harvest;
    private String marketName;
    private Double price;
    private String status; // e.g., "Listed", "Sold", "Pending"
    private LocalDateTime createdAt;

    public Market() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Harvest getHarvest() { return harvest; }
    public void setHarvest(Harvest harvest) { this.harvest = harvest; }

    public String getMarketName() { return marketName; }
    public void setMarketName(String marketName) { this.marketName = marketName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Market{" +
                "id=" + id +
                ", harvestId=" + (harvest != null ? harvest.getId() : "null") +
                ", marketName='" + marketName + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
