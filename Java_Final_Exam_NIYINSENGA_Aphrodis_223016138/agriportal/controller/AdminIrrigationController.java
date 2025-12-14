package com.agriportal.controller;

import com.agriportal.model.Field;
import com.agriportal.model.IrrigationSchedule;
import com.agriportal.model.dao.FieldDAO;
import com.agriportal.model.dao.IrrigationScheduleDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

/**
 * AdminIrrigationController - admin UI to view and manage all irrigation schedules.
 */
public class AdminIrrigationController extends JPanel {

    private final IrrigationScheduleDAO dao = new IrrigationScheduleDAO();
    private final FieldDAO fieldDAO = new FieldDAO();

    private final JTable table;
    private final DefaultTableModel model;

    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");

    public AdminIrrigationController() {
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(245, 255, 250));

        JLabel title = new JLabel("Irrigation Schedules (Admin) - All Farmers");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Field", "Field ID", "Date", "Duration (hrs)", "Volume", "Notes"}, 0) {
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

        // listeners
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
            List<IrrigationSchedule> list = dao.findAll();
            for (IrrigationSchedule s : list) {
                Field f = s.getField();
                String fname = f != null ? f.getName() : "N/A";
                Integer fid = f != null ? f.getId() : null;
                model.addRow(new Object[]{
                        s.getId(),
                        fname,
                        fid,
                        s.getScheduleDate() != null ? s.getScheduleDate().toString() : "",
                        s.getDurationHours(),
                        s.getWaterVolume(),
                        s.getNotes()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load irrigation schedules: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
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
            List<Field> fields = fieldDAO.findAll();
            if (fields.isEmpty()) { JOptionPane.showMessageDialog(this, "No fields available.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            String[] items = new String[fields.size()];
            for (int i = 0; i < fields.size(); i++) items[i] = fields.get(i).getId() + " - " + fields.get(i).getName();

            JComboBox combo = new JComboBox(items);
            JTextField date = new JTextField(LocalDate.now().toString());
            JTextField dur = new JTextField();
            JTextField vol = new JTextField();
            JTextField notes = new JTextField();

            Object[] msg = {"Field:", combo, "Date (yyyy-MM-dd):", date, "Duration (hrs):", dur, "Water Volume:", vol, "Notes:", notes};
            int res = JOptionPane.showConfirmDialog(this, msg, "Add Irrigation Schedule", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            IrrigationSchedule s = new IrrigationSchedule();
            int idx = combo.getSelectedIndex();
            s.setField(fields.get(idx));
            s.setScheduleDate(LocalDate.parse(date.getText().trim()));
            try { s.setDurationHours(Double.parseDouble(dur.getText().trim())); } catch (Exception ex) { s.setDurationHours(null); }
            try { s.setWaterVolume(Double.parseDouble(vol.getText().trim())); } catch (Exception ex) { s.setWaterVolume(null); }
            s.setNotes(notes.getText().trim());

            dao.insert(s);
            load();
            JOptionPane.showMessageDialog(this, "Irrigation schedule added.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add schedule: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void editSelected() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a schedule to edit."); return; }
        try {
            IrrigationSchedule s = dao.findById(id);
            if (s == null) { JOptionPane.showMessageDialog(this, "Not found."); return; }

            List<Field> fields = fieldDAO.findAll();
            if (fields.isEmpty()) { JOptionPane.showMessageDialog(this, "No fields available."); return; }
            String[] items = new String[fields.size()];
            int sel = 0;
            for (int i = 0; i < fields.size(); i++) {
                items[i] = fields.get(i).getId() + " - " + fields.get(i).getName();
                if (s.getField() != null && fields.get(i).getId() == s.getField().getId()) sel = i;
            }
            JComboBox combo = new JComboBox(items);
            combo.setSelectedIndex(sel);

            JTextField date = new JTextField(s.getScheduleDate() != null ? s.getScheduleDate().toString() : "");
            JTextField dur = new JTextField(s.getDurationHours() != null ? s.getDurationHours().toString() : "");
            JTextField vol = new JTextField(s.getWaterVolume() != null ? s.getWaterVolume().toString() : "");
            JTextField notes = new JTextField(s.getNotes() != null ? s.getNotes() : "");

            Object[] msg = {"Field:", combo, "Date (yyyy-MM-dd):", date, "Duration (hrs):", dur, "Water Volume:", vol, "Notes:", notes};
            int res = JOptionPane.showConfirmDialog(this, msg, "Edit Irrigation Schedule", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            int idx = combo.getSelectedIndex();
            s.setField(fields.get(idx));
            s.setScheduleDate(LocalDate.parse(date.getText().trim()));
            try { s.setDurationHours(Double.parseDouble(dur.getText().trim())); } catch (Exception ex) { s.setDurationHours(null); }
            try { s.setWaterVolume(Double.parseDouble(vol.getText().trim())); } catch (Exception ex) { s.setWaterVolume(null); }
            s.setNotes(notes.getText().trim());

            dao.update(s);
            load();
            JOptionPane.showMessageDialog(this, "Schedule updated.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to edit schedule: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSelected() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a schedule to delete."); return; }
        int conf = JOptionPane.showConfirmDialog(this, "Delete schedule id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(id);
            load();
            JOptionPane.showMessageDialog(this, "Schedule deleted.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete schedule: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
