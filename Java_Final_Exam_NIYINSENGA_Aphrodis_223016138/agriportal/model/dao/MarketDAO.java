package com.agriportal.model.dao;

import com.agriportal.model.Harvest;
import com.agriportal.model.Market;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MarketDAO {

    private final HarvestDAO harvestDAO = new HarvestDAO();

    public MarketDAO() {}

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS market (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "harvest_id INT," +
                "market_name VARCHAR(150)," +
                "price DOUBLE," +
                "status VARCHAR(64)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (harvest_id) REFERENCES harvest(id) ON DELETE CASCADE" +
                ")";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }

    public boolean insert(Market m) throws SQLException {
        String sql = "INSERT INTO market (harvest_id, market_name, price, status) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (m.getHarvest() != null) ps.setInt(1, m.getHarvest().getId()); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, m.getMarketName());
            if (m.getPrice() != null) ps.setDouble(3, m.getPrice()); else ps.setNull(3, Types.DOUBLE);
            ps.setString(4, m.getStatus());
            int updated = ps.executeUpdate();
            if (updated > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) m.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(Market m) throws SQLException {
        String sql = "UPDATE market SET harvest_id=?, market_name=?, price=?, status=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (m.getHarvest() != null) ps.setInt(1, m.getHarvest().getId()); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, m.getMarketName());
            if (m.getPrice() != null) ps.setDouble(3, m.getPrice()); else ps.setNull(3, Types.DOUBLE);
            ps.setString(4, m.getStatus());
            ps.setInt(5, m.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM market WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Market findById(int id) throws SQLException {
        String sql = "SELECT * FROM market WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Market> findAll() throws SQLException {
        List<Market> out = new ArrayList<Market>();
        String sql = "SELECT * FROM market ORDER BY created_at DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) out.add(mapRow(rs));
        }
        return out;
    }

    public List<Market> findByFarmerId(int farmerId) throws SQLException {
        List<Market> out = new ArrayList<Market>();
        String sql = "SELECT m.* FROM market m " +
                "JOIN harvest h ON m.harvest_id = h.id " +
                "JOIN crop c ON h.crop_id = c.id " +
                "JOIN fields f ON c.field_id = f.id " +
                "WHERE f.farmer_id = ? ORDER BY m.created_at DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, farmerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        }
        return out;
    }

    private Market mapRow(ResultSet rs) throws SQLException {
        Market m = new Market();
        m.setId(rs.getInt("id"));
        int hid = rs.getInt("harvest_id");
        if (!rs.wasNull()) {
            Harvest h = harvestDAO.findById(hid);
            m.setHarvest(h);
        }
        m.setMarketName(rs.getString("market_name"));
        double p = rs.getDouble("price");
        if (!rs.wasNull()) m.setPrice(p);
        m.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) m.setCreatedAt(ts.toLocalDateTime());
        return m;
    }
}
