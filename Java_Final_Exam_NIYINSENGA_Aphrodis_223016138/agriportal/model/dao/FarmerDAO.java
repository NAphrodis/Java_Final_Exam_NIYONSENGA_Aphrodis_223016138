package com.agriportal.model.dao;

import com.agriportal.model.Farmer;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FarmerDAO {

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS farmers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), phone VARCHAR(50), username VARCHAR(100) UNIQUE, password VARCHAR(255)" +
                ")";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }

    public Farmer findById(int id) throws SQLException {
        String sql = "SELECT * FROM farmers WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Farmer findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM farmers WHERE username = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }
    public List<Farmer> findByNameOrId(String term) throws SQLException {
        List<Farmer> list = new ArrayList<Farmer>();
        String sql;
        boolean isNumeric = term.matches("\\d+");

        if (isNumeric) {
            sql = "SELECT * FROM farmers WHERE id = ?";
        } else {
            sql = "SELECT * FROM farmers WHERE name LIKE ?";
        }

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            if (isNumeric) {
                p.setInt(1, Integer.parseInt(term));
            } else {
                p.setString(1, "%" + term + "%");
            }

            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }


    public List<Farmer> findAll() throws SQLException {
        List<Farmer> list = new ArrayList<Farmer>();
        String sql = "SELECT * FROM farmers";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean insert(Farmer f) throws SQLException {
        String sql = "INSERT INTO farmers (name, phone, username, password) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, f.getName());
            p.setString(2, f.getPhone());
            p.setString(3, f.getUsername());
            p.setString(4, f.getPassword());
            int rows = p.executeUpdate();
            return rows > 0;  // ✅ Now returns true if inserted
        }
    }


    public boolean update(Farmer f) throws SQLException {
        String sql = "UPDATE farmers SET name=?, phone=?, username=?, password=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, f.getName());
            p.setString(2, f.getPhone());
            p.setString(3, f.getUsername());
            p.setString(4, f.getPassword());
            p.setInt(5, f.getId());
            return p.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM farmers WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            return p.executeUpdate() > 0;
        }
    }

    private Farmer mapRow(ResultSet rs) throws SQLException {
        Farmer f = new Farmer();
        f.setId(rs.getInt("id"));
        f.setName(rs.getString("name"));
        f.setPhone(rs.getString("phone"));
        f.setUsername(rs.getString("username"));
        f.setPassword(rs.getString("password"));
        return f;
    }
}
