package com.agriportal.model;

import java.time.LocalDateTime;

/**
 * Field model representing a farmer's field or land area.
 */
public class Field {
    private int id;
    private String name;
    private String address;
    private Double capacity; // capacity in hectares
    private String manager;
    private String contact;
    private Farmer owner; // instead of farmer (more natural)
    private LocalDateTime createdAt;

    private String soilType;
    private String climate;
    private String location;
    private double areaHectares;

    public Field() {}

    public Field(int id, String name, String address, Double capacity,
                 String manager, String contact, Farmer owner, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.manager = manager;
        this.contact = contact;
        this.owner = owner;
        this.createdAt = createdAt;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }

    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public Farmer getOwner() { return owner; }
    public void setOwner(Farmer owner) { this.owner = owner; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getClimate() { return climate; }
    public void setClimate(String climate) { this.climate = climate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getAreaHectares() { return areaHectares; }
    public void setAreaHectares(double areaHectares) { this.areaHectares = areaHectares; }

    public void setOwnerId(int id) {
        if (this.owner == null) this.owner = new Farmer();
        this.owner.setId(id);
    }

    @Override
    public String toString() {
        return "Field{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", areaHectares=" + areaHectares +
                ", owner=" + (owner != null ? owner.getId() : "null") +
                '}';
    }
}
