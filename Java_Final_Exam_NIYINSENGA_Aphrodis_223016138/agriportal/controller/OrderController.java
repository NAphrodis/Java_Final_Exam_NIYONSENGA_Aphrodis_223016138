package com.agriportal.controller;

import com.agriportal.model.Order;
import com.agriportal.model.Product;
import com.agriportal.model.Farmer;
import com.agriportal.model.dao.OrderDAO;
import com.agriportal.model.dao.ProductDAO;
import com.agriportal.model.dao.PaymentDAO;
import com.agriportal.model.Payment;
import com.agriportal.view.PaymentDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class OrderController extends JPanel {

    private final JTable table;
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnMarkComplete = new JButton("Mark Completed");
    private final JButton btnAccept = new JButton("Accept Order");
    private final JButton btnReject = new JButton("Reject Order");
    private final JButton btnPay = new JButton("Make Payment");
    private final JButton btnDeleteCompleted = new JButton("Delete Completed Order"); // 🆕 new admin-only button

    private final JLabel lblTitle = new JLabel("Orders Management");

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final PaymentDAO paymentDAO = new PaymentDAO();

    private Farmer loggedFarmer; // null for admin mode

    /** Constructor for Admin mode */
    public OrderController() {
        this(null);
    }

    /** Constructor for Farmer mode */
    public OrderController(Farmer farmer) {
        this.loggedFarmer = farmer;
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        // ===== Header =====
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 102));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(lblTitle);
        topPanel.add(btnRefresh);

        if (farmer == null) {
            // === Admin view ===
            btnMarkComplete.setBackground(new Color(0, 128, 64));
            btnMarkComplete.setForeground(Color.WHITE);
            btnDeleteCompleted.setBackground(new Color(180, 40, 40));
            btnDeleteCompleted.setForeground(Color.WHITE);
            btnPay.setBackground(new Color(255, 140, 0));
            btnPay.setForeground(Color.WHITE);

            topPanel.add(btnMarkComplete);
            topPanel.add(btnPay);
            topPanel.add(btnDeleteCompleted); // 🆕 add delete button for admin
        } else {
            // === Farmer view ===
            btnAccept.setBackground(new Color(0, 102, 204));
            btnAccept.setForeground(Color.WHITE);
            btnReject.setBackground(new Color(200, 0, 0));
            btnReject.setForeground(Color.WHITE);
            btnMarkComplete.setBackground(new Color(0, 128, 64));
            btnMarkComplete.setForeground(Color.WHITE);

            topPanel.add(btnAccept);
            topPanel.add(btnReject);
            topPanel.add(btnMarkComplete);
        }

        add(topPanel, BorderLayout.NORTH);

        // ===== Table =====
        table = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Product", "Seller", "Buyer", "Qty", "Order Date", "Status", "Total Cost"}, 0
        ));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Event Listeners =====
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadOrders();
            }
        });

        btnMarkComplete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                markCompleted();
            }
        });

        btnAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateOrderStatus("Accepted");
            }
        });

        btnReject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateOrderStatus("Rejected");
            }
        });

        btnPay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                makePayment();
            }
        });

        btnDeleteCompleted.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCompletedOrder();
            }
        });


        loadOrders();
    }

    // ===== Load orders =====
    private void loadOrders() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            List<Order> orders;
            if (loggedFarmer != null)
                orders = orderDAO.findBySellerId(loggedFarmer.getId());
            else
                orders = orderDAO.findAll();

            for (Order o : orders) {
                Product p = o.getProduct();
                String seller = (p != null && p.getSeller() != null) ? p.getSeller().getName() : "Unknown";
                String prodName = (p != null) ? p.getName() : "Unknown";
                String date = (o.getOrderDate() != null) ? o.getOrderDate().format(fmt) : "";

                model.addRow(new Object[]{
                        o.getId(), prodName, seller, o.getBuyerName(),
                        o.getQuantityOrdered(), date, o.getStatus(), o.totalCost()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load orders: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ===== Farmer marks as Completed =====
    private void markCompleted() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        int id = (Integer) table.getValueAt(row, 0);
        try {
            Order o = orderDAO.findById(id);
            if (o == null) {
                JOptionPane.showMessageDialog(this, "Order not found.");
                return;
            }
            if ("Completed".equalsIgnoreCase(o.getStatus())) {
                JOptionPane.showMessageDialog(this, "Already completed.");
                return;
            }

            if (!"Paid".equalsIgnoreCase(o.getStatus())) {
                JOptionPane.showMessageDialog(this,
                        "You can only mark a Paid order as Completed.",
                        "Not Allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            o.setStatus("Completed");
            orderDAO.update(o);
            JOptionPane.showMessageDialog(this, "Order marked as completed!");
            loadOrders();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ===== Farmer Accept / Reject Orders =====
    private void updateOrderStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        int id = (Integer) table.getValueAt(row, 0);
        try {
            Order o = orderDAO.findById(id);
            if (o == null) {
                JOptionPane.showMessageDialog(this, "Order not found.");
                return;
            }

            if (loggedFarmer != null && o.getProduct() != null &&
                o.getProduct().getSeller() != null &&
                o.getProduct().getSeller().getId() != loggedFarmer.getId()) {
                JOptionPane.showMessageDialog(this, "You can only manage your own orders.");
                return;
            }

            o.setStatus(newStatus);
            orderDAO.update(o);
            JOptionPane.showMessageDialog(this, "Order marked as " + newStatus + ".");
            loadOrders();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update order: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ===== Customer Payment Logic =====
    private void makePayment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        int id = (Integer) table.getValueAt(row, 0);
        String status = table.getValueAt(row, 6).toString();
        double amount = Double.parseDouble(table.getValueAt(row, 7).toString());

        if (!"Accepted".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                    "You can only pay for orders that have been accepted by the farmer.",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PaymentDialog dlg = new PaymentDialog(SwingUtilities.getWindowAncestor(this), id, amount, "Customer");
        dlg.setVisible(true);
        if (dlg.isPaymentSuccessful()) {
            try {
                Order o = orderDAO.findById(id);
                o.setStatus("Paid");
                orderDAO.update(o);

                Payment p = new Payment();
                p.setOrderId(id);
                p.setAmount(amount);
                p.setMethod("CARD");
                p.setStatus("COMPLETED");
                p.setPaidAt(new Timestamp(System.currentTimeMillis()));
                paymentDAO.insert(p);

                JOptionPane.showMessageDialog(this, "Payment successful! Order marked as Paid.");
                loadOrders();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to record payment: " + ex.getMessage(),
                        "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ===== 🆕 Admin-only: Delete Completed Order =====
    	private void deleteCompletedOrder() {
    	    int row = table.getSelectedRow();
    	    if (row < 0) {
    	        JOptionPane.showMessageDialog(this, "Select an order to delete.");
    	        return;
    	    }

    	    int id = (Integer) table.getValueAt(row, 0);
    	    String status = table.getValueAt(row, 6).toString();

    	    if (!"Completed".equalsIgnoreCase(status)) {
    	        JOptionPane.showMessageDialog(this,
    	                "Only Completed orders can be deleted (for record integrity).",
    	                "Not Allowed", JOptionPane.WARNING_MESSAGE);
    	        return;
    	    }

    	    int confirm = JOptionPane.showConfirmDialog(this,
    	            "Are you sure you want to permanently delete this Completed order (ID: " + id + ")?",
    	            "Confirm Delete", JOptionPane.YES_NO_OPTION);
    	    if (confirm != JOptionPane.YES_OPTION) return;

    	    try {
    	        orderDAO.delete(id);
    	        JOptionPane.showMessageDialog(this, "Completed order deleted successfully.");
    	        loadOrders();
    	    } catch (SQLException ex) {
    	        JOptionPane.showMessageDialog(this, "Failed to delete order: " + ex.getMessage(),
    	                "DB Error", JOptionPane.ERROR_MESSAGE);
    	        ex.printStackTrace();
    	    }
    	}

    public JTable getTable() {
        return table;
    }
}
