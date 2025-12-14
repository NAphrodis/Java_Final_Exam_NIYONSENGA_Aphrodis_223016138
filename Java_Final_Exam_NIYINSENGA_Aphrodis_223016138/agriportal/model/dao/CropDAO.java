package com.agriportal.model.dao;

import com.agriportal.model.Crop;
import com.agriportal.model.Field;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CropDAO {

    private final FieldDAO fieldDAO = new FieldDAO();

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS crop (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "crop_name VARCHAR(100) NOT NULL, " +
                "variety VARCHAR(100), " +
                "planted_date DATE, " +
                "expected_harvest_date DATE, " +
                "status VARCHAR(50), " +
                "expected_yield DOUBLE, " +
                "field_id INT, " +
                "FOREIGN KEY (field_id) REFERENCES fields(id) " +
                "ON DELETE CASCADE ON UPDATE CASCADE)";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    public boolean insert(Crop c) throws SQLException {
        String sql = "INSERT INTO crop (crop_name, variety, planted_date, expected_harvest_date, status, expected_yield, field_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCropName());
            ps.setString(2, c.getVariety());
            ps.setDate(3, c.getPlantedDate() != null ? Date.valueOf(c.getPlantedDate()) : null);
            ps.setDate(4, c.getExpectedHarvestDate() != null ? Date.valueOf(c.getExpectedHarvestDate()) : null);
            ps.setString(5, c.getStatus());
            if (c.getExpectedYield() != null)
                ps.setDouble(6, c.getExpectedYield());
            else
                ps.setNull(6, Types.DOUBLE);
            if (c.getField() != null)
                ps.setInt(7, c.getField().getId());
            else
                ps.setNull(7, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
            return true;
        }
    }

    public boolean update(Crop c) throws SQLException {
        String sql = "UPDATE crop SET crop_name=?, variety=?, planted_date=?, expected_harvest_date=?, status=?, expected_yield=?, field_id=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCropName());
            ps.setString(2, c.getVariety());
            ps.setDate(3, c.getPlantedDate() != null ? Date.valueOf(c.getPlantedDate()) : null);
            ps.setDate(4, c.getExpectedHarvestDate() != null ? Date.valueOf(c.getExpectedHarvestDate()) : null);
            ps.setString(5, c.getStatus());
            if (c.getExpectedYield() != null)
                ps.setDouble(6, c.getExpectedYield());
            else
                ps.setNull(6, Types.DOUBLE);
            if (c.getField() != null)
                ps.setInt(7, c.getField().getId());
            else
                ps.setNull(7, Types.INTEGER);
            ps.setInt(8, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM crop WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Crop findById(int id) throws SQLException {
        String sql = "SELECT * FROM crop WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Crop> findAll() throws SQLException {
        List<Crop> list = new ArrayList<>();
        String sql = "SELECT * FROM crop ORDER BY planted_date DESC";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Crop> findByFarmerId(int farmerId) throws SQLException {
        List<Crop> list = new ArrayList<>();
        String sql = "SELECT c.* FROM crop c " +
                     "JOIN fields f ON c.field_id = f.id " +
                     "WHERE f.farmer_id = ? " +
                     "ORDER BY COALESCE(c.planted_date, NOW()) DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, farmerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Crop mapRow(ResultSet rs) throws SQLException {
        Crop c = new Crop();
        c.setId(rs.getInt("id"));
        c.setCropName(rs.getString("crop_name"));
        c.setVariety(rs.getString("variety"));
        Date pd = rs.getDate("planted_date");
        if (pd != null) c.setPlantedDate(pd.toLocalDate());
        Date hd = rs.getDate("expected_harvest_date");
        if (hd != null) c.setExpectedHarvestDate(hd.toLocalDate());
        c.setStatus(rs.getString("status"));
        double ey = rs.getDouble("expected_yield");
        if (!rs.wasNull()) c.setExpectedYield(ey);
        int fieldId = rs.getInt("field_id");
        if (!rs.wasNull() && fieldId > 0) {
            Field f = fieldDAO.findById(fieldId);
            if (f != null) c.setField(f);
        }
        return c;
    }
}
