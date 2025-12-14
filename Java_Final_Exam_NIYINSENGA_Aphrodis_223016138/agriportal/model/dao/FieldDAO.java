package com.agriportal.model.dao;

import com.agriportal.model.Field;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class FieldDAO {

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS fields (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "location VARCHAR(255), " +
                "area_ha DOUBLE DEFAULT 0, " +
                "farmer_id INT, " +
                "FOREIGN KEY (farmer_id) REFERENCES farmers(id) ON DELETE SET NULL" +
                ")";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        }
    }

    public boolean insert(Field field) throws SQLException {
        String sql = "INSERT INTO fields (name, location, area_ha, farmer_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, field.getName());
            ps.setString(2, field.getLocation());
            ps.setDouble(3, field.getAreaHectares());
            if (field.getOwner() != null)
                ps.setInt(4, field.getOwner().getId());
            else
                ps.setNull(4, Types.INTEGER);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Field field) throws SQLException {
        String sql = "UPDATE fields SET name=?, location=?, area_ha=?, farmer_id=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, field.getName());
            ps.setString(2, field.getLocation());
            ps.setDouble(3, field.getAreaHectares());
            if (field.getOwner() != null)
                ps.setInt(4, field.getOwner().getId());
            else
                ps.setNull(4, Types.INTEGER);
            ps.setInt(5, field.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM fields WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Field findById(int id) throws SQLException {
        String sql = "SELECT * FROM fields WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Field> findAll() throws SQLException {
        List<Field> list = new ArrayList<Field>();
        String sql = "SELECT * FROM fields ORDER BY id DESC";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Field> findByOwnerId(int farmerId) throws SQLException {
        List<Field> list = new ArrayList<Field>();
        String sql = "SELECT * FROM fields WHERE farmer_id = ? ORDER BY id DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, farmerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Field mapRow(ResultSet rs) throws SQLException {
        Field f = new Field();
        f.setId(rs.getInt("id"));
        f.setName(rs.getString("name"));
        f.setLocation(rs.getString("location"));
        f.setAreaHectares(rs.getDouble("area_ha"));
        int ownerId = rs.getInt("farmer_id");
        if (!rs.wasNull() && ownerId > 0) f.setOwnerId(ownerId);
        return f;
    }
}
