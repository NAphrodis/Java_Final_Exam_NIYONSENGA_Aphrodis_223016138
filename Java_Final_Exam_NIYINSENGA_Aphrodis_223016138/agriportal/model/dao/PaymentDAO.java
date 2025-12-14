package com.agriportal.model.dao;

import com.agriportal.model.Payment;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public PaymentDAO() {}

    // ===== CREATE TABLE =====
    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS payments (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "order_id INT," +
                "amount DOUBLE," +
                "method VARCHAR(50)," +
                "status VARCHAR(50)," +
                "paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        Connection c = null;
        Statement s = null;
        try {
            c = DatabaseConnection.getConnection();
            s = c.createStatement();
            s.execute(sql);
        } finally {
            if (s != null) try { s.close(); } catch (SQLException ex) {}
            if (c != null) try { c.close(); } catch (SQLException ex) {}
        }
    }

    // ===== INSERT =====
    public int insert(Payment p) throws SQLException {
        String sql = "INSERT INTO payments (order_id, amount, method, status, paid_at) VALUES (?, ?, ?, ?, ?)";
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DatabaseConnection.getConnection();
            ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (p.getOrderId() != null) ps.setInt(1, p.getOrderId()); else ps.setNull(1, Types.INTEGER);
            ps.setDouble(2, p.getAmount());
            ps.setString(3, p.getMethod());
            ps.setString(4, p.getStatus());
            ps.setTimestamp(5, p.getPaidAt());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                p.setId(id);
                rs.close();
                return id;
            }
            if (rs != null) rs.close();
            return -1;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ex) {}
            if (c != null) try { c.close(); } catch (SQLException ex) {}
        }
    }

    // ===== FIND BY ID =====
    public Payment findById(int id) throws SQLException {
        String sql = "SELECT * FROM payments WHERE id = ?";
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DatabaseConnection.getConnection();
            ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (ps != null) try { ps.close(); } catch (SQLException ex) {}
            if (c != null) try { c.close(); } catch (SQLException ex) {}
        }
    }

    // ===== FIND ALL =====
    public List<Payment> findAll() throws SQLException {
        String sql = "SELECT * FROM payments ORDER BY paid_at DESC";
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Payment> out = new ArrayList<Payment>();
        try {
            c = DatabaseConnection.getConnection();
            ps = c.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) out.add(mapRow(rs));
            return out;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (ps != null) try { ps.close(); } catch (SQLException ex) {}
            if (c != null) try { c.close(); } catch (SQLException ex) {}
        }
    }

    // ===== UPDATE =====
    public boolean update(Payment p) throws SQLException {
        String sql = "UPDATE payments SET order_id=?, amount=?, method=?, status=?, paid_at=? WHERE id=?";
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DatabaseConnection.getConnection();
            ps = c.prepareStatement(sql);
            if (p.getOrderId() != null) ps.setInt(1, p.getOrderId()); else ps.setNull(1, Types.INTEGER);
            ps.setDouble(2, p.getAmount());
            ps.setString(3, p.getMethod());
            ps.setString(4, p.getStatus());
            ps.setTimestamp(5, p.getPaidAt());
            ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ex) {}
            if (c != null) try { c.close(); } catch (SQLException ex) {}
        }
    }

    // ===== DELETE =====
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM payments WHERE id = ?";
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DatabaseConnection.getConnection();
            ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ex) {}
            if (c != null) try { c.close(); } catch (SQLException ex) {}
        }
    }

    // ===== MAP RESULTSET TO OBJECT =====
    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id"));
        int oid = rs.getInt("order_id");
        if (!rs.wasNull()) p.setOrderId(oid);
        p.setAmount(rs.getDouble("amount"));
        p.setMethod(rs.getString("method"));
        p.setStatus(rs.getString("status"));
        p.setPaidAt(rs.getTimestamp("paid_at"));
        return p;
    }
}
