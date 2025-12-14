package com.agriportal.controller;

import com.agriportal.model.Crop;
import com.agriportal.model.Harvest;
import com.agriportal.model.dao.CropDAO;
import com.agriportal.model.dao.HarvestDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

/**
 * AdminHarvestController - admin UI to view and manage all harvest records.
 */
public class AdminHarvestController extends JPanel {

    private final HarvestDAO dao = new HarvestDAO();
    private final CropDAO cropDAO = new CropDAO();

    private final JTable table;
    private final DefaultTableModel model;

    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");

    public AdminHarvestController() {
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(245, 255, 250));

        JLabel title = new JLabel("Harvests (Admin) - All Farmers");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Crop", "Farmer ID", "Date", "Quantity", "Notes"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnRefresh);
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        // listeners (no lambdas)
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDialog();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelected();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelected();
            }
        });

        load();
    }

    public void load() {
        model.setRowCount(0);
        try {
            List<Harvest> list = dao.findAll();
            for (Harvest h : list) {
                String cropName = h.getCrop() != null ? h.getCrop().getCropName() : "N/A";
                Integer farmerId = (h.getCrop() != null && h.getCrop().getField() != null && h.getCrop().getField().getOwner() != null)
                        ? h.getCrop().getField().getOwner().getId() : null;
                model.addRow(new Object[]{
                        h.getId(),
                        cropName,
                        farmerId,
                        h.getHarvestDate() != null ? h.getHarvestDate().toString() : "",
                        h.getQuantity(),
                        h.getNotes()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load harvests: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private int getSelectedId() {
        int r = table.getSelectedRow();
        if (r < 0) return -1;
        Object o = model.getValueAt(r, 0);
        if (o == null) return -1;
        return o instanceof Integer ? (Integer) o : Integer.parseInt(o.toString());
    }

    private void addDialog() {
        try {
            List<Crop> crops = cropDAO.findAll();
            if (crops.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No crops available. Add crops first.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] items = new String[crops.size()];
            for (int i = 0; i < crops.size(); i++) items[i] = crops.get(i).getId() + " - " + crops.get(i).getCropName();

            JComboBox combo = new JComboBox(items);
            JTextField date = new JTextField(LocalDate.now().toString());
            JTextField qty = new JTextField();
            JTextField notes = new JTextField();

            Object[] msg = {"Crop:", combo, "Date (yyyy-MM-dd):", date, "Quantity:", qty, "Notes:", notes};
            int res = JOptionPane.showConfirmDialog(this, msg, "Add Harvest", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            Harvest h = new Harvest();
            int idx = combo.getSelectedIndex();
            h.setCrop(crops.get(idx));
            h.setHarvestDate(LocalDate.parse(date.getText().trim()));
            try { h.setQuantity(Double.parseDouble(qty.getText().trim())); } catch (Exception ex) { h.setQuantity(null); }
            h.setNotes(notes.getText().trim());

            dao.insert(h);
            load();
            JOptionPane.showMessageDialog(this, "Harvest added.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add harvest: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void editSelected() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a harvest to edit."); return; }
        try {
            Harvest h = dao.findById(id);
            if (h == null) { JOptionPane.showMessageDialog(this, "Harvest not found."); return; }

            List<Crop> crops = cropDAO.findAll();
            if (crops.isEmpty()) { JOptionPane.showMessageDialog(this, "No crops available."); return; }
            String[] items = new String[crops.size()];
            int sel = 0;
            for (int i = 0; i < crops.size(); i++) {
                items[i] = crops.get(i).getId() + " - " + crops.get(i).getCropName();
                if (h.getCrop() != null && crops.get(i).getId() == h.getCrop().getId()) sel = i;
            }
            JComboBox combo = new JComboBox(items);
            combo.setSelectedIndex(sel);
            JTextField date = new JTextField(h.getHarvestDate() != null ? h.getHarvestDate().toString() : "");
            JTextField qty = new JTextField(h.getQuantity() != null ? h.getQuantity().toString() : "");
            JTextField notes = new JTextField(h.getNotes() != null ? h.getNotes() : "");

            Object[] msg = {"Crop:", combo, "Date (yyyy-MM-dd):", date, "Quantity:", qty, "Notes:", notes};
            int res = JOptionPane.showConfirmDialog(this, msg, "Edit Harvest", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            int idx = combo.getSelectedIndex();
            h.setCrop(crops.get(idx));
            h.setHarvestDate(LocalDate.parse(date.getText().trim()));
            try { h.setQuantity(Double.parseDouble(qty.getText().trim())); } catch (Exception ex) { h.setQuantity(null); }
            h.setNotes(notes.getText().trim());

            dao.update(h);
            load();
            JOptionPane.showMessageDialog(this, "Harvest updated.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to edit harvest: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSelected() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a harvest to delete."); return; }
        int conf = JOptionPane.showConfirmDialog(this, "Delete harvest id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(id);
            load();
            JOptionPane.showMessageDialog(this, "Harvest deleted.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete harvest: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
