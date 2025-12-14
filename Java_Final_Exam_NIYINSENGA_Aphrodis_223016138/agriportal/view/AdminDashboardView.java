package com.agriportal.view;

import com.agriportal.controller.FieldController;
import com.agriportal.model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AdminDashboardView extends JPanel {

    private final Admin admin; // 🧩 dynamically loaded admin

    private final JButton btnManageAdmins = new JButton("🧑‍💼 Manage Admins");
    private final JButton btnManageFarmers = new JButton("👩‍🌾 Manage Farmers");
    private final JButton btnManageFields = new JButton("🌾 Manage Fields");
    private final JButton btnManageHarvests = new JButton("🪣 Manage Harvests");
    private final JButton btnManageIrrigation = new JButton("💧 Manage Irrigation");
    private final JButton btnReports = new JButton("📈 Reports");
    private final JButton btnSettings = new JButton("⚙️ Settings");
    private final JButton btnManageForecasts = new JButton("🌦️ Forecasts & Recommendations");
    private final JButton btnManagePayments = new JButton("💳 Manage Payments");

    // ✅ New Customers Button (ADDED)
    private final JButton btnManageCustomers = new JButton("🧍 Manage Customers");

    /**
     * Constructor with Admin parameter.
     */
    public AdminDashboardView(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(240, 250, 255));

        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 128, 96));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("📊 Admin Dashboard", SwingConstants.LEFT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        String adminName = (admin != null && admin.getName() != null)
                ? admin.getName()
                : "Administrator";

        JLabel lblWelcome = new JLabel("Welcome, " + adminName + " 👋", SwingConstants.RIGHT);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblWelcome.setForeground(Color.WHITE);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblWelcome, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // === CENTER PANEL (cards grid) ===
        // 🔧 Changed grid to 3x4 to fit Customers card neatly
        JPanel center = new JPanel(new GridLayout(3, 4, 12, 12));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Style all buttons
        styleButton(btnManageAdmins, new Color(30, 144, 255));       // blue
        styleButton(btnManageFarmers, new Color(46, 139, 87));       // green
        styleButton(btnManageFields, new Color(70, 130, 180));       // teal
        styleButton(btnManageHarvests, new Color(205, 92, 92));      // reddish
        styleButton(btnManageIrrigation, new Color(72, 209, 204));   // aqua
        styleButton(btnReports, new Color(255, 165, 0));             // orange
        styleButton(btnSettings, new Color(105, 105, 105));          // gray
        styleButton(btnManageForecasts, new Color(100, 149, 237));   // steel blue
        styleButton(btnManagePayments, new Color(123, 104, 238));    // violet-blue

        // ✅ Style for Customers button
        styleButton(btnManageCustomers, new Color(153, 102, 255));   // purple tone

        // Add cards with small description labels
        center.add(wrapCard(btnManageAdmins, "Manage system administrators"));
        center.add(wrapCard(btnManageFarmers, "View and manage farmers"));
        center.add(wrapCard(btnManageFields, "Manage Fields / Land"));
        center.add(wrapCard(btnManageHarvests, "Manage harvest data"));
        center.add(wrapCard(btnManageIrrigation, "View or edit irrigation schedules"));
        center.add(wrapCard(btnReports, "Generate reports & exports"));
        center.add(wrapCard(btnSettings, "Configure application settings"));
        center.add(wrapCard(btnManageForecasts, "Manage weather forecasts & crop advice"));
        center.add(wrapCard(btnManagePayments, "View and manage all payments"));
        // ✅ Add new Customers card
        center.add(wrapCard(btnManageCustomers, "View and manage customers or buyers"));

        add(center, BorderLayout.CENTER);

        // === FOOTER ===
        JLabel footer = new JLabel("© 2025 AgriPortal Rwanda – Smart Agriculture Initiative",
                SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setForeground(new Color(90, 90, 90));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        add(footer, BorderLayout.SOUTH);

        // === LOCAL BUTTON ACTIONS (only demo actions) ===
        btnManageFields.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("Manage Fields");
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.getContentPane().add(new FieldController(null));
                f.setSize(900, 550);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });

        btnManageFarmers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Farmer management window coming soon.",
                        "Manage Farmers", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnManageHarvests.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Harvest management tab can be accessed from main app.",
                        "Manage Harvests", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnManageIrrigation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Irrigation schedule management tab can be accessed from main app.",
                        "Manage Irrigation", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnReports.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Reports and analytics module coming soon.",
                        "Reports", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnManageAdmins.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Admin management interface coming soon.",
                        "Manage Admins", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnManageForecasts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Weather forecasts & recommendations management coming soon.",
                        "Forecasts", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "System settings & preferences configuration coming soon.",
                        "Settings", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnManagePayments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("Manage Payments");
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                try {
                    f.getContentPane().add(new com.agriportal.controller.PaymentController());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminDashboardView.this,
                            "Payments panel unavailable: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                f.setSize(900, 550);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });

        // ✅ Action for Manage Customers (placeholder)
        btnManageCustomers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AdminDashboardView.this,
                        "Customer management interface coming soon.",
                        "Manage Customers", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    // === Utility methods ===
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JPanel wrapCard(JButton btn, String desc) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel label = new JLabel("<html><small>" + desc + "</small></html>", SwingConstants.CENTER);
        label.setForeground(new Color(80, 80, 80));
        p.add(btn, BorderLayout.CENTER);
        p.add(label, BorderLayout.SOUTH);
        return p;
    }

    // === Listener registration (for AgriPortalApp) ===
    public void addManageAdminsListener(ActionListener l) { btnManageAdmins.addActionListener(l); }
    public void addManageFarmersListener(ActionListener l) { btnManageFarmers.addActionListener(l); }
    public void addManageFieldsListener(ActionListener l) { btnManageFields.addActionListener(l); }
    public void addManageHarvestsListener(ActionListener l) { btnManageHarvests.addActionListener(l); }
    public void addManageIrrigationListener(ActionListener l) { btnManageIrrigation.addActionListener(l); }
    public void addReportsListener(ActionListener l) { btnReports.addActionListener(l); }
    public void addSettingsListener(ActionListener l) { btnSettings.addActionListener(l); }
    public void addManageForecastsListener(ActionListener l) { btnManageForecasts.addActionListener(l); }
    public void addManagePaymentsListener(ActionListener l) { btnManagePayments.addActionListener(l); }

    // ✅ Newly added listener method for Customers
    public void addManageCustomersListener(ActionListener l) {
        btnManageCustomers.addActionListener(l);
    }

    // === Test harness ===
    public static void main(String[] args) {
        final Admin testAdmin = new Admin();
        testAdmin.setId(1);
        testAdmin.setName("NIYONSENGA Aphrodis");
        testAdmin.setUsername("admin1");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("AgriPortal - Admin Dashboard");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.getContentPane().add(new AdminDashboardView(testAdmin));
                f.setSize(950, 650);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
