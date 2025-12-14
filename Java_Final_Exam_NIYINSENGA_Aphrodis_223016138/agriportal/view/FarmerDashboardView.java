package com.agriportal.view;

import com.agriportal.controller.*;
import com.agriportal.model.Field;
import com.agriportal.model.Farmer;
import com.agriportal.model.Forecast;
import com.agriportal.model.dao.FieldDAO;
import com.agriportal.model.dao.ForecastDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;


public class FarmerDashboardView extends JFrame {

    private final Farmer farmer;
    private final ForecastDAO forecastDAO = new ForecastDAO();
    private final FieldDAO fieldDAO = new FieldDAO();

    private JTable tblFields;
    private JTable tblForecast;
    private JTextArea txtRecommendations;

    private JButton btnAddField = new JButton("➕ Add Field");
    private JButton btnUpdateField = new JButton("✏️ Update Field");
    private JButton btnDeleteField = new JButton("🗑️ Delete Selected");
    private JButton btnRefreshField = new JButton("🔄 Refresh");

    private JButton btnLogout = new JButton("Logout");
    private JButton btnExit = new JButton("Exit");

    public FarmerDashboardView(Farmer farmer) {
        this.farmer = farmer;
        setTitle("AgriPortal - Farmer: " + (farmer != null ? farmer.getName() : "Unknown"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        // ===== MENU BAR (NEW) =====
        setJMenuBar(createMenuBar());

        // ===== MAIN LAYOUT =====
        getContentPane().setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 128, 96));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftHeader.setOpaque(false);
        
        JLabel logo = new JLabel("🌾 AgriPortal Farmer Dashboard");
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        leftHeader.add(logo);
        
        JLabel tagline = new JLabel("  | Empowering Farmers with Smart Insights");
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 13));
        tagline.setForeground(Color.WHITE);
        leftHeader.add(tagline);

        styleButton(btnLogout, new Color(255, 140, 0));
        styleButton(btnExit, new Color(200, 0, 0));

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.setOpaque(false);
        rightHeader.add(btnLogout);
        rightHeader.add(btnExit);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        getContentPane().add(header, BorderLayout.NORTH);

        // ===== TABS =====
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        // Create controller instances with null checks
        tabs.addTab("📊 Dashboard", createDashboardTab());
        
        // Check if controllers exist before adding tabs
        try {
            tabs.addTab("🌾 My Crops", new CropController(farmer));
        } catch (Exception e) {
            tabs.addTab("🌾 My Crops", createNotAvailablePanel("Crop Management"));
        }
        
        try {
            tabs.addTab("💧 Irrigation", new IrrigationController(farmer));
        } catch (Exception e) {
            tabs.addTab("💧 Irrigation", createNotAvailablePanel("Irrigation Management"));
        }
        
        try {
            tabs.addTab("🌽 Harvest", new HarvestController(farmer));
        } catch (Exception e) {
            tabs.addTab("🌽 Harvest", createNotAvailablePanel("Harvest Management"));
        }
        
        try {
            tabs.addTab("🏪 Market", new MarketController(farmer));
        } catch (Exception e) {
            tabs.addTab("🏪 Market", createNotAvailablePanel("Market Management"));
        }
        
        try {
            tabs.addTab("📦 Products", new ProductController(farmer));
        } catch (Exception e) {
            tabs.addTab("📦 Products", createNotAvailablePanel("Product Management"));
        }
        
        try {
            tabs.addTab("📜 Orders", new OrderController(farmer));
        } catch (Exception e) {
            tabs.addTab("📜 Orders", createNotAvailablePanel("Order Management"));
        }
        
        tabs.addTab("👤 Profile", createProfilePanel());

        getContentPane().add(tabs, BorderLayout.CENTER);

        // ===== FOOTER =====
        JLabel footer = new JLabel("© 2025 AgriPortal Rwanda - Smart Agriculture Initiative", 
                                  SwingConstants.CENTER);
        footer.setForeground(new Color(90, 90, 90));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setBorder(new EmptyBorder(5, 0, 5, 0));
        getContentPane().add(footer, BorderLayout.SOUTH);

        // ===== BUTTON EVENTS =====
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                // Try to open login window
                try {
                    Class<?> loginControllerClass = Class.forName("com.agriportal.controller.LoginController");
                    Object controller = loginControllerClass.getDeclaredConstructor().newInstance();
                    if (controller instanceof JFrame) {
                        ((JFrame) controller).setVisible(true);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FarmerDashboardView.this,
                        "Login window not available. Please restart the application.",
                        "Logout Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(FarmerDashboardView.this,
                    "Are you sure you want to exit AgriPortal?",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // ===== Initial Load =====
        loadForecastTable();
        refreshFieldsTable();
    }

    /**
     * ✅ Creates a menu bar like Admin Dashboard
     */
    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0, 128, 96));
        menuBar.setBorderPainted(false);

        // === Edit Menu ===
        JMenu editMenu = new JMenu("Edit");
        editMenu.setForeground(Color.WHITE);
        editMenu.setFont(new Font("SansSerif", Font.BOLD, 13));

        JMenuItem refreshItem = new JMenuItem("🔄 Refresh Dashboard");
        JMenuItem profileItem = new JMenuItem("👤 Edit Profile");

        refreshItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshFieldsTable();
                loadForecastTable();
                JOptionPane.showMessageDialog(FarmerDashboardView.this,
                    "Dashboard refreshed successfully!",
                    "Refreshed", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        profileItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(FarmerDashboardView.this,
                    "Profile editing is available in the 'Profile' tab.",
                    "Edit Profile", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        editMenu.add(refreshItem);
        editMenu.add(profileItem);

        // === Help Menu ===
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(new Font("SansSerif", Font.BOLD, 13));

        JMenuItem aboutItem = new JMenuItem("ℹ️ About AgriPortal");
        JMenuItem supportItem = new JMenuItem("📧 Contact Support");

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(FarmerDashboardView.this,
                    "AgriPortal Rwanda v1.0\nSmart Agriculture Information System\n© 2025 All Rights Reserved.",
                    "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        supportItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(FarmerDashboardView.this,
                    "For support, contact: support@agriportal.rw",
                    "Support", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        helpMenu.add(aboutItem);
        helpMenu.add(supportItem);

        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createDashboardTab() {
        JPanel dashboard = new JPanel(new BorderLayout(10, 10));
        dashboard.setBorder(new EmptyBorder(10, 10, 10, 10));
        dashboard.setBackground(new Color(240, 248, 255));

        JLabel lblTitle = new JLabel("📊 My Farms Overview");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(new Color(0, 100, 0));
        dashboard.add(lblTitle, BorderLayout.NORTH);

        // ===== Fields Panel =====
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("🌾 My Fields"));

        // Create table model
        DefaultTableModel fieldsModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Location", "Area (ha)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        tblFields = new JTable(fieldsModel);
        tblFields.setRowHeight(25);
        tblFields.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane scrollFields = new JScrollPane(tblFields);

        // Style buttons
        styleButton(btnAddField, new Color(46, 139, 87));      // Green
        styleButton(btnUpdateField, new Color(30, 144, 255));  // Blue
        styleButton(btnDeleteField, new Color(205, 92, 92));   // Red
        styleButton(btnRefreshField, new Color(255, 165, 0));  // Orange

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btnPanel.setBackground(new Color(240, 248, 255));
        btnPanel.add(btnAddField);
        btnPanel.add(btnUpdateField);
        btnPanel.add(btnDeleteField);
        btnPanel.add(btnRefreshField);

        leftPanel.add(btnPanel, BorderLayout.NORTH);
        leftPanel.add(scrollFields, BorderLayout.CENTER);

        // ===== Right Weather + Recommendations =====
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Weather Panel
        JPanel weatherPanel = new JPanel(new BorderLayout());
        weatherPanel.setBorder(BorderFactory.createTitledBorder("🌦 Weather Forecast"));
        DefaultTableModel forecastModel = new DefaultTableModel(
            new Object[]{"Date", "Condition", "Temp (°C)", "Rain (mm)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblForecast = new JTable(forecastModel);
        tblForecast.setRowHeight(25);
        JScrollPane scrollForecast = new JScrollPane(tblForecast);
        weatherPanel.add(scrollForecast, BorderLayout.CENTER);

        // Recommendations Panel
        JPanel recommendationPanel = new JPanel(new BorderLayout());
        recommendationPanel.setBorder(BorderFactory.createTitledBorder("🌱 Recommendations"));
        txtRecommendations = new JTextArea();
        txtRecommendations.setLineWrap(true);
        txtRecommendations.setWrapStyleWord(true);
        txtRecommendations.setEditable(false);
        txtRecommendations.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtRecommendations.setBackground(new Color(255, 255, 240));
        JScrollPane scrollRecommendations = new JScrollPane(txtRecommendations);
        recommendationPanel.add(scrollRecommendations, BorderLayout.CENTER);

        rightPanel.add(weatherPanel, BorderLayout.NORTH);
        rightPanel.add(recommendationPanel, BorderLayout.CENTER);

        // Split pane for left and right panels
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(550);
        split.setOneTouchExpandable(true);
        dashboard.add(split, BorderLayout.CENTER);

        // ===== Buttons actions =====
        btnRefreshField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshFieldsTable();
            }
        });

        btnAddField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFieldDialog();
            }
        });

        btnUpdateField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFieldDialog();
            }
        });

        btnDeleteField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedField();
            }
        });

        return dashboard;
    }

    private JPanel createProfilePanel() {
        JPanel profile = new JPanel(new GridBagLayout());
        profile.setBackground(new Color(245, 255, 250));
        profile.setBorder(new EmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Name
        profile.add(createProfileLabel("👤 Name:"), gbc);
        gbc.gridx = 1;
        profile.add(createProfileValue(safeString(farmer.getName())), gbc);

        // Username
        gbc.gridy++;
        gbc.gridx = 0;
        profile.add(createProfileLabel("📧 Username:"), gbc);
        gbc.gridx = 1;
        profile.add(createProfileValue(safeString(farmer.getUsername())), gbc);

        // Contact
        gbc.gridy++;
        gbc.gridx = 0;
        profile.add(createProfileLabel("📞 Contact:"), gbc);
        gbc.gridx = 1;
        profile.add(createProfileValue(safeString(farmer.getPhone())), gbc);

        // Farmer ID
        gbc.gridy++;
        gbc.gridx = 0;
        profile.add(createProfileLabel("🆔 Farmer ID:"), gbc);
        gbc.gridx = 1;
        profile.add(createProfileValue(String.valueOf(farmer.getId())), gbc);

        return profile;
    }

    private JLabel createProfileLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(0, 102, 102));
        return label;
    }

    private JLabel createProfileValue(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        label.setBackground(Color.WHITE);
        label.setOpaque(true);
        return label;
    }

    private JPanel createNotAvailablePanel(String moduleName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 250, 240));
        
        JLabel label = new JLabel(moduleName + " module is not available", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.ITALIC, 14));
        label.setForeground(new Color(160, 160, 160));
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private String safeString(String s) {
        return s == null ? "Not provided" : s;
    }

    private void styleButton(final JButton btn, final Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
    }

    private void loadForecastTable() {
        try {
            Forecast today = forecastDAO.findByDate(LocalDate.now());
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Date");
            model.addColumn("Condition");
            model.addColumn("Temp (°C)");
            model.addColumn("Rain (mm)");

            if (today != null) {
                Vector row = new Vector();
                row.add(today.getDate());
                row.add(today.getCondition());
                row.add(today.getTemperature());
                row.add(today.getRainfall());
                model.addRow(row);
                txtRecommendations.setText(today.getRecommendation());
            } else {
                // Add sample data for demo
                Vector row = new Vector();
                row.add(LocalDate.now().toString());
                row.add("Partly Cloudy");
                row.add("25°C");
                row.add("2mm");
                model.addRow(row);
                txtRecommendations.setText("Good day for planting leafy vegetables. " +
                    "Water your fields in the morning for best results.");
            }
            tblForecast.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading forecast: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshFieldsTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) tblFields.getModel();
            model.setRowCount(0);
            
            if (farmer == null) {
                model.addRow(new Object[]{"-", "No farmer data", "", ""});
                return;
            }
            
            List<Field> fields = fieldDAO.findByOwnerId(farmer.getId());
            
            if (fields.isEmpty()) {
                model.addRow(new Object[]{"-", "No fields found. Use 'Add Field' to register one.", "", ""});
            } else {
                for (Field f : fields) {
                    model.addRow(new Object[]{
                        f.getId(), 
                        f.getName(), 
                        f.getLocation(), 
                        String.format("%.2f", f.getAreaHectares())
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading fields: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getSelectedFieldId() {
        int row = tblFields.getSelectedRow();
        if (row < 0) return -1;
        Object val = tblFields.getValueAt(row, 0);
        if (val == null) return -1;
        
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void addFieldDialog() {
        JTextField nameField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField areaField = new JTextField(20);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        panel.add(new JLabel("Field Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        panel.add(locationField, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Area (hectares):"), gbc);
        gbc.gridx = 1;
        panel.add(areaField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Add New Field", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Field field = new Field();
                field.setName(nameField.getText().trim());
                field.setLocation(locationField.getText().trim());
                
                try {
                    double area = Double.parseDouble(areaField.getText().trim());
                    field.setAreaHectares(area);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a valid number for area.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                field.setOwner(farmer);
                fieldDAO.insert(field);
                refreshFieldsTable();
                JOptionPane.showMessageDialog(this, 
                    "Field added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error adding field: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateFieldDialog() {
        int id = getSelectedFieldId();
        if (id < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a field to update.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Field field = fieldDAO.findById(id);
            if (field == null) {
                JOptionPane.showMessageDialog(this, 
                    "Field not found.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JTextField nameField = new JTextField(field.getName(), 20);
            JTextField locationField = new JTextField(field.getLocation(), 20);
            JTextField areaField = new JTextField(String.valueOf(field.getAreaHectares()), 20);
            
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            
            panel.add(new JLabel("Field Name:"), gbc);
            gbc.gridx = 1;
            panel.add(nameField, gbc);
            
            gbc.gridy++;
            gbc.gridx = 0;
            panel.add(new JLabel("Location:"), gbc);
            gbc.gridx = 1;
            panel.add(locationField, gbc);
            
            gbc.gridy++;
            gbc.gridx = 0;
            panel.add(new JLabel("Area (hectares):"), gbc);
            gbc.gridx = 1;
            panel.add(areaField, gbc);
            
            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Update Field ID: " + id, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                field.setName(nameField.getText().trim());
                field.setLocation(locationField.getText().trim());
                
                try {
                    double area = Double.parseDouble(areaField.getText().trim());
                    field.setAreaHectares(area);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a valid number for area.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                fieldDAO.update(field);
                refreshFieldsTable();
                JOptionPane.showMessageDialog(this, 
                    "Field updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error updating field: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedField() {
        int id = getSelectedFieldId();
        if (id < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a field to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete field ID " + id + "?\nThis action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                fieldDAO.delete(id);
                refreshFieldsTable();
                JOptionPane.showMessageDialog(this, 
                    "Field deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting field: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        final Farmer f = new Farmer();
        f.setId(1);
        f.setName("Test Farmer");
        f.setUsername("farmer1");
        f.setPhone("0780000000");
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                FarmerDashboardView frame = new FarmerDashboardView(f);
                frame.setVisible(true);
            }
        });
    }
}