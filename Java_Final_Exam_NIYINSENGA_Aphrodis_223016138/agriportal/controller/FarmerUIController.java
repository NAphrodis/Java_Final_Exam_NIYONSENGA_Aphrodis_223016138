package com.agriportal.controller;

import com.agriportal.model.Field;
import com.agriportal.model.Farmer;
import com.agriportal.model.Forecast;
import com.agriportal.model.dao.FieldDAO;
import com.agriportal.model.dao.FarmerDAO;
import com.agriportal.model.dao.ForecastDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class FarmerUIController extends JPanel {

    private final Farmer farmer;
    private final FieldDAO fieldDAO = new FieldDAO();
    private final FarmerDAO farmerDAO = new FarmerDAO();
    private final ForecastDAO forecastDAO = new ForecastDAO();

    private JTable fieldsTable;
    private DefaultTableModel fieldsModel;
    private JTable weatherTable;
    private DefaultTableModel weatherModel;
    private JTextArea recommendationsArea;
    private final JTabbedPane tabs = new JTabbedPane();

    public FarmerUIController(final Farmer farmer) {
        this.farmer = farmer;
        setLayout(new BorderLayout(10, 10));
        add(buildHeaderPanel(), BorderLayout.NORTH);
        tabs.addTab("My Fields", buildFieldsPanel());
        tabs.addTab("Profile", buildProfileTab());
        add(tabs, BorderLayout.CENTER);
        loadFields();
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 128, 96));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("🌾 AgriPortal Farmer Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton btnExit = new JButton("Exit");
        btnExit.setBackground(new Color(200, 0, 0));
        btnExit.setForeground(Color.WHITE);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { System.exit(0); }
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnExit, BorderLayout.EAST);
        return header;
    }

    private JPanel buildFieldsPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JButton btnAdd = new JButton("Add Field");
        final JButton btnUpdate = new JButton("Update Field");
        final JButton btnDelete = new JButton("Delete Field");
        final JButton btnRefresh = new JButton("Refresh");
        buttons.add(btnAdd); buttons.add(btnUpdate); buttons.add(btnDelete); buttons.add(btnRefresh);
        main.add(buttons, BorderLayout.NORTH);

        fieldsModel = new DefaultTableModel(new Object[]{"ID", "Name", "Location", "Area (ha)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        fieldsTable = new JTable(fieldsModel);
        JScrollPane fieldScroll = new JScrollPane(fieldsTable);

        weatherModel = new DefaultTableModel(new Object[]{"Date", "Condition", "Temp", "Rain"}, 0);
        weatherTable = new JTable(weatherModel);
        JScrollPane weatherScroll = new JScrollPane(weatherTable);

        recommendationsArea = new JTextArea();
        recommendationsArea.setEditable(false);
        JScrollPane recScroll = new JScrollPane(recommendationsArea);

        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.add(weatherScroll);
        rightPanel.add(recScroll);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fieldScroll, rightPanel);
        split.setResizeWeight(0.6);
        main.add(split, BorderLayout.CENTER);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addFieldDialog(); }
        });
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { updateFieldDialog(); }
        });
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteField(); }
        });
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadFields(); }
        });

        return main;
    }

    private JPanel buildProfileTab() {
        final JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        final JTextField txtName = new JTextField(farmer.getName());
        final JTextField txtPhone = new JTextField(farmer.getPhone());
        final JPasswordField txtPassword = new JPasswordField(farmer.getPassword());
        JButton btnSave = new JButton("Save");

        panel.add(new JLabel("Name:")); panel.add(txtName);
        panel.add(new JLabel("Phone:")); panel.add(txtPhone);
        panel.add(new JLabel("Password:")); panel.add(txtPassword);
        panel.add(new JLabel("")); panel.add(btnSave);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    farmer.setName(txtName.getText());
                    farmer.setPhone(txtPhone.getText());
                    farmer.setPassword(new String(txtPassword.getPassword()));
                    farmerDAO.update(farmer);
                    JOptionPane.showMessageDialog(panel, "Profile updated.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel, ex.getMessage());
                }
            }
        });
        return panel;
    }

    private void loadFields() {
        fieldsModel.setRowCount(0);
        try {
            List<Field> list = fieldDAO.findByOwnerId(farmer.getId());
            for (int i = 0; i < list.size(); i++) {
                Field f = list.get(i);
                fieldsModel.addRow(new Object[]{f.getId(), f.getName(), f.getLocation(), f.getAreaHectares()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading fields: " + ex.getMessage());
        }
    }

    private void addFieldDialog() {
        JTextField name = new JTextField();
        JTextField location = new JTextField();
        JTextField area = new JTextField();
        Object[] msg = {"Name:", name, "Location:", location, "Area (ha):", area};
        int res = JOptionPane.showConfirmDialog(this, msg, "Add Field", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            Field f = new Field();
            f.setName(name.getText());
            f.setLocation(location.getText());
            f.setAreaHectares(Double.parseDouble(area.getText()));
            f.setOwner(farmer);
            fieldDAO.insert(f);
            loadFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateFieldDialog() {
        int row = fieldsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a field."); return; }
        int id = (Integer) fieldsModel.getValueAt(row, 0);
        try {
            Field f = fieldDAO.findById(id);
            JTextField name = new JTextField(f.getName());
            JTextField loc = new JTextField(f.getLocation());
            JTextField area = new JTextField(String.valueOf(f.getAreaHectares()));
            Object[] msg = {"Name:", name, "Location:", loc, "Area (ha):", area};
            int res = JOptionPane.showConfirmDialog(this, msg, "Update Field", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;
            f.setName(name.getText());
            f.setLocation(loc.getText());
            f.setAreaHectares(Double.parseDouble(area.getText()));
            f.setOwner(farmer);
            fieldDAO.update(f);
            loadFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }

    private void deleteField() {
        int row = fieldsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a field."); return; }
        int id = (Integer) fieldsModel.getValueAt(row, 0);
        try {
            fieldDAO.delete(id);
            loadFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }
}
