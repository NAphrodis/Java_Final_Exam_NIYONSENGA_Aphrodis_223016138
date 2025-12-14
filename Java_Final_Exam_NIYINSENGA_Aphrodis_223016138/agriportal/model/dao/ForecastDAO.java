package com.agriportal.model.dao;

import com.agriportal.model.Forecast;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ForecastDAO - handles CRUD for forecast table.
 */
public class ForecastDAO {

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS forecasts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "forecast_date DATE NOT NULL, " +
                "weather_condition VARCHAR(100), " +
                "temperature DOUBLE, " +
                "rainfall DOUBLE, " +
                "recommendation VARCHAR(255)" +
                ")";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }

    public boolean insert(Forecast f) throws SQLException {
        String sql = "INSERT INTO forecasts (forecast_date, weather_condition, temperature, rainfall, recommendation) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(f.getDate()));
            ps.setString(2, f.getCondition());
            if (f.getTemperature() != null) ps.setDouble(3, f.getTemperature()); else ps.setNull(3, Types.DOUBLE);
            if (f.getRainfall() != null) ps.setDouble(4, f.getRainfall()); else ps.setNull(4, Types.DOUBLE);
            ps.setString(5, f.getRecommendation());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) f.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }
    public Forecast findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT * FROM forecasts WHERE forecast_date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Forecast f = new Forecast();
                f.setId(rs.getInt("id"));
                f.setDate(rs.getDate("forecast_date").toLocalDate());
                f.setCondition(rs.getString("weather_condition"));
                f.setTemperature(rs.getDouble("temperature"));
                f.setRainfall(rs.getDouble("rainfall"));
                f.setRecommendation(rs.getString("recommendation"));
                return f;
            }
            return null;
        }
    }


    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM forecasts WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Forecast> findAll() throws SQLException {
        List<Forecast> list = new ArrayList<Forecast>();
        String sql = "SELECT * FROM forecasts ORDER BY forecast_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Forecast f = new Forecast();
                f.setId(rs.getInt("id"));
                Date d = rs.getDate("forecast_date");
                if (d != null) f.setDate(d.toLocalDate());
                f.setCondition(rs.getString("weather_condition"));
                double temp = rs.getDouble("temperature");
                if (!rs.wasNull()) f.setTemperature(temp);
                double rain = rs.getDouble("rainfall");
                if (!rs.wasNull()) f.setRainfall(rain);
                f.setRecommendation(rs.getString("recommendation"));
                list.add(f);
            }
        }
        return list;
    }

	public List<Forecast> findRecent(int limit) throws SQLException {
	    List<Forecast> list = new ArrayList<Forecast>();
	    String sql = "SELECT * FROM forecasts ORDER BY forecast_date DESC LIMIT ?";
	    try (Connection c = DatabaseConnection.getConnection();
	         PreparedStatement ps = c.prepareStatement(sql)) {
	        ps.setInt(1, limit);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            Forecast f = new Forecast();
	            f.setId(rs.getInt("id"));
	            Date d = rs.getDate("forecast_date");
	            if (d != null) f.setDate(d.toLocalDate());
	            f.setCondition(rs.getString("weather_condition"));
	            double temp = rs.getDouble("temperature");
	            if (!rs.wasNull()) f.setTemperature(temp);
	            double rain = rs.getDouble("rainfall");
	            if (!rs.wasNull()) f.setRainfall(rain);
	            f.setRecommendation(rs.getString("recommendation"));
	            list.add(f);
	        }
	    }
	    return list;
	}
}

