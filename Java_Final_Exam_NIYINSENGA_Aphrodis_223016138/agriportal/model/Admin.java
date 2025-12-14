package com.agriportal.model;

import java.io.Serializable;
import java.util.Objects;


public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String password; // store hashed password in production
    private String name;

    public Admin() { }

    public Admin(int id, String username, String password, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Admin{" + "id=" + id + ", username='" + username + '\'' + ", name='" + name + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Admin admin = (Admin) o;
        return id == admin.id && Objects.equals(username, admin.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
