package com.agriportal.model.dao;

import com.agriportal.model.Field;
import com.agriportal.model.IrrigationSchedule;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for irrigation_schedule table
 */
public class IrrigationScheduleDAO {

    private final FieldDAO fieldDAO = new FieldDAO();

    public IrrigationScheduleDAO() {}

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS irrigation_schedule (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "field_id INT," +
                "schedule_date DATE," +
                "duration_hours DOUBLE," +
                "water_volume DOUBLE," +
                "notes TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (field_id) REFERENCES fields(id) ON DELETE CASCADE" +
                ")";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }

    public boolean insert(IrrigationSchedule s) throws SQLException {
        String sql = "INSERT INTO irrigation_schedule (field_id, schedule_date, duration_hours, water_volume, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (s.getField() != null) ps.setInt(1, s.getField().getId()); else ps.setNull(1, Types.INTEGER);
            if (s.getScheduleDate() != null) ps.setDate(2, Date.valueOf(s.getScheduleDate())); else ps.setNull(2, Types.DATE);
            if (s.getDurationHours() != null) ps.setDouble(3, s.getDurationHours()); else ps.setNull(3, Types.DOUBLE);
            if (s.getWaterVolume() != null) ps.setDouble(4, s.getWaterVolume()); else ps.setNull(4, Types.DOUBLE);
            ps.setString(5, s.getNotes());
            int updated = ps.executeUpdate();
            if (updated > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) s.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(IrrigationSchedule s) throws SQLException {
        String sql = "UPDATE irrigation_schedule SET field_id=?, schedule_date=?, duration_hours=?, water_volume=?, notes=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (s.getField() != null) ps.setInt(1, s.getField().getId()); else ps.setNull(1, Types.INTEGER);
            if (s.getScheduleDate() != null) ps.setDate(2, Date.valueOf(s.getScheduleDate())); else ps.setNull(2, Types.DATE);
            if (s.getDurationHours() != null) ps.setDouble(3, s.getDurationHours()); else ps.setNull(3, Types.DOUBLE);
            if (s.getWaterVolume() != null) ps.setDouble(4, s.getWaterVolume()); else ps.setNull(4, Types.DOUBLE);
            ps.setString(5, s.getNotes());
            ps.setInt(6, s.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM irrigation_schedule WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public IrrigationSchedule findById(int id) throws SQLException {
        String sql = "SELECT * FROM irrigation_schedule WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<IrrigationSchedule> findAll() throws SQLException {
        List<IrrigationSchedule> out = new ArrayList<IrrigationSchedule>();
        String sql = "SELECT * FROM irrigation_schedule ORDER BY schedule_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) out.add(mapRow(rs));
        }
        return out;
    }

    /**
     * Find schedules for fields owned by specified farmer (join fields -> farmer_id)
     */
    public List<IrrigationSchedule> findByFarmerId(int farmerId) throws SQLException {
        List<IrrigationSchedule> out = new ArrayList<IrrigationSchedule>();
        String sql = "SELECT isch.* FROM irrigation_schedule isch " +
                     "JOIN fields f ON isch.field_id = f.id " +
                     "WHERE f.farmer_id = ? ORDER BY isch.schedule_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, farmerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
            }
        }
        return out;
    }

    private IrrigationSchedule mapRow(ResultSet rs) throws SQLException {
        IrrigationSchedule s = new IrrigationSchedule();
        s.setId(rs.getInt("id"));
        int fid = rs.getInt("field_id");
        if (!rs.wasNull()) {
            Field f = fieldDAO.findById(fid);
            s.setField(f);
        }
        Date d = rs.getDate("schedule_date");
        if (d != null) s.setScheduleDate(d.toLocalDate());
        double dur = rs.getDouble("duration_hours");
        if (!rs.wasNull()) s.setDurationHours(dur);
        double vol = rs.getDouble("water_volume");
        if (!rs.wasNull()) s.setWaterVolume(vol);
        s.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
        return s;
    }
}
