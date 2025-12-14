package com.agriportal.controller;

import com.agriportal.model.Crop;
import com.agriportal.model.Field;
import com.agriportal.model.Farmer;
import com.agriportal.model.dao.CropDAO;
import com.agriportal.model.dao.FieldDAO;
import com.agriportal.view.AddCropView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;


public class CropUIController extends JPanel {

    private final CropDAO cropDAO = new CropDAO();
    private final FieldDAO fieldDAO = new FieldDAO();
    private final Farmer farmer; // if null -> admin view of all crops

    private final JTable table;
    private final DefaultTableModel model;

    public CropUIController(Farmer farmer) {
        this.farmer = farmer;
        setLayout(new BorderLayout(8,8));

        model = new DefaultTableModel(new Object[]{"ID", "Crop", "Variety", "Planted", "Expected Harvest", "Status", "Field"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Add Crop");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete = new JButton("Delete");
        bottom.add(btnRefresh);
        bottom.add(btnAdd);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { load(); }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { openAddCropWindow(); }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteSelectedCrop(); }
        });

        load();
    }

    public void load() {
        model.setRowCount(0);
        try {
            List<Crop> list;
            if (farmer != null) {
                list = cropDAO.findByFarmerId(farmer.getId());
            } else {
                list = cropDAO.findAll();
            }

            if (list != null) {
                for (Crop c : list) {
                    String fieldName = (c.getField() != null) ? (c.getField().getName() + " (id:" + c.getField().getId() + ")") : "N/A";
                    model.addRow(new Object[]{
                            c.getId(),
                            c.getCropName(),
                            c.getVariety(),
                            c.getPlantedDate() != null ? c.getPlantedDate().toString() : "",
                            c.getExpectedHarvestDate() != null ? c.getExpectedHarvestDate().toString() : "",
                            c.getStatus(),
                            fieldName
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load crops: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openAddCropWindow() {
        try {
            List<Field> fields;
            if (farmer != null) {
                fields = fieldDAO.findByOwnerId(farmer.getId());
            } else {
                fields = fieldDAO.findAll();
            }

            if (fields == null || fields.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No fields available. Add a field first.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            AddCropView view = new AddCropView();
            java.util.List<String> items = new java.util.ArrayList<String>();
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                items.add(f.getId() + " - " + f.getName());
            }
            view.setFieldItems(items);

            if (farmer != null && fields.size() == 1) {
                view.setSelectedFieldIndex(0);
                view.disableFieldSelection();
            }

            int res = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this), view, "Add Crop", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;

            String cropName = view.getCropName();
            if (cropName == null || cropName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Crop name required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate planted = null;
            LocalDate harvest = null;
            try {
                if (view.getPlanted() != null && view.getPlanted().trim().length() > 0)
                    planted = LocalDate.parse(view.getPlanted().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid planted date. Use yyyy-MM-dd", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (view.getHarvest() != null && view.getHarvest().trim().length() > 0)
                    harvest = LocalDate.parse(view.getHarvest().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid expected harvest date. Use yyyy-MM-dd", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Double expectedYield = null;
            String ey = view.getExpectedYield();
            if (ey != null && ey.trim().length() > 0) {
                try { expectedYield = Double.parseDouble(ey.trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Expected yield numeric", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            }

            int selFieldIndex = view.getSelectedFieldIndex();
            if (selFieldIndex < 0 || selFieldIndex >= fields.size()) {
                JOptionPane.showMessageDialog(this, "Select a field", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Field selectedField = fields.get(selFieldIndex);

            Crop c = new Crop();
            c.setCropName(cropName.trim());
            c.setVariety(view.getVariety());
            c.setPlantedDate(planted);
            c.setExpectedHarvestDate(harvest);
            c.setExpectedYield(expectedYield);
            c.setStatus("Planted");
            c.setField(selectedField);

            boolean ok = cropDAO.insert(c);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Crop added.");
                load();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add crop.", "DB Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add crop: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSelectedCrop() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a crop first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (Integer) model.getValueAt(r, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete crop id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = cropDAO.delete(id);
            if (ok) {
                load();
                JOptionPane.showMessageDialog(this, "Crop deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Crop not deleted.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete crop: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
