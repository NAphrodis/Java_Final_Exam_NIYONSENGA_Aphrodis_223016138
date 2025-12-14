package com.agriportal.model.dao;

import com.agriportal.model.Product;
import com.agriportal.model.Farmer;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal ProductDAO with findById, findAll, update, insert, createTable.
 */
public class ProductDAO {

    private final FarmerDAO farmerDAO = new FarmerDAO();

    public ProductDAO() {}

    public Product findById(int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Product> findAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id DESC";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
    public List<Product> search(String keyword) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR category LIKE ? OR description LIKE ? ORDER BY id DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }


    public boolean update(Product p) throws SQLException {
        String sql = "UPDATE products SET name=?, description=?, price=?, quantity=?, category=?, seller_id=? WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setString(5, p.getCategory());
            if (p.getSeller() != null) ps.setInt(6, p.getSeller().getId()); else ps.setNull(6, Types.INTEGER);
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        }
    }
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }


    public int insert(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, description, price, quantity, category, seller_id) VALUES (?,?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setString(5, p.getCategory());
            if (p.getSeller() != null) ps.setInt(6, p.getSeller().getId()); else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setCategory(rs.getString("category"));
        int sid = rs.getInt("seller_id");
        if (!rs.wasNull() && sid > 0) {
            Farmer f = farmerDAO.findById(sid);
            p.setSeller(f);
        }
        return p;
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(150) NOT NULL," +
                "description TEXT NULL," +
                "price DOUBLE NOT NULL DEFAULT 0," +
                "quantity INT NOT NULL DEFAULT 0," +
                "category VARCHAR(80) NULL," +
                "seller_id INT NULL," +
                "FOREIGN KEY (seller_id) REFERENCES farmers(id) ON DELETE SET NULL" +
                ")";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        }
    }
}
