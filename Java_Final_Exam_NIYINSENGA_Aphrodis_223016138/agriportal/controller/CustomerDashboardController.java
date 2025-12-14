package com.agriportal.controller;

import com.agriportal.model.Customer;
import com.agriportal.model.Order;
import com.agriportal.model.Product;
import com.agriportal.model.dao.CustomerDAO;
import com.agriportal.model.dao.OrderDAO;
import com.agriportal.model.dao.ProductDAO;
import com.agriportal.view.PaymentDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class CustomerDashboardController extends JPanel {

    private final Customer customer;
    private final JTabbedPane tabs = new JTabbedPane();
    private final JPanel dashboardPanel = new JPanel(new BorderLayout());
    private final MarketplaceController marketplaceController;

    private final DefaultTableModel ordersModel;
    private final JTable ordersTable;

    private final JButton btnPayNow = new JButton("💳 Pay for Accepted Order");
    private final JButton btnRefresh = new JButton("🔄 Refresh Orders");
    private final JButton btnCancelOrder = new JButton("❌ Cancel Selected Order");

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public CustomerDashboardController(Customer customer) {
        this.customer = customer;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 255, 250));

        // ===== Header =====
        JPanel header = buildHeaderPanel();
        add(header, BorderLayout.NORTH);

        // ===== Orders Table =====
        ordersModel = new DefaultTableModel(
                new Object[]{"ID", "Product", "Qty", "Date", "Status", "Total"},
                0
        ) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        ordersTable = new JTable(ordersModel);
        dashboardPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        // ===== Top Bar Buttons =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(new JLabel("Welcome, " + (customer != null ? customer.getName() : "Customer")));
        top.add(btnRefresh);
        top.add(btnCancelOrder);
        top.add(btnPayNow);
        dashboardPanel.add(top, BorderLayout.NORTH);

        // ===== Listeners =====
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadOrders();
            }
        });

        btnCancelOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelSelectedOrder();
            }
        });

        btnPayNow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                payForAcceptedOrder();
            }
        });

        // ===== Marketplace =====
        marketplaceController = new MarketplaceController(customer);

        // ===== Tabs =====
        tabs.addTab("🛒 My Orders", dashboardPanel);
        tabs.addTab("🌾 Marketplace", marketplaceController);
        tabs.addTab("⚙️ Edit Profile", buildProfileEditor());
        add(tabs, BorderLayout.CENTER);

        // ===== Initial Load =====
        loadOrders();
    }

    /**
     * Handles payments for accepted orders.
     */
    private void payForAcceptedOrder() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to pay.");
            return;
        }

        int id = (Integer) ordersModel.getValueAt(row, 0);
        String status = String.valueOf(ordersModel.getValueAt(row, 4));

        if (!"Accepted".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "You can only pay for accepted orders.");
            return;
        }

        try {
            Order o = orderDAO.findById(id);
            if (o == null) {
                JOptionPane.showMessageDialog(this, "Order not found.");
                return;
            }

            // Open Payment Dialog
            PaymentDialog dlg = new PaymentDialog(
                    SwingUtilities.getWindowAncestor(this),
                    o,
                    customer.getName()
            );
            dlg.setVisible(true);

            // If success, mark order as Paid
            if (dlg.isPaymentSuccessful()) {
                o.setStatus("Paid");
                orderDAO.update(o);
                JOptionPane.showMessageDialog(this,
                        "✅ Payment successful! Order marked as Paid.",
                        "Payment Success", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Payment failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Header panel with branding and logout controls.
     */
    private JPanel buildHeaderPanel() {
        final JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 128, 96));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel logo = new JLabel("🌾 AgriPortal Rwanda");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Connecting Farmers & Buyers for Sustainable Growth");
        tagline.setForeground(Color.WHITE);
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 13));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(logo);
        left.add(tagline);

        final JButton btnLogout = new JButton("Logout");
        final JButton btnExit = new JButton("Exit");
        btnLogout.setBackground(new Color(255, 140, 0));
        btnLogout.setForeground(Color.WHITE);
        btnExit.setBackground(new Color(200, 0, 0));
        btnExit.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(btnLogout);
        right.add(btnExit);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(header,
                        "Are you sure you want to logout?",
                        "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    JFrame top = (JFrame) SwingUtilities.getWindowAncestor(header);
                    if (top != null) top.dispose();
                    new LoginController().setVisible(true);
                }
            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(header,
                        "Are you sure you want to exit?",
                        "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) System.exit(0);
            }
        });

        return header;
    }

    /**
     * Loads the customer's orders table.
     */
    public void loadOrders() {
        ordersModel.setRowCount(0);
        dashboardPanel.removeAll();
        dashboardPanel.setLayout(new BorderLayout());

        // Rebuild order table view each refresh to avoid multiple summary labels
        dashboardPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(new JLabel("Welcome, " + (customer != null ? customer.getName() : "Customer")));
        top.add(btnRefresh);
        top.add(btnCancelOrder);
        top.add(btnPayNow);
        dashboardPanel.add(top, BorderLayout.NORTH);

        try {
            List<Order> list = orderDAO.findByBuyerId(customer.getId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            double totalSpent = 0;

            if (list != null) {
                for (Order o : list) {
                    Product p = o.getProduct();
                    double total = o.totalCost();
                    totalSpent += total;
                    ordersModel.addRow(new Object[]{
                            o.getId(),
                            p != null ? p.getName() : "Unknown",
                            o.getQuantityOrdered(),
                            o.getOrderDate() != null ? o.getOrderDate().format(fmt) : "",
                            o.getStatus(),
                            total
                    });
                }
            }

            JLabel summary = new JLabel("💵 Total Spent: " + totalSpent + " RWF");
            summary.setFont(new Font("SansSerif", Font.BOLD, 14));
            summary.setForeground(new Color(0, 102, 51));
            dashboardPanel.add(summary, BorderLayout.SOUTH);
            dashboardPanel.revalidate();
            dashboardPanel.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load orders: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Cancels pending orders.
     */
    private void cancelSelectedOrder() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel.");
            return;
        }

        int id = (Integer) ordersModel.getValueAt(row, 0);
        String status = String.valueOf(ordersModel.getValueAt(row, 4));

        if (!"Pending".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only pending orders can be cancelled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this order?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Order o = orderDAO.findById(id);
                if (o != null) {
                    o.setStatus("Cancelled");
                    orderDAO.update(o);
                    JOptionPane.showMessageDialog(this, "Order cancelled successfully.");
                    loadOrders();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Failed to cancel order: " + ex.getMessage(),
                        "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Builds profile editor tab.
     */
    private JPanel buildProfileEditor() {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 250, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        final JTextField txtName = new JTextField(customer.getName(), 20);
        final JTextField txtEmail = new JTextField(customer.getEmail(), 20);
        final JTextField txtPhone = new JTextField(customer.getPhone(), 20);
        final JPasswordField txtPassword = new JPasswordField(customer.getPassword(), 20);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; panel.add(txtName, gbc);
        gbc.gridy++; gbc.gridx = 0; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; panel.add(txtEmail, gbc);
        gbc.gridy++; gbc.gridx = 0; panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; panel.add(txtPhone, gbc);
        gbc.gridy++; gbc.gridx = 0; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(txtPassword, gbc);

        final JButton btnSave = new JButton("Save Profile");
        btnSave.setBackground(new Color(0, 153, 102));
        btnSave.setForeground(Color.WHITE);
        gbc.gridy++; gbc.gridx = 1; panel.add(btnSave, gbc);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                customer.setName(txtName.getText().trim());
                customer.setEmail(txtEmail.getText().trim());
                customer.setPhone(txtPhone.getText().trim());
                customer.setPassword(new String(txtPassword.getPassword()));

                try {
                    boolean ok = customerDAO.update(customer);
                    if (ok) {
                        JOptionPane.showMessageDialog(panel, "✅ Profile updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(panel, "❌ Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        return panel;
    }
}
