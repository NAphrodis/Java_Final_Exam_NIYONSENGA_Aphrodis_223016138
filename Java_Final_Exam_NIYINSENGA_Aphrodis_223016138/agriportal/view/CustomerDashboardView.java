package com.agriportal.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerDashboardView extends JPanel {

    private JLabel lblWelcome;
    private JTable orderTable;
    private DefaultTableModel orderModel;
    private JButton btnRefresh;
    private JTextField txtName, txtEmail, txtPhone, txtUsername;

    public CustomerDashboardView() {
        // Set main layout to BorderLayout for proper component placement
        setLayout(new BorderLayout());
        setBackground(new Color(245, 255, 250)); // soft mint background

        // ====== Header ======
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 102, 102));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblWelcome = new JLabel("Welcome, Customer!", SwingConstants.CENTER);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        topPanel.add(lblWelcome, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // ====== Split panel (Profile left, Orders right) ======
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.4);
        split.setDividerLocation(400);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(true);

        // ----- Profile panel (Left side) -----
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(new Color(230, 255, 240));
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("👤 Profile Information"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Full Name
        profilePanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        txtName.setEditable(false);
        profilePanel.add(txtName, gbc);

        // Email
        gbc.gridy++;
        gbc.gridx = 0;
        profilePanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        txtEmail.setEditable(false);
        profilePanel.add(txtEmail, gbc);

        // Phone
        gbc.gridy++;
        gbc.gridx = 0;
        profilePanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        txtPhone.setEditable(false);
        profilePanel.add(txtPhone, gbc);

        // Username
        gbc.gridy++;
        gbc.gridx = 0;
        profilePanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        txtUsername.setEditable(false);
        profilePanel.add(txtUsername, gbc);

        // Add padding at the bottom
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        profilePanel.add(Box.createVerticalGlue(), gbc);

        split.setLeftComponent(profilePanel);

        // ----- Orders panel (Right side) -----
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(Color.WHITE);
        ordersPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("🛒 My Orders"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Create table model and table
        orderModel = new DefaultTableModel(
            new Object[]{"Order ID", "Product", "Qty", "Date", "Status", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        orderTable = new JTable(orderModel);
        orderTable.setRowHeight(25);
        orderTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        orderTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scroll = new JScrollPane(orderTable);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ordersPanel.add(scroll, BorderLayout.CENTER);

        // Refresh button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setBackground(new Color(0, 153, 102));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Add hover effect to button
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRefresh.setBackground(new Color(0, 173, 122));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnRefresh.setBackground(new Color(0, 153, 102));
            }
        });
        
        buttonPanel.add(btnRefresh);
        ordersPanel.add(buttonPanel, BorderLayout.SOUTH);

        split.setRightComponent(ordersPanel);
        
        // Add split pane to center of main panel
        add(split, BorderLayout.CENTER);
        
        // ====== Footer ======
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(240, 240, 240));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel footerLabel = new JLabel("© 2025 AgriPortal - Customer Dashboard", SwingConstants.CENTER);
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(100, 100, 100));
        
        footerPanel.add(footerLabel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void setWelcomeName(String name) {
        lblWelcome.setText("Welcome, " + name + "!");
    }

    public void setProfile(String name, String email, String phone, String username) {
        txtName.setText(name != null ? name : "");
        txtEmail.setText(email != null ? email : "");
        txtPhone.setText(phone != null ? phone : "");
        txtUsername.setText(username != null ? username : "");
    }
    
    public void clearProfile() {
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtUsername.setText("");
    }
    
    public void clearOrders() {
        orderModel.setRowCount(0);
    }
    
    public void addOrder(Object[] orderData) {
        orderModel.addRow(orderData);
    }

    public JTable getOrderTable() { 
        return orderTable; 
    }

    public JButton getRefreshButton() { 
        return btnRefresh; 
    }

    public DefaultTableModel getOrderModel() { 
        return orderModel; 
    }
    
    // Getter methods for text fields
    public String getCustomerName() {
        return txtName.getText();
    }
    
    public String getCustomerEmail() {
        return txtEmail.getText();
    }
    
    public String getCustomerPhone() {
        return txtPhone.getText();
    }
    
    public String getCustomerUsername() {
        return txtUsername.getText();
    }
    
    // Test method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Customer Dashboard - AgriPortal");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                CustomerDashboardView dashboard = new CustomerDashboardView();
                
                // Set test data
                dashboard.setWelcomeName("John Customer");
                dashboard.setProfile("John Doe", "john@example.com", "+250 78 123 4567", "johndoe");
                
                // Add sample orders
                dashboard.addOrder(new Object[]{"ORD001", "Tomatoes", 5, "2025-01-15", "Delivered", "RWF 25,000"});
                dashboard.addOrder(new Object[]{"ORD002", "Potatoes", 10, "2025-01-18", "Processing", "RWF 15,000"});
                dashboard.addOrder(new Object[]{"ORD003", "Carrots", 8, "2025-01-20", "Pending", "RWF 12,000"});
                
                frame.getContentPane().add(dashboard);
                frame.setSize(1100, 700);
                frame.setMinimumSize(new Dimension(900, 600));
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}