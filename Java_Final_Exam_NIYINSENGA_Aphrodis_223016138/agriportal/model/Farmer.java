package com.agriportal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Farmer (user) in the Agriculture Portal System.
 */
public class Farmer implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String phone;
    private String username;
    private String password; // in a real app store a hash, not plain text
    private final List<Field> fields = new ArrayList<Field>();

    public Farmer() { }

    public Farmer(int id, String name, String phone, String username, String password) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    // convenience factory for registration (id assigned later)
    public static Farmer register(String name, String phone, String username, String password) {
        return new Farmer(0, name, phone, username, password);
    }

    // Basic business methods
    public void addField(Field field) {
        if (field == null) throw new IllegalArgumentException("field cannot be null");
        fields.add(field);
        ((Field) fields).setOwner(this);
    }

    public boolean removeField(Field field) {
        if (field == null) return false;
        boolean removed = fields.remove(field);
        if (removed) field.setOwner(null);
        return removed;
    }

    // validation helper
    public boolean isValidForRegistration() {
        // Replace isBlank (Java 11) with null/trim/empty checks compatible with older JDKs
        if (name == null || name.trim().length() == 0) return false;
        if (username == null || username.trim().length() == 0) return false;
        if (password == null || password.length() < 6) return false;
        return true;
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Field> getFields() { return fields; }

    @Override
    public String toString() {
        return "Farmer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", fieldsCount=" + fields.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Farmer farmer = (Farmer) o;
        return id == farmer.id && Objects.equals(username, farmer.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
