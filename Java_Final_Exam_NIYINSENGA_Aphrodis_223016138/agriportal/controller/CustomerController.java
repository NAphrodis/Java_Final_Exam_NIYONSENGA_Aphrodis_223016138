package com.agriportal.controller;

import com.agriportal.model.Customer;
import com.agriportal.model.dao.CustomerDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;


public class CustomerController extends JPanel {

    private final CustomerDAO dao = new CustomerDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField txtSearch = new JTextField(20);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnAdd = new JButton("Add Customer");
    private final JButton btnUpdate = new JButton("Update Customer");
    private final JButton btnDelete = new JButton("Delete Customer");

    public CustomerController() {
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(245, 255, 250));

        // === Header (Search Panel) ===
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(240, 255, 240));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("🔍 Search Customer by Name or ID:"));
        header.add(txtSearch);
        header.add(btnSearch);
        header.add(btnRefresh);
        add(header, BorderLayout.NORTH);

        // === Table ===
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Username", "Email", "Phone"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // === Buttons Panel ===
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        styleButton(btnAdd, new Color(46, 139, 87));     // Green
        styleButton(btnUpdate, new Color(70, 130, 180)); // Blue
        styleButton(btnDelete, new Color(220, 20, 60));  // Red
        styleButton(btnSearch, new Color(30, 144, 255)); // Light Blue
        styleButton(btnRefresh, new Color(105, 105, 105)); // Gray

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        add(actions, BorderLayout.SOUTH);

        // === Listeners ===
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSearch();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAll();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddDialog();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUpdateDialog();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedCustomer();
            }
        });

        // Load customers initially
        loadAll();
    }

    // === Load All Customers ===
    private void loadAll() {
        model.setRowCount(0);
        try {
            List<Customer> list = dao.findAll();
            if (list != null) {
                for (Customer c : list) {
                    model.addRow(new Object[]{
                            c.getId(),
                            c.getName(),
                            c.getUsername(),
                            c.getEmail(),
                            c.getPhone()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // === Search ===
    private void doSearch() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter name or ID to search.");
            return;
        }

        model.setRowCount(0);
        try {
            List<Customer> list = dao.findByNameOrId(q);
            if (list == null || list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No customers found.");
                return;
            }
            for (Customer c : list) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getName(),
                        c.getUsername(),
                        c.getEmail(),
                        c.getPhone()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // === Add Dialog ===
    private void showAddDialog() {
        JTextField nameField = new JTextField(15);
        JTextField userField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);

        JPanel p = new JPanel(new GridLayout(5, 2, 6, 6));
        p.add(new JLabel("Name:")); p.add(nameField);
        p.add(new JLabel("Username:")); p.add(userField);
        p.add(new JLabel("Email:")); p.add(emailField);
        p.add(new JLabel("Phone:")); p.add(phoneField);
        p.add(new JLabel("Password:")); p.add(passField);

        int result = JOptionPane.showConfirmDialog(this, p, "Add New Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Customer c = new Customer();
            c.setName(nameField.getText().trim());
            c.setUsername(userField.getText().trim());
            c.setEmail(emailField.getText().trim());
            c.setPhone(phoneField.getText().trim());
            c.setPassword(new String(passField.getPassword()));

            try {
                boolean ok = dao.insert(c);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "✅ Customer added successfully!");
                    loadAll();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to add customer.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // === Update Dialog ===
    private void showUpdateDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to update.");
            return;
        }

        int id = (Integer) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        String username = (String) model.getValueAt(row, 2);
        String email = (String) model.getValueAt(row, 3);
        String phone = (String) model.getValueAt(row, 4);

        JTextField nameField = new JTextField(name, 15);
        JTextField userField = new JTextField(username, 15);
        JTextField emailField = new JTextField(email, 15);
        JTextField phoneField = new JTextField(phone, 15);

        JPanel p = new JPanel(new GridLayout(4, 2, 6, 6));
        p.add(new JLabel("Name:")); p.add(nameField);
        p.add(new JLabel("Username:")); p.add(userField);
        p.add(new JLabel("Email:")); p.add(emailField);
        p.add(new JLabel("Phone:")); p.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, p, "Update Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Customer c = new Customer();
                c.setId(id);
                c.setName(nameField.getText().trim());
                c.setUsername(userField.getText().trim());
                c.setEmail(emailField.getText().trim());
                c.setPhone(phoneField.getText().trim());

                boolean ok = dao.update(c);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "✅ Customer updated successfully!");
                    loadAll();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to update customer.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // === Delete Customer ===
    private void deleteSelectedCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to delete.");
            return;
        }

        int id = (Integer) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete customer: " + name + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = dao.delete(id);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "🗑️ Customer deleted successfully!");
                    loadAll();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to delete customer.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // === Style Helper ===
    private void styleButton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
    }
}
