package com.agriportal.controller;

import com.agriportal.model.Customer;
import com.agriportal.model.dao.CustomerDAO;
import com.agriportal.view.MarketplaceView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class CustomerLoginController extends JFrame implements ActionListener {

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnCancel = new JButton("Clear");
    private final JButton btnRegister = new JButton("Register");

    private final CustomerDAO customerDAO = new CustomerDAO();

    public CustomerLoginController() {
        super("Customer Login - AgriPortal");
        setSize(420, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ensure table exists
        try {
            customerDAO.createTable();
        } catch (SQLException ex) {
            // Log and continue — show once
            JOptionPane.showMessageDialog(this, "Warning: Could not ensure customers table exists: " + ex.getMessage(), "DB Warning", JOptionPane.WARNING_MESSAGE);
        }

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; add(passwordField, gbc);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(btnRegister);
        p.add(btnCancel);
        p.add(btnLogin);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(p, gbc);

        btnLogin.addActionListener(this);
        btnCancel.addActionListener(this);
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // open register dialog (your RegisterController)
                RegisterController rc = new RegisterController();
                rc.setVisible(true);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Customer c = customerDAO.findByUsernameAndPassword(username, password);
                if (c != null) {
                    JOptionPane.showMessageDialog(this, "Welcome, " + c.getName() + "!");
                    // Launch a customer dashboard frame
                    JFrame f = new JFrame("Customer Dashboard - " + c.getName());
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.getContentPane().add(new com.agriportal.controller.CustomerDashboardController(c));
                    f.setSize(1000, 650);
                    f.setLocationRelativeTo(null);
                    f.setVisible(true);
                    dispose();
                    return;
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnCancel) {
            usernameField.setText("");
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CustomerLoginController cl = new CustomerLoginController();
                cl.setVisible(true);
            }
        });
    }
}
