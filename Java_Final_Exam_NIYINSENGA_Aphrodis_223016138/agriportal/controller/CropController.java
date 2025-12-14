package com.agriportal.controller;

import com.agriportal.model.Crop;
import com.agriportal.model.Field;
import com.agriportal.model.Farmer;
import com.agriportal.model.dao.CropDAO;
import com.agriportal.model.dao.FieldDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;


public class CropController extends JPanel {

    private final CropDAO cropDAO;
    private final FieldDAO fieldDAO;
    private final Farmer farmer;

    private final JTable table;
    private final DefaultTableModel model;

    public CropController() {
        this(null);
    }

    public CropController(Farmer farmer) {
        this.farmer = farmer;
        cropDAO = new CropDAO();
        fieldDAO = new FieldDAO();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 255, 250));

        JLabel title = new JLabel(
                farmer == null ? "🌾 Manage All Crops" : "🌱 My Crops"
        );
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(0, 100, 60));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // === Table ===
        model = new DefaultTableModel(
                new Object[]{"ID", "Crop", "Variety", "Planted", "Expected Harvest", "Status", "Field ID"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // === Buttons ===
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add Crop");
        JButton editBtn = new JButton("Edit Crop");
        JButton delBtn = new JButton("Delete Crop");
        JButton refreshBtn = new JButton("Refresh");

        styleButton(addBtn, new Color(34, 139, 34));
        styleButton(editBtn, new Color(70, 130, 180));
        styleButton(delBtn, new Color(180, 40, 40));
        styleButton(refreshBtn, new Color(120, 120, 120));

        bottom.add(refreshBtn);
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(delBtn);
        add(bottom, BorderLayout.SOUTH);

        // === Actions ===
        addBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { addCropDialog(); }});
        editBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { editSelectedCrop(); }});
        delBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { deleteSelectedCrop(); }});
        refreshBtn.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { load(); }});

        load();
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
    }

    /** Load crops into table */
    public void load() {
        model.setRowCount(0);
        try {
            List<Crop> list = cropDAO.findAll();
            for (Crop c : list) {
                // Filter if farmer mode
                if (farmer != null) {
                    if (c.getField() == null || c.getField().getOwner() == null ||
                            c.getField().getOwner().getId() != farmer.getId()) {
                        continue;
                    }
                }
                model.addRow(new Object[]{
                        c.getId(),
                        c.getCropName(),
                        c.getVariety(),
                        c.getPlantedDate() != null ? c.getPlantedDate().toString() : "",
                        c.getExpectedHarvestDate() != null ? c.getExpectedHarvestDate().toString() : "",
                        c.getStatus(),
                        c.getField() != null ? c.getField().getId() : null
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load crops: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** Add new crop */
    private void addCropDialog() {
        try {
            List<Field> fields;
            if (farmer != null)
                fields = fieldDAO.findByOwnerId(farmer.getId());
            else
                fields = fieldDAO.findAll();

            if (fields == null || fields.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No fields available. Add a field first.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JTextField cropField = new JTextField();
            JTextField varietyField = new JTextField();
            JTextField plantedField = new JTextField(LocalDate.now().toString());
            JTextField harvestField = new JTextField();
            JTextField expectedYieldField = new JTextField();

            String[] fieldItems = new String[fields.size()];
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                fieldItems[i] = f.getId() + " - " + f.getName();
            }
            JComboBox fieldBox = new JComboBox(fieldItems);

            Object[] msg = {
                    "Crop name:", cropField,
                    "Variety (optional):", varietyField,
                    "Planted date (yyyy-MM-dd):", plantedField,
                    "Expected harvest (yyyy-MM-dd):", harvestField,
                    "Expected yield (numeric):", expectedYieldField,
                    "Select field:", fieldBox
            };

            int res = JOptionPane.showConfirmDialog(this, msg, "Add Crop", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            String cropName = cropField.getText().trim();
            if (cropName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Crop name required.");
                return;
            }

            LocalDate planted = null, harvest = null;
            try { planted = LocalDate.parse(plantedField.getText().trim()); } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid planted date format."); return;
            }
            String htxt = harvestField.getText().trim();
            if (!htxt.isEmpty()) {
                try { harvest = LocalDate.parse(htxt); } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid expected harvest date."); return;
                }
            }

            Double expectedYield = null;
            String ey = expectedYieldField.getText().trim();
            if (!ey.isEmpty()) {
                try { expectedYield = Double.parseDouble(ey); } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Expected yield must be numeric."); return;
                }
            }

            int selIndex = fieldBox.getSelectedIndex();
            Field selectedField = fields.get(selIndex);

            Crop c = new Crop();
            c.setCropName(cropName);
            c.setVariety(varietyField.getText().trim().isEmpty() ? null : varietyField.getText().trim());
            c.setPlantedDate(planted);
            c.setExpectedHarvestDate(harvest);
            c.setExpectedYield(expectedYield);
            c.setStatus("Planted");
            c.setField(selectedField);

            cropDAO.insert(c);
            load();
            JOptionPane.showMessageDialog(this, "Crop added successfully.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add crop: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int getSelectedCropId() {
        int row = table.getSelectedRow();
        if (row < 0) return -1;
        return (int) model.getValueAt(row, 0);
    }

    private void editSelectedCrop() {
        int id = getSelectedCropId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(this, "Select a crop first.");
            return;
        }
        try {
            Crop c = cropDAO.findById(id);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Crop not found.");
                return;
            }
            if (farmer != null && (c.getField() == null || c.getField().getOwner() == null ||
                    c.getField().getOwner().getId() != farmer.getId())) {
                JOptionPane.showMessageDialog(this, "You can only edit crops for your fields.", "Permission", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField cropField = new JTextField(c.getCropName());
            JTextField varietyField = new JTextField(c.getVariety() != null ? c.getVariety() : "");
            JTextField plantedField = new JTextField(c.getPlantedDate() != null ? c.getPlantedDate().toString() : "");
            JTextField harvestField = new JTextField(c.getExpectedHarvestDate() != null ? c.getExpectedHarvestDate().toString() : "");
            JTextField statusField = new JTextField(c.getStatus() != null ? c.getStatus() : "");
            JTextField expectedYieldField = new JTextField(c.getExpectedYield() != null ? c.getExpectedYield().toString() : "");

            Object[] msg = {
                    "Crop name:", cropField,
                    "Variety:", varietyField,
                    "Planted (yyyy-MM-dd):", plantedField,
                    "Expected harvest (yyyy-MM-dd):", harvestField,
                    "Status:", statusField,
                    "Expected yield:", expectedYieldField
            };

            int res = JOptionPane.showConfirmDialog(this, msg, "Edit Crop", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            c.setCropName(cropField.getText().trim());
            c.setVariety(varietyField.getText().trim().isEmpty() ? null : varietyField.getText().trim());
            try { c.setPlantedDate(LocalDate.parse(plantedField.getText().trim())); } catch (Exception ex) {}
            try { String ht = harvestField.getText().trim(); c.setExpectedHarvestDate(ht.isEmpty() ? null : LocalDate.parse(ht)); } catch (Exception ex) {}
            c.setStatus(statusField.getText().trim());
            String ey = expectedYieldField.getText().trim();
            try { c.setExpectedYield(ey.isEmpty() ? null : Double.parseDouble(ey)); } catch (NumberFormatException ex) {}

            cropDAO.update(c);
            load();
            JOptionPane.showMessageDialog(this, "Crop updated successfully.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to edit crop: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteSelectedCrop() {
        int id = getSelectedCropId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(this, "Select a crop first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete crop ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Crop c = cropDAO.findById(id);
            if (farmer != null && (c.getField() == null || c.getField().getOwner() == null ||
                    c.getField().getOwner().getId() != farmer.getId())) {
                JOptionPane.showMessageDialog(this, "You can only delete crops from your fields.", "Permission", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cropDAO.delete(id)) {
                load();
                JOptionPane.showMessageDialog(this, "Crop deleted.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete crop: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // === Test Run ===
    public static void main(String[] args) {
        JFrame f = new JFrame("CropController Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new CropController(null)); // admin mode
        f.setSize(950, 550);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
