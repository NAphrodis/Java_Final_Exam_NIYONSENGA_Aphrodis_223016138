package com.agriportal.model.dao;

import com.agriportal.model.Admin;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AdminDAO {

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS admins (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(100) UNIQUE, password VARCHAR(255), name VARCHAR(255)" +
                ")";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }

    public Admin findById(int id) throws SQLException {
        String sql = "SELECT * FROM admins WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Admin findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM admins WHERE username = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Admin> findAll() throws SQLException {
        List<Admin> list = new ArrayList<Admin>();
        String sql = "SELECT * FROM admins ORDER BY id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public int insert(Admin a) throws SQLException {
        String sql = "INSERT INTO admins (username, password, name) VALUES (?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, a.getUsername());
            p.setString(2, a.getPassword());
            p.setString(3, a.getName());
            p.executeUpdate();
            try (ResultSet g = p.getGeneratedKeys()) {
                if (g.next()) {
                    a.setId(g.getInt(1));
                    return a.getId();
                }
            }
        }
        return -1;
    }

    
    
    public boolean update(Admin a) throws SQLException {
        if (a == null || a.getId() <= 0) return false;
        String sql = "UPDATE admins SET username = ?, password = ?, name = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, a.getUsername());
            p.setString(2, a.getPassword());
            p.setString(3, a.getName());
            p.setInt(4, a.getId());
            int affected = p.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM admins WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            return p.executeUpdate() > 0;
        }
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getInt("id"));
        a.setUsername(rs.getString("username"));
        a.setPassword(rs.getString("password"));
        a.setName(rs.getString("name"));
        return a;
    }
    
    
    public List<Admin> findByNameOrId(String term) throws SQLException {
        List<Admin> list = new ArrayList<Admin>();
        String sql;
        boolean isNumber = term.matches("\\d+");

        if (isNumber) {
            sql = "SELECT * FROM admins WHERE id = ?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement p = c.prepareStatement(sql)) {
                p.setInt(1, Integer.parseInt(term));
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
        } else {
            sql = "SELECT * FROM admins WHERE name LIKE ? OR username LIKE ?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement p = c.prepareStatement(sql)) {
                p.setString(1, "%" + term + "%");
                p.setString(2, "%" + term + "%");
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

}
