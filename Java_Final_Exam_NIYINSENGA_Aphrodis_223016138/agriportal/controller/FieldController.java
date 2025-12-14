package com.agriportal.controller;

import com.agriportal.model.Farmer;
import com.agriportal.model.Field;
import com.agriportal.model.dao.FieldDAO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class FieldController extends JPanel {

    private Farmer currentFarmer; // null = Admin mode
    private FieldDAO fieldDAO = new FieldDAO();

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch = new JTextField(20);
    private JButton btnAdd = new JButton("Add Field");
    private JButton btnEdit = new JButton("Edit");
    private JButton btnDelete = new JButton("Delete");
    private JButton btnRefresh = new JButton("Refresh");

    private List<Field> allFields = new ArrayList<Field>();

    public FieldController(Farmer farmer) {
        this.currentFarmer = farmer;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 255, 250));

        JLabel title = new JLabel(farmer == null ? "🌾 Manage All Fields" : "🌱 My Fields");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(0, 100, 60));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        top.add(searchPanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Address", "Capacity (ha)", "Manager", "Contact", "Created At"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // === Buttons Panel ===
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 🎨 Button Colors (Consistent Visual Theme)
        styleButton(btnRefresh, new Color(30, 144, 255));  // blue
        styleButton(btnAdd, new Color(46, 139, 87));       // green
        styleButton(btnEdit, new Color(255, 165, 0));      // orange
        styleButton(btnDelete, new Color(220, 20, 60));    // red

        bottom.add(btnRefresh);
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        // === Button Actions ===
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showAddDialog(); }
        });
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showEditDialog(); }
        });
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteSelected(); }
        });
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadFields(); }
        });

        // === Live Search Listener ===
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        loadFields();
    }

    // === Styling Helper ===
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void loadFields() {
        model.setRowCount(0);
        try {
            if (currentFarmer == null)
                allFields = fieldDAO.findAll();
            else
                allFields = fieldDAO.findByOwnerId(currentFarmer.getId());

            for (int i = 0; i < allFields.size(); i++) {
                Field f = allFields.get(i);
                addRow(f);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void filter() {
        String q = txtSearch.getText().trim().toLowerCase();
        model.setRowCount(0);
        if (q.isEmpty()) {
            for (int i = 0; i < allFields.size(); i++) {
                addRow(allFields.get(i));
            }
        } else {
            for (int i = 0; i < allFields.size(); i++) {
                Field f = allFields.get(i);
                if ((f.getName() != null && f.getName().toLowerCase().contains(q)) ||
                    (f.getManager() != null && f.getManager().toLowerCase().contains(q))) {
                    addRow(f);
                }
            }
        }
    }

    private void addRow(Field f) {
        model.addRow(new Object[]{
                f.getId(), f.getName(), f.getAddress(),
                f.getCapacity(), f.getManager(),
                f.getContact(), f.getCreatedAt()
        });
    }

    private void showAddDialog() {
        JTextField name = new JTextField();
        JTextField address = new JTextField();
        JTextField capacity = new JTextField();
        JTextField manager = new JTextField();
        JTextField contact = new JTextField();

        Object[] msg = {
                "Name:", name,
                "Address:", address,
                "Capacity (ha):", capacity,
                "Manager:", manager,
                "Contact:", contact
        };

        int res = JOptionPane.showConfirmDialog(this, msg, "Add Field", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            Field f = new Field();
            f.setName(name.getText());
            f.setAddress(address.getText());
            f.setCapacity(Double.parseDouble(capacity.getText()));
            f.setManager(manager.getText());
            f.setContact(contact.getText());
            if (currentFarmer != null) f.setOwnerId(currentFarmer.getId());
            fieldDAO.insert(f);
            loadFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try {
            Field f = fieldDAO.findById(id);
            JTextField name = new JTextField(f.getName());
            JTextField address = new JTextField(f.getAddress());
            JTextField capacity = new JTextField(String.valueOf(f.getCapacity()));
            JTextField manager = new JTextField(f.getManager());
            JTextField contact = new JTextField(f.getContact());
            Object[] msg = {
                    "Name:", name,
                    "Address:", address,
                    "Capacity (ha):", capacity,
                    "Manager:", manager,
                    "Contact:", contact
            };
            int res = JOptionPane.showConfirmDialog(this, msg, "Edit Field", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;
            f.setName(name.getText());
            f.setAddress(address.getText());
            f.setCapacity(Double.parseDouble(capacity.getText()));
            f.setManager(manager.getText());
            f.setContact(contact.getText());
            fieldDAO.update(f);
            loadFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a field to delete.");
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete field ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            fieldDAO.delete(id);
            loadFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Test main
    public static void main(String[] args) {
        JFrame f = new JFrame("FieldController Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new FieldController(null));
        f.setSize(900, 500);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
