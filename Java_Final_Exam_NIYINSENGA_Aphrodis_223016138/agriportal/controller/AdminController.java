package com.agriportal.controller;

import com.agriportal.model.Admin;
import com.agriportal.model.dao.AdminDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;


public class AdminController extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private AdminDAO adminDAO;

    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnReset;

    public AdminController() {
        adminDAO = new AdminDAO();
        setLayout(new BorderLayout());

        // === Search Panel ===
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.add(new JLabel("🔍 Search by ID or Name:"));
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
        model = new DefaultTableModel(new Object[]{"ID", "Username", "Name"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // === Buttons ===
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton addBtn = new JButton("Add Admin");
        JButton updateBtn = new JButton("Update Admin");
        JButton deleteBtn = new JButton("Delete Admin");
        JButton refreshBtn = new JButton("Refresh");

        styleButton(addBtn, new Color(46, 139, 87));
        styleButton(updateBtn, new Color(70, 130, 180));
        styleButton(deleteBtn, new Color(220, 20, 60));
        styleButton(refreshBtn, new Color(105, 105, 105));

        btns.add(addBtn);
        btns.add(updateBtn);
        btns.add(deleteBtn);
        btns.add(refreshBtn);
        add(btns, BorderLayout.SOUTH);

        // === Listeners ===
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchAdmins();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addAdminDialog();
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAdminDialog();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedAdmin();
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });

        load();
    }

    /** Load admin list into the table */
    public void load() {
        model.setRowCount(0);
        try {
            List<Admin> list = adminDAO.findAll();
            for (Admin a : list) {
                model.addRow(new Object[]{a.getId(), a.getUsername(), a.getName()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load error: " + ex.getMessage());
        }
    }

    /** Search admins by name or ID */
    private void searchAdmins() {
        String term = txtSearch.getText().trim();
        if (term.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a name or ID first.");
            return;
        }
        model.setRowCount(0);
        try {
            List<Admin> list = adminDAO.findByNameOrId(term);
            for (Admin a : list) {
                model.addRow(new Object[]{a.getId(), a.getUsername(), a.getName()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
        }
    }

    /** Add new admin */
    private void addAdminDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField nameField = new JTextField();

        Object[] fields = {
                "Username:", usernameField,
                "Password (min 6):", passField,
                "Name:", nameField
        };

        int res = JOptionPane.showConfirmDialog(this, fields, "Add Admin", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String username = usernameField.getText().trim();
        String password = new String(passField.getPassword());
        String name = nameField.getText().trim();

        if (username.isEmpty() || password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Username and password (min 6) required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Admin existing = adminDAO.findByUsername(username);
            if (existing != null) {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Admin a = new Admin();
            a.setUsername(username);
            a.setPassword(password);
            a.setName(name.isEmpty() ? null : name);
            adminDAO.insert(a);
            load();
            JOptionPane.showMessageDialog(this, "Admin created successfully.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to create admin: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Update selected admin info */
    private void updateAdminDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an admin first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String currentUsername = (String) model.getValueAt(row, 1);
        String currentName = (String) model.getValueAt(row, 2);

        JTextField usernameField = new JTextField(currentUsername);
        JPasswordField passField = new JPasswordField();
        JTextField nameField = new JTextField(currentName);

        Object[] fields = {
                "Username:", usernameField,
                "New Password (optional):", passField,
                "Name:", nameField
        };

        int res = JOptionPane.showConfirmDialog(this, fields, "Update Admin", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String username = usernameField.getText().trim();
        String password = new String(passField.getPassword());
        String name = nameField.getText().trim();

        try {
            Admin a = adminDAO.findById(id);
            if (a == null) {
                JOptionPane.showMessageDialog(this, "Admin not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            a.setUsername(username);
            if (!password.isEmpty()) a.setPassword(password);
            a.setName(name);

            boolean ok = adminDAO.update(a);
            if (ok) {
                load();
                JOptionPane.showMessageDialog(this, "Admin updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Admin update failed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating admin: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Delete selected admin */
    private void deleteSelectedAdmin() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an admin first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete admin ID " + id + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = adminDAO.delete(id);
            if (ok) {
                load();
                JOptionPane.showMessageDialog(this, "Admin deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Admin not deleted.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete admin: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Style utility */
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Admin Management");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new AdminController());
        f.setSize(850, 450);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
