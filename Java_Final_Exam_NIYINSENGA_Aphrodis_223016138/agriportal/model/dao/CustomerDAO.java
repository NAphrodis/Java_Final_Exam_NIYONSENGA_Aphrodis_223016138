package com.agriportal.model.dao;

import com.agriportal.model.Customer;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CustomerDAO {

    /** Find customer by username */
    public Customer findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Find by username and password (used for login) */
    public Customer findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username = ? AND password = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Find by ID */
    public Customer findById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Search by name or ID (used by search bar) */
    public List<Customer> findByNameOrId(String q) throws SQLException {
        List<Customer> list = new ArrayList<Customer>();
        String sql = "SELECT * FROM customers WHERE name LIKE ? OR CAST(id AS CHAR) = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, "%" + q + "%");
            p.setString(2, q);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    Customer cust = new Customer();
                    cust.setId(rs.getInt("id"));
                    cust.setName(rs.getString("name"));
                    cust.setUsername(rs.getString("username"));
                    cust.setEmail(rs.getString("email"));
                    cust.setPhone(rs.getString("phone"));
                    list.add(cust);
                }
            }
        }
        return list;
    }

    /** Insert new customer */
    public boolean insert(Customer c) throws SQLException {
        String sql = "INSERT INTO customers (name, email, phone, username, password) VALUES (?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getUsername());
            ps.setString(5, c.getPassword());
            int updated = ps.executeUpdate();
            if (updated > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) c.setId(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    /** Update existing customer */
    public boolean update(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name=?, email=?, phone=?, username=?, password=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getUsername());
            ps.setString(5, c.getPassword());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Delete customer by ID */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Get all customers */
    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Maps a ResultSet row to a Customer object */
    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setUsername(rs.getString("username"));
        c.setPassword(rs.getString("password"));
        return c;
    }

    /** Creates the customers table if it doesn’t exist */
    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS customers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(150) NOT NULL," +
                "email VARCHAR(150) NULL," +
                "phone VARCHAR(50) NULL," +
                "username VARCHAR(80) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL" +
                ")";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        }
    }
}
