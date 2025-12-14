package com.agriportal.controller;

import com.agriportal.model.Customer;
import com.agriportal.model.Farmer;
import com.agriportal.model.dao.CustomerDAO;
import com.agriportal.model.dao.FarmerDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;


public class RegisterController extends JFrame {

    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JTextField txtFullName = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JComboBox<String> cmbRole = new JComboBox<>(new String[]{"CUSTOMER", "FARMER"});
    private final JButton btnRegister = new JButton("Register");
    private final JButton btnBack = new JButton("Back");

    public RegisterController() {
        super("AgriPortal - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // === Layout Setup ===
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 247, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel lblTitle = new JLabel("Create New Account", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 51, 102));
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy++; gbc.gridx = 0; mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; mainPanel.add(txtUsername, gbc);

        gbc.gridy++; gbc.gridx = 0; mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; mainPanel.add(txtPassword, gbc);

        gbc.gridy++; gbc.gridx = 0; mainPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; mainPanel.add(txtFullName, gbc);

        gbc.gridy++; gbc.gridx = 0; mainPanel.add(new JLabel("Email / Phone:"), gbc);
        gbc.gridx = 1; mainPanel.add(txtEmail, gbc);

        gbc.gridy++; gbc.gridx = 0; mainPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; mainPanel.add(cmbRole, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnRegister.setBackground(new Color(0, 150, 0));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBackground(new Color(90, 130, 180));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        buttonsPanel.add(btnRegister);
        buttonsPanel.add(btnBack);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(buttonsPanel, gbc);

        add(mainPanel);

        // === Listeners ===
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /** Handles register logic for both roles */
    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String role = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (role.equalsIgnoreCase("CUSTOMER")) {
                CustomerDAO customerDAO = new CustomerDAO();
                if (customerDAO.findByUsername(username) != null) {
                    JOptionPane.showMessageDialog(this, "Username already exists for a customer!", "Duplicate", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Customer c = new Customer();
                c.setUsername(username);
                c.setPassword(password);
                c.setName(fullName);
                c.setEmail(email);
                c.setPhone(email);
                customerDAO.insert(c);
                JOptionPane.showMessageDialog(this, "Customer account created successfully!");
                dispose();

            } else { // FARMER
                FarmerDAO farmerDAO = new FarmerDAO();
                if (farmerDAO.findByUsername(username) != null) {
                    JOptionPane.showMessageDialog(this, "Username already exists for a farmer!", "Duplicate", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Farmer f = new Farmer();
                f.setUsername(username);
                f.setPassword(password);
                f.setName(fullName);
                f.setPhone(email);
                farmerDAO.insert(f);
                JOptionPane.showMessageDialog(this, "Farmer account created successfully!");
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // === Demo ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RegisterController().setVisible(true);
            }
        });
    }
}
