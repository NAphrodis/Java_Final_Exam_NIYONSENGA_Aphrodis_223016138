package com.agriportal.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Simple container for weather observations related to a farm.
 */
public class WeatherInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int farmId; // lightweight reference to the farm (avoid circular dependencies)
    private LocalDate date;
    private Double temperatureCelsius;
    private Double rainfallMm;
    private String condition; // e.g., "Sunny", "Rainy", "Cloudy"

    public WeatherInfo() { }

    public WeatherInfo(int id, int farmId, LocalDate date, Double temperatureCelsius, Double rainfallMm, String condition) {
        this.id = id;
        this.farmId = farmId;
        this.date = date;
        this.temperatureCelsius = temperatureCelsius;
        this.rainfallMm = rainfallMm;
        this.condition = condition;
    }

    public boolean isGoodForPlanting() {
        // naive rule: temperature between 15 and 30 and low recent rainfall
        if (temperatureCelsius == null) return false;
        return temperatureCelsius >= 15 && temperatureCelsius <= 30 && (rainfallMm == null || rainfallMm < 20);
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFarmId() { return farmId; }
    public void setFarmId(int farmId) { this.farmId = farmId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(Double temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }

    public Double getRainfallMm() { return rainfallMm; }
    public void setRainfallMm(Double rainfallMm) { this.rainfallMm = rainfallMm; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    @Override
    public String toString() {
        return "WeatherInfo{" +
                "id=" + id +
                ", farmId=" + farmId +
                ", date=" + date +
                ", temperatureCelsius=" + temperatureCelsius +
                ", rainfallMm=" + rainfallMm +
                ", condition='" + condition + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeatherInfo that = (WeatherInfo) o;
        return id == that.id && Objects.equals(date, that.date) && farmId == that.farmId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, farmId, date);
    }
}
