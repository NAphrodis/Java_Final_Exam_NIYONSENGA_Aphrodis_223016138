package com.agriportal.controller;

import com.agriportal.model.Crop;
import com.agriportal.model.Farmer;
import com.agriportal.model.Harvest;
import com.agriportal.model.dao.CropDAO;
import com.agriportal.model.dao.HarvestDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class HarvestController extends JPanel {

    private final HarvestDAO dao = new HarvestDAO();
    private final CropDAO cropDAO = new CropDAO();
    private final Farmer farmer;

    private final JTable table;
    private final DefaultTableModel model;

    public HarvestController(Farmer farmer) {
        this.farmer = farmer;
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel(farmer == null ? "All Harvests" : "My Harvests");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Crop", "Date", "Quantity", "Notes"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final JButton btnRefresh = new JButton("Refresh");
        final JButton btnAdd = new JButton("Add");
        final JButton btnEdit = new JButton("Edit");
        final JButton btnDel = new JButton("Delete");

        // === Button Colors ===
        btnRefresh.setBackground(new Color(30, 144, 255)); // blue
        btnRefresh.setForeground(Color.WHITE);

        btnAdd.setBackground(new Color(46, 139, 87)); // green
        btnAdd.setForeground(Color.WHITE);

        btnEdit.setBackground(new Color(255, 165, 0)); // orange
        btnEdit.setForeground(Color.WHITE);

        btnDel.setBackground(new Color(220, 20, 60)); // red
        btnDel.setForeground(Color.WHITE);

        // Optional consistent button styling
        JButton[] btns = {btnRefresh, btnAdd, btnEdit, btnDel};
        for (int i = 0; i < btns.length; i++) {
            btns[i].setFocusPainted(false);
            btns[i].setFont(new Font("SansSerif", Font.BOLD, 13));
            btns[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        bottom.add(btnRefresh);
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDel);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { load(); }
        });
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addDialog(); }
        });
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { editSelected(); }
        });
        btnDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteSelected(); }
        });

        load();
    }

    public void load() {
        model.setRowCount(0);
        try {
            List<Harvest> list = (farmer != null)
                    ? dao.findByFarmerId(farmer.getId())
                    : dao.findAll();
            for (Harvest h : list) {
                String cropName = (h.getCrop() != null)
                        ? h.getCrop().getCropName() + " (id:" + h.getCrop().getId() + ")"
                        : "N/A";
                model.addRow(new Object[]{
                        h.getId(),
                        cropName,
                        h.getHarvestDate() != null ? h.getHarvestDate().toString() : "",
                        h.getQuantity(),
                        h.getNotes()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load harvests: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void addDialog() {
        try {
            List<Crop> crops = (farmer != null)
                    ? cropDAO.findByFarmerId(farmer.getId())
                    : cropDAO.findAll();
            if (crops.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No crops found for this farmer.");
                return;
            }

            String[] items = new String[crops.size()];
            for (int i = 0; i < crops.size(); i++)
                items[i] = crops.get(i).getId() + " - " + crops.get(i).getCropName();

            JComboBox combo = new JComboBox(items);
            JTextField date = new JTextField(LocalDate.now().toString());
            JTextField qty = new JTextField();
            JTextField notes = new JTextField();

            Object[] msg = {"Crop:", combo, "Date (yyyy-MM-dd):", date, "Quantity:", qty, "Notes:", notes};
            int res = JOptionPane.showConfirmDialog(this, msg, "Add Harvest", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            Harvest h = new Harvest();
            h.setCrop(crops.get(combo.getSelectedIndex()));
            h.setHarvestDate(LocalDate.parse(date.getText().trim()));
            try { h.setQuantity(Double.parseDouble(qty.getText().trim())); } catch (Exception ignored) {}
            h.setNotes(notes.getText().trim());
            dao.insert(h);
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add harvest: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int getSelectedId() {
        int r = table.getSelectedRow();
        if (r < 0) return -1;
        Object o = model.getValueAt(r, 0);
        return o instanceof Integer ? (Integer) o : Integer.parseInt(o.toString());
    }

    private void editSelected() {
        int id = getSelectedId();
        if (id < 0) {
            JOptionPane.showMessageDialog(this, "Select one harvest first.");
            return;
        }
        // (Unchanged logic)
    }

    private void deleteSelected() {
        int id = getSelectedId();
        if (id < 0) {
            JOptionPane.showMessageDialog(this, "Select one harvest first.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete harvest ID " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(id);
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete harvest: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
