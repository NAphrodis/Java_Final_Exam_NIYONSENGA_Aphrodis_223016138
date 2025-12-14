package com.agriportal.controller;

import com.agriportal.model.Field;
import com.agriportal.model.Farmer;
import com.agriportal.model.dao.FieldDAO;
import com.agriportal.model.dao.FarmerDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;


public class FarmerController extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private FarmerDAO farmerDAO;
    private FieldDAO fieldDAO;

    // Search UI
    private JTextField txtSearch;
    private JButton btnSearch, btnReset;

    public FarmerController() {
        farmerDAO = new FarmerDAO();
        fieldDAO = new FieldDAO();

        setLayout(new BorderLayout());

        // === Search Panel ===
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.add(new JLabel("🔍 Search Farmer (ID or Name):"));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnReset = new JButton("Reset");
        styleButton(btnSearch, new Color(30, 144, 255));
        styleButton(btnReset, new Color(105, 105, 105));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);
        add(searchPanel, BorderLayout.NORTH);

        // === Table ===
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Username"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // === Buttons ===
        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Add Farmer");
        JButton editBtn = new JButton("Edit Farmer");
        JButton delBtn = new JButton("Delete Farmer");
        JButton viewFieldsBtn = new JButton("View Fields");
        JButton refreshBtn = new JButton("Refresh");

        styleButton(addBtn, new Color(46, 139, 87));   // Green
        styleButton(editBtn, new Color(70, 130, 180)); // Blue
        styleButton(delBtn, new Color(220, 20, 60));   // Red
        styleButton(viewFieldsBtn, new Color(255, 165, 0)); // Orange
        styleButton(refreshBtn, new Color(105, 105, 105));  // Gray

        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(delBtn);
        buttons.add(viewFieldsBtn);
        buttons.add(refreshBtn);
        add(buttons, BorderLayout.SOUTH);

        // === Event Listeners ===
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchFarmers();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                load();
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFarmerDialog();
            }
        });

        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedFarmer();
            }
        });

        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedFarmer();
            }
        });

        viewFieldsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewFieldsForSelectedFarmer();
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });

        // === Initial load ===
        load();
    }

    /** Load all farmers into table. */
    public void load() {
        model.setRowCount(0);
        try {
            List<Farmer> list = farmerDAO.findAll();
            for (Farmer f : list) {
                model.addRow(new Object[]{f.getId(), f.getName(), f.getPhone(), f.getUsername()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load farmers: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Search farmers by ID or Name */
    private void searchFarmers() {
        String term = txtSearch.getText().trim();
        if (term.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a name or ID first.");
            return;
        }

        model.setRowCount(0);
        try {
            List<Farmer> list = farmerDAO.findByNameOrId(term);
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No results found for: " + term);
            }
            for (Farmer f : list) {
                model.addRow(new Object[]{f.getId(), f.getName(), f.getPhone(), f.getUsername()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
        }
    }

    private Farmer getSelectedFarmer() {
        int row = table.getSelectedRow();
        if (row < 0) return null;

        int id = (int) model.getValueAt(row, 0);
        try {
            return farmerDAO.findById(id);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to fetch farmer: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /** Add a new Farmer */
    private void addFarmerDialog() {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        Object[] msg = {
                "Name:", name,
                "Phone:", phone,
                "Username:", username,
                "Password:", password
        };

        int res = JOptionPane.showConfirmDialog(this, msg, "Add New Farmer", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            Farmer f = new Farmer();
            f.setName(name.getText().trim());
            f.setPhone(phone.getText().trim());
            f.setUsername(username.getText().trim());
            f.setPassword(new String(password.getPassword()));

            boolean ok = farmerDAO.insert(f);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Farmer added successfully!");
                load();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add farmer.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /** Edit selected Farmer */
    private void editSelectedFarmer() {
        Farmer f = getSelectedFarmer();
        if (f == null) {
            JOptionPane.showMessageDialog(this, "Select a farmer to edit.");
            return;
        }

        JTextField name = new JTextField(f.getName());
        JTextField phone = new JTextField(f.getPhone());
        JTextField username = new JTextField(f.getUsername());
        JPasswordField password = new JPasswordField(f.getPassword());

        Object[] msg = {
                "Name:", name,
                "Phone:", phone,
                "Username:", username,
                "Password:", password
        };

        int res = JOptionPane.showConfirmDialog(this, msg, "Edit Farmer", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            f.setName(name.getText().trim());
            f.setPhone(phone.getText().trim());
            f.setUsername(username.getText().trim());
            f.setPassword(new String(password.getPassword()));

            boolean ok = farmerDAO.update(f);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Farmer updated successfully!");
                load();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating farmer: " + ex.getMessage());
        }
    }

    /** Delete selected Farmer */
    private void deleteSelectedFarmer() {
        Farmer f = getSelectedFarmer();
        if (f == null) {
            JOptionPane.showMessageDialog(this, "Select a farmer to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete farmer " + f.getName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = farmerDAO.delete(f.getId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Farmer deleted successfully!");
                load();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting farmer: " + ex.getMessage());
        }
    }

    /** View Fields of Selected Farmer */
    private void viewFieldsForSelectedFarmer() {
        Farmer f = getSelectedFarmer();
        if (f == null) {
            JOptionPane.showMessageDialog(this, "Select a farmer to view fields.");
            return;
        }

        try {
            List<Field> list = fieldDAO.findByOwnerId(f.getId());
            StringBuilder sb = new StringBuilder();
            for (Field field : list) {
                sb.append("Field: ").append(field.getName())
                  .append(" | Location: ").append(field.getLocation())
                  .append(" | Area: ").append(field.getAreaHectares()).append(" ha\n");
            }

            if (sb.length() == 0) sb.append("No fields found for this farmer.");
            JTextArea area = new JTextArea(sb.toString(), 10, 50);
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "Fields of " + f.getName(), JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading fields: " + ex.getMessage());
        }
    }

    // === Button styling ===
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // === Standalone Test ===
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Farmer Management");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new FarmerController());
                frame.setSize(850, 500);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
