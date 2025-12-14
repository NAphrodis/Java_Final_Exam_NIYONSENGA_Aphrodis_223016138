package com.agriportal.main;

import com.agriportal.model.dao.*;
import com.agriportal.util.DatabaseConnection;
import com.agriportal.controller.LoginController;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Main application entry point.
 * Ensures all database tables exist before showing the login window.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    // Create all tables
                    new AdminDAO().createTable();
                    new FarmerDAO().createTable();
                    new CustomerDAO().createTable();
                    new FieldDAO().createTable();   // ✅ includes owner_id FK
                    new CropDAO().createTable();
                    new ProductDAO().createTable();
                    new OrderDAO().createTable();

                    System.out.println("✅ All tables verified/created successfully.");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "Database initialization failed: " + ex.getMessage(), 
                        "DB Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }

                // ✅ Launch the login window
                LoginController login = new LoginController();
                login.setVisible(true);
            }
        });
    }
}
