package com.agriportal.controller;

import com.agriportal.model.Field;
import com.agriportal.model.Farmer;
import com.agriportal.model.IrrigationSchedule;
import com.agriportal.model.dao.FieldDAO;
import com.agriportal.model.dao.IrrigationScheduleDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class IrrigationController extends JPanel {

    private final IrrigationScheduleDAO dao = new IrrigationScheduleDAO();
    private final FieldDAO fieldDAO = new FieldDAO();
    private final Farmer farmer; // null = admin

    private final JTable table;
    private final DefaultTableModel model;

    public IrrigationController(Farmer farmer) {
        this.farmer = farmer;
        setLayout(new BorderLayout(8,8));

        JLabel title = new JLabel(farmer == null ? "All Irrigation Schedules" : "My Irrigation Schedules");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID","Field","Date","Duration (h)","Water","Notes"}, 0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JButton btnRefresh = new JButton("Refresh");
        final JButton btnAdd = new JButton("Add");
        final JButton btnEdit = new JButton("Edit");
        final JButton btnDel = new JButton("Delete");

        // ===  Button Colors with BLACK TEXT ===
        styleRefreshButton(btnRefresh);
        styleAddButton(btnAdd);
        styleEditButton(btnEdit);
        styleDeleteButton(btnDel);

        bottom.add(btnRefresh);
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDel);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { load(); } });
        btnAdd.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { addDialog(); } });
        btnEdit.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { editSelected(); } });
        btnDel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { deleteSelected(); } });

        load();
    }

    public void load() {
        model.setRowCount(0);
        try {
            List<IrrigationSchedule> list;
            if (farmer != null) list = dao.findByFarmerId(farmer.getId());
            else list = dao.findAll();
            for (IrrigationSchedule s : list) {
                String fieldName = s.getField() != null ? s.getField().getName() + " (id:" + s.getField().getId() + ")" : "N/A";
                model.addRow(new Object[]{s.getId(), fieldName,
                        s.getScheduleDate() != null ? s.getScheduleDate().toString() : "",
                        s.getDurationHours(), s.getWaterVolume(), s.getNotes()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load schedules: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void addDialog() {
        try {
            List<Field> fields = (farmer != null) ? fieldDAO.findByOwnerId(farmer.getId()) : fieldDAO.findAll();
            if (fields.isEmpty()) { JOptionPane.showMessageDialog(this, "No fields found."); return; }
            String[] items = new String[fields.size()];
            for (int i = 0; i < fields.size(); i++) items[i] = fields.get(i).getId() + " - " + fields.get(i).getName();

            JComboBox combo = new JComboBox(items);
            JTextField date = new JTextField(LocalDate.now().toString());
            JTextField dur = new JTextField();
            JTextField vol = new JTextField();
            JTextField notes = new JTextField();

            Object[] msg = {"Field:", combo, "Date (yyyy-MM-dd):", date, "Duration (hours):", dur, "Water volume:", vol, "Notes:", notes};
            int res = JOptionPane.showConfirmDialog(this, msg, "Add Schedule", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            int idx = combo.getSelectedIndex();
            Field selected = fields.get(idx);
            IrrigationSchedule s = new IrrigationSchedule();
            s.setField(selected);
            s.setScheduleDate(LocalDate.parse(date.getText().trim()));
            try { s.setDurationHours(Double.parseDouble(dur.getText().trim())); } catch (Exception ex) {}
            try { s.setWaterVolume(Double.parseDouble(vol.getText().trim())); } catch (Exception ex) {}
            s.setNotes(notes.getText().trim());
            dao.insert(s);
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int getSelectedId() {
        int r = table.getSelectedRow();
        if (r < 0) return -1;
        Object obj = model.getValueAt(r, 0);
        return obj instanceof Integer ? (Integer) obj : Integer.parseInt(obj.toString());
    }

    private void editSelected() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select one schedule."); return; }
        try {
            IrrigationSchedule s = dao.findById(id);
            if (s == null) { JOptionPane.showMessageDialog(this, "Not found."); return; }

            List<Field> fields = (farmer != null) ? fieldDAO.findByOwnerId(farmer.getId()) : fieldDAO.findAll();
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
            Object[] msg = {"Field:", combo, "Date (yyyy-MM-dd):", date, "Duration (hours):", dur, "Water volume:", vol, "Notes:", notes};
            int res = JOptionPane.showConfirmDialog(this, msg, "Edit Schedule", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            int idx = combo.getSelectedIndex();
            s.setField(fields.get(idx));
            s.setScheduleDate(LocalDate.parse(date.getText().trim()));
            try { s.setDurationHours(Double.parseDouble(dur.getText().trim())); } catch (Exception ex) { s.setDurationHours(null); }
            try { s.setWaterVolume(Double.parseDouble(vol.getText().trim())); } catch (Exception ex) { s.setWaterVolume(null); }
            s.setNotes(notes.getText().trim());
            dao.update(s);
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to edit: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteSelected() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select one schedule."); return; }
        int conf = JOptionPane.showConfirmDialog(this, "Delete schedule id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(id);
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // BUTTON STYLING METHODS - ALL WITH BLACK TEXT
    private void styleRefreshButton(final JButton btn) {
        btn.setBackground(new Color(30, 144, 255)); // BLUE
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(25, 120, 220));
                btn.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 144, 255));
                btn.setForeground(Color.BLACK); // Keep black on exit
            }
        });
    }

    private void styleAddButton(final JButton btn) {
        btn.setBackground(new Color(46, 139, 87)); // GREEN
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(35, 120, 70));
                btn.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(46, 139, 87));
                btn.setForeground(Color.BLACK); // Keep black on exit
            }
        });
    }

    private void styleEditButton(final JButton btn) {
        btn.setBackground(new Color(255, 165, 0)); // ORANGE
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 145, 0));
                btn.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 165, 0));
                btn.setForeground(Color.BLACK); // Keep black on exit
            }
        });
    }

    private void styleDeleteButton(final JButton btn) {
        btn.setBackground(new Color(220, 20, 60)); // RED
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(190, 10, 45));
                btn.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(220, 20, 60));
                btn.setForeground(Color.BLACK); // Keep black on exit
            }
        });
    }
}