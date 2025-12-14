package com.agriportal.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class IrrigationSchedule {
    private int id;
    private Field field; // the field this schedule is for
    private LocalDate scheduleDate;
    private Double durationHours;
    private Double waterVolume; // liters or cubic meters, whichever you use
    private String notes;
    private LocalDateTime createdAt;

    public IrrigationSchedule() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Field getField() { return field; }
    public void setField(Field field) { this.field = field; }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public Double getDurationHours() { return durationHours; }
    public void setDurationHours(Double durationHours) { this.durationHours = durationHours; }

    public Double getWaterVolume() { return waterVolume; }
    public void setWaterVolume(Double waterVolume) { this.waterVolume = waterVolume; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "IrrigationSchedule{" +
                "id=" + id +
                ", fieldId=" + (field != null ? field.getId() : "null") +
                ", scheduleDate=" + scheduleDate +
                ", durationHours=" + durationHours +
                ", waterVolume=" + waterVolume +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
