package com.agriportal.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Harvest {
    private int id;
    private Crop crop;
    private LocalDate harvestDate;
    private Double quantity;
    private String notes;
    private LocalDateTime createdAt;

    public Harvest() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Crop getCrop() { return crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public LocalDate getHarvestDate() { return harvestDate; }
    public void setHarvestDate(LocalDate harvestDate) { this.harvestDate = harvestDate; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Harvest{" +
                "id=" + id +
                ", cropId=" + (crop != null ? crop.getId() : "null") +
                ", harvestDate=" + harvestDate +
                ", quantity=" + quantity +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
