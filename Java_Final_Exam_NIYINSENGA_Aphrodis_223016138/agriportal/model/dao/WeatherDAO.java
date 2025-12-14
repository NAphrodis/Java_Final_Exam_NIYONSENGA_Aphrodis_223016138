package com.agriportal.model.dao;

import com.agriportal.model.WeatherInfo;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeatherDAO {

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS weather_info (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "farm_id INT NULL, " +
                "date DATE NOT NULL, " +
                "temperature_celsius DOUBLE, " +
                "rainfall_mm DOUBLE, " +
                "condition VARCHAR(100))";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }

    public boolean insert(WeatherInfo w) throws SQLException {
        String sql = "INSERT INTO weather_info (farm_id, date, temperature_celsius, rainfall_mm, condition) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (w.getFarmId() > 0) ps.setInt(1, w.getFarmId()); else ps.setNull(1, Types.INTEGER);
            ps.setDate(2, Date.valueOf(w.getDate()));
            ps.setObject(3, w.getTemperatureCelsius());
            ps.setObject(4, w.getRainfallMm());
            ps.setString(5, w.getCondition());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(WeatherInfo w) throws SQLException {
        String sql = "UPDATE weather_info SET date=?, temperature_celsius=?, rainfall_mm=?, condition=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(w.getDate()));
            ps.setObject(2, w.getTemperatureCelsius());
            ps.setObject(3, w.getRainfallMm());
            ps.setString(4, w.getCondition());
            ps.setInt(5, w.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM weather_info WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<WeatherInfo> findAll() throws SQLException {
        List<WeatherInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM weather_info ORDER BY date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private WeatherInfo mapRow(ResultSet rs) throws SQLException {
        WeatherInfo w = new WeatherInfo();
        w.setId(rs.getInt("id"));
        w.setFarmId(rs.getInt("farm_id"));
        Date d = rs.getDate("date");
        if (d != null) w.setDate(d.toLocalDate());
        w.setTemperatureCelsius(rs.getDouble("temperature_celsius"));
        w.setRainfallMm(rs.getDouble("rainfall_mm"));
        w.setCondition(rs.getString("condition"));
        return w;
    }
}
