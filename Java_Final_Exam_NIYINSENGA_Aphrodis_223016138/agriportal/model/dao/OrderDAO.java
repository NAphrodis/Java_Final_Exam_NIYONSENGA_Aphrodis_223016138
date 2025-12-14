package com.agriportal.model.dao;

import com.agriportal.model.Order;
import com.agriportal.model.Product;
import com.agriportal.model.Customer;
import com.agriportal.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderDAO - CRUD operations for orders, including transactional insert + stock reduce.
 */
public class OrderDAO {

    private final ProductDAO productDAO = new ProductDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public OrderDAO() {}

    public boolean insert(Order o) throws SQLException {
        String sql = "INSERT INTO orders (product_id, buyer_id, buyer_name, quantity_ordered, order_date, status) VALUES (?,?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, o.getProduct().getId());
            if (o.getBuyer() != null) ps.setInt(2, o.getBuyer().getId());
            else ps.setNull(2, Types.INTEGER);

            ps.setString(3, o.getBuyerName());
            ps.setInt(4, o.getQuantityOrdered());
            ps.setDate(5, Date.valueOf(o.getOrderDate() != null ? o.getOrderDate() : LocalDate.now()));
            ps.setString(6, o.getStatus());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) o.setId(rs.getInt(1));
            }
            return true;
        }
    }

    /**
     * Atomic operation: check product stock, reduce it and insert order in one transaction.
     * Returns true when committed; false if insufficient stock (no DB change).
     */
    public boolean insertAndReduceStock(Order o) throws SQLException {
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            // Lock product row FOR UPDATE
            String sel = "SELECT quantity, price FROM products WHERE id = ? FOR UPDATE";
            try (PreparedStatement psSel = con.prepareStatement(sel)) {
                psSel.setInt(1, o.getProduct().getId());
                try (ResultSet rs = psSel.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return false; // product not found
                    }
                    int available = rs.getInt("quantity");
                    if (available < o.getQuantityOrdered()) {
                        con.rollback();
                        return false; // insufficient stock
                    }
                }
            }

            // reduce stock
            String upd = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            try (PreparedStatement psUpd = con.prepareStatement(upd)) {
                psUpd.setInt(1, o.getQuantityOrdered());
                psUpd.setInt(2, o.getProduct().getId());
                int updated = psUpd.executeUpdate();
                if (updated != 1) {
                    con.rollback();
                    return false;
                }
            }

            // insert order (use same connection)
            String ins = "INSERT INTO orders (product_id, buyer_id, buyer_name, quantity_ordered, order_date, status) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement psIns = con.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
                psIns.setInt(1, o.getProduct().getId());
                if (o.getBuyer() != null) psIns.setInt(2, o.getBuyer().getId());
                else psIns.setNull(2, Types.INTEGER);
                psIns.setString(3, o.getBuyerName());
                psIns.setInt(4, o.getQuantityOrdered());
                psIns.setDate(5, Date.valueOf(o.getOrderDate() != null ? o.getOrderDate() : LocalDate.now()));
                psIns.setString(6, o.getStatus());
                psIns.executeUpdate();
                try (ResultSet rs = psIns.getGeneratedKeys()) {
                    if (rs.next()) o.setId(rs.getInt(1));
                }
            }

            con.commit();
            return true;

        } catch (SQLException ex) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignore) {}
            }
            throw ex;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignore) {}
            }
        }
    }

    public Order findById(int id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Order> findAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Order> findBySellerId(int sellerId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.* FROM orders o JOIN products p ON o.product_id = p.id WHERE p.seller_id = ? ORDER BY o.order_date DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
    public List<Order> findByBuyerId(int buyerId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY order_date DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean update(Order o) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getStatus());
            ps.setInt(2, o.getId());
            return ps.executeUpdate() > 0;
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setBuyerName(rs.getString("buyer_name"));
        o.setQuantityOrdered(rs.getInt("quantity_ordered"));
        Date d = rs.getDate("order_date");
        if (d != null) o.setOrderDate(d.toLocalDate());
        o.setStatus(rs.getString("status"));

        int pid = rs.getInt("product_id");
        if (!rs.wasNull() && pid > 0) {
            Product p = productDAO.findById(pid);
            o.setProduct(p);
        }

        int bid = rs.getInt("buyer_id");
        if (!rs.wasNull() && bid > 0) {
            Customer cust = customerDAO.findById(bid);
            o.setBuyer(cust);
        }

        return o;
    }
    /**
     * Delete an order by ID and any linked payment (admin cleanup).
     */
    public boolean delete(int id) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            // delete payment first if exists
            try (PreparedStatement ps1 = con.prepareStatement("DELETE FROM payments WHERE order_id = ?")) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
            }

            // then delete order
            try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM orders WHERE id = ?")) {
                ps2.setInt(1, id);
                int affected = ps2.executeUpdate();
                con.commit();
                return affected > 0;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }


    
    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "product_id INT NOT NULL," +
                "buyer_id INT NULL," +
                "buyer_name VARCHAR(150) NULL," +
                "quantity_ordered INT NOT NULL," +
                "order_date DATE NOT NULL," +
                "status VARCHAR(50) NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE," +
                "FOREIGN KEY (buyer_id) REFERENCES customers(id) ON DELETE SET NULL" +
                ")";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        }
    }


}
