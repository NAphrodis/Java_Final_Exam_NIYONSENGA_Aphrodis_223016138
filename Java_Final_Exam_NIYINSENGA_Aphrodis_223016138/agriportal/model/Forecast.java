package com.agriportal.model;

import java.time.LocalDate;

/**
 * Forecast model - represents a weather forecast and crop recommendation.
 */
public class Forecast {
    private int id;
    private LocalDate date;
    private String condition;
    private Double temperature;
    private Double rainfall;
    private String recommendation;

    public Forecast() {}

    public Forecast(LocalDate date, String condition, Double temperature, Double rainfall, String recommendation) {
        this.date = date;
        this.condition = condition;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.recommendation = recommendation;
    }

    // === Getters & Setters ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getRainfall() { return rainfall; }
    public void setRainfall(Double rainfall) { this.rainfall = rainfall; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}
