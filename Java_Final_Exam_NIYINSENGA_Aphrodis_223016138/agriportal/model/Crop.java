package com.agriportal.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;


public class Crop implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String cropName;
    private String variety;
    private LocalDate plantedDate;
    private LocalDate expectedHarvestDate;
    private String status; // e.g., "Planted", "Growing", "Harvested"
    private Double expectedYield; // e.g., in kg or tonnes
    private Field field; // The field (land) where this crop is planted

    public Crop() {}

    public Crop(int id, String cropName, String variety,
                LocalDate plantedDate, LocalDate expectedHarvestDate,
                Double expectedYield) {
        this.id = id;
        this.cropName = cropName;
        this.variety = variety;
        this.plantedDate = plantedDate;
        this.expectedHarvestDate = expectedHarvestDate;
        this.expectedYield = expectedYield;
        this.status = "Planted";
    }

    // === Business Helpers ===
    public int growthPeriodDays() {
        if (plantedDate == null || expectedHarvestDate == null)
            return -1;
        return Period.between(plantedDate, expectedHarvestDate).getDays();
    }

    public boolean isHarvestDue() {
        if (expectedHarvestDate == null) return false;
        return !LocalDate.now().isBefore(expectedHarvestDate);
    }

    public void markHarvested() {
        this.status = "Harvested";
    }

    // === Getters and Setters ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public LocalDate getPlantedDate() { return plantedDate; }
    public void setPlantedDate(LocalDate plantedDate) { this.plantedDate = plantedDate; }

    public LocalDate getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(LocalDate expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getExpectedYield() { return expectedYield; }
    public void setExpectedYield(Double expectedYield) { this.expectedYield = expectedYield; }

    public Field getField() { return field; }
    public void setField(Field field) { this.field = field; }

    // === Display ===
    @Override
    public String toString() {
        return "Crop{" +
                "id=" + id +
                ", cropName='" + cropName + '\'' +
                ", variety='" + variety + '\'' +
                ", plantedDate=" + plantedDate +
                ", expectedHarvestDate=" + expectedHarvestDate +
                ", status='" + status + '\'' +
                ", expectedYield=" + expectedYield +
                ", fieldId=" + (field != null ? field.getId() : "null") +
                '}';
    }

    // === Equality and Hash ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crop crop = (Crop) o;
        return id == crop.id && Objects.equals(cropName, crop.cropName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cropName);
    }
}
