package com.agriportal.model.dao;

import com.agriportal.model.Crop;
import com.agriportal.model.Harvest;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HarvestDAO {

    private final CropDAO cropDAO = new CropDAO();

    public HarvestDAO() {}

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS harvest (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "crop_id INT," +
                "harvest_date DATE," +
                "quantity DOUBLE," +
                "notes TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (crop_id) REFERENCES crop(id) ON DELETE CASCADE" +
                ")";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }

    public boolean insert(Harvest h) throws SQLException {
        String sql = "INSERT INTO harvest (crop_id, harvest_date, quantity, notes) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (h.getCrop() != null) ps.setInt(1, h.getCrop().getId()); else ps.setNull(1, Types.INTEGER);
            if (h.getHarvestDate() != null) ps.setDate(2, Date.valueOf(h.getHarvestDate())); else ps.setNull(2, Types.DATE);
            if (h.getQuantity() != null) ps.setDouble(3, h.getQuantity()); else ps.setNull(3, Types.DOUBLE);
            ps.setString(4, h.getNotes());
            int updated = ps.executeUpdate();
            if (updated > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) h.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(Harvest h) throws SQLException {
        String sql = "UPDATE harvest SET crop_id=?, harvest_date=?, quantity=?, notes=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (h.getCrop() != null) ps.setInt(1, h.getCrop().getId()); else ps.setNull(1, Types.INTEGER);
            if (h.getHarvestDate() != null) ps.setDate(2, Date.valueOf(h.getHarvestDate())); else ps.setNull(2, Types.DATE);
            if (h.getQuantity() != null) ps.setDouble(3, h.getQuantity()); else ps.setNull(3, Types.DOUBLE);
            ps.setString(4, h.getNotes());
            ps.setInt(5, h.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM harvest WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Harvest findById(int id) throws SQLException {
        String sql = "SELECT * FROM harvest WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Harvest> findAll() throws SQLException {
        List<Harvest> out = new ArrayList<Harvest>();
        String sql = "SELECT * FROM harvest ORDER BY harvest_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) out.add(mapRow(rs));
        }
        return out;
    }

    public List<Harvest> findByCropId(int cropId) throws SQLException {
        List<Harvest> out = new ArrayList<Harvest>();
        String sql = "SELECT * FROM harvest WHERE crop_id = ? ORDER BY harvest_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cropId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        }
        return out;
    }

    /**
     * Find harvests belonging to crops whose fields are owned by farmerId
     */
    public List<Harvest> findByFarmerId(int farmerId) throws SQLException {
        List<Harvest> out = new ArrayList<Harvest>();
        String sql = "SELECT h.* FROM harvest h " +
                "JOIN crop c ON h.crop_id = c.id " +
                "JOIN fields f ON c.field_id = f.id " +
                "WHERE f.farmer_id = ? ORDER BY h.harvest_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, farmerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        }
        return out;
    }

    private Harvest mapRow(ResultSet rs) throws SQLException {
        Harvest h = new Harvest();
        h.setId(rs.getInt("id"));
        int cropId = rs.getInt("crop_id");
        if (!rs.wasNull()) {
            Crop crop = cropDAO.findById(cropId);
            h.setCrop(crop);
        }
        Date d = rs.getDate("harvest_date");
        if (d != null) h.setHarvestDate(d.toLocalDate());
        double q = rs.getDouble("quantity");
        if (!rs.wasNull()) h.setQuantity(q);
        h.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) h.setCreatedAt(ts.toLocalDateTime());
        return h;
    }
}
