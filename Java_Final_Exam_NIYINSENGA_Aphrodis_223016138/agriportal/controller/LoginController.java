package com.agriportal.controller;

import com.agriportal.model.Admin;
import com.agriportal.model.Customer;
import com.agriportal.model.Farmer;
import com.agriportal.model.dao.AdminDAO;
import com.agriportal.model.dao.CustomerDAO;
import com.agriportal.model.dao.FarmerDAO;
import com.agriportal.view.AgriPortalApp;
import com.agriportal.view.FarmerDashboardView;
import com.agriportal.view.LoginView;

import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginController extends JFrame {

    private final LoginView view;
    private final AdminDAO adminDAO = new AdminDAO();
    private final FarmerDAO farmerDAO = new FarmerDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public LoginController() {
        super("AgriPortal Login");
        view = new LoginView();
        getContentPane().add(view);
        setSize(420, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // attach listeners
        view.addLoginListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        view.addRegisterListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RegisterController rc = new RegisterController();
                rc.setVisible(true);
            }
        });
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();
        String role = view.getSelectedRole();

        if (username.isEmpty() || password.isEmpty() || role.equals("Select Role")) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // ================= ADMIN LOGIN =================
            if (role.equals("Admin")) {
                Admin a = adminDAO.findByUsername(username);
                if (a != null && a.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(this, "Welcome Admin " + a.getName() + "!");
                    AgriPortalApp app = new AgriPortalApp(a);
                    app.setVisible(true);
                    dispose();
                    return;
                }
            }
            // ================= FARMER LOGIN =================
            else if (role.equals("Farmer")) {
                Farmer farmer = farmerDAO.findByUsername(username);
                if (farmer != null && farmer.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(this, "Welcome Farmer " + farmer.getName() + "!");

                    // FarmerDashboardView is a JFrame — open it directly
                    FarmerDashboardView dashboard = new FarmerDashboardView(farmer);
                    dashboard.setVisible(true);

                    dispose(); // close login
                    return;
                }
            }
            // ================= CUSTOMER LOGIN =================
            else if (role.equals("Customer")) {
                Customer customer = customerDAO.findByUsername(username);
                if (customer != null && customer.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(this, "Welcome " + customer.getName() + "!");
                    JFrame custFrame = new JFrame("Customer Dashboard - " + customer.getName());
                    custFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    custFrame.getContentPane().add(new CustomerDashboardController(customer));
                    custFrame.setSize(1000, 650);
                    custFrame.setLocationRelativeTo(null);
                    custFrame.setVisible(true);
                    dispose();
                    return;
                }
            }

            // ================= INVALID =================
            JOptionPane.showMessageDialog(this,
                    "Invalid credentials or role mismatch.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ========== MAIN ==========
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginController().setVisible(true);
            }
        });
    }
}
