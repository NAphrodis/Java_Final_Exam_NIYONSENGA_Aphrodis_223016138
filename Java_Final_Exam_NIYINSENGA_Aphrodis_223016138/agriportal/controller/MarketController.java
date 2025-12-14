package com.agriportal.controller;

import com.agriportal.model.Harvest;
import com.agriportal.model.Market;
import com.agriportal.model.Farmer;
import com.agriportal.model.dao.HarvestDAO;
import com.agriportal.model.dao.MarketDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MarketController extends JPanel {

    private final MarketDAO dao = new MarketDAO();
    private final HarvestDAO harvestDAO = new HarvestDAO();
    private final Farmer farmer;

    private final JTable table;
    private final DefaultTableModel model;

    public MarketController(Farmer farmer) {
        this.farmer = farmer;
        setLayout(new BorderLayout(8,8));
        JLabel title = new JLabel(farmer == null ? "Market Listings" : "My Market Listings");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID","Harvest","Market","Price","Status"}, 0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final JButton btnRefresh = new JButton("Refresh");
        final JButton btnAdd = new JButton("Add");
        final JButton btnEdit = new JButton("Edit");
        final JButton btnDel = new JButton("Delete");

        // === 🎨 Button Colors ===
        btnRefresh.setBackground(new Color(30, 144, 255)); // blue
        btnRefresh.setForeground(Color.WHITE);

        btnAdd.setBackground(new Color(46, 139, 87)); // green
        btnAdd.setForeground(Color.WHITE);

        btnEdit.setBackground(new Color(255, 165, 0)); // orange
        btnEdit.setForeground(Color.WHITE);

        btnDel.setBackground(new Color(220, 20, 60)); // red
        btnDel.setForeground(Color.WHITE);

        // === Consistent Styling ===
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

        // === Action Listeners ===
        btnRefresh.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { load(); } });
        btnAdd.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { addDialog(); } });
        btnEdit.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { editSelected(); } });
        btnDel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { deleteSelected(); } });

        load();
    }

    public void load() {
        model.setRowCount(0);
        try {
            List<Market> list;
            if (farmer != null) list = dao.findByFarmerId(farmer.getId());
            else list = dao.findAll();
            for (Market m : list) {
                String harvestLabel = m.getHarvest()!=null ? ("hid:" + m.getHarvest().getId()) : "N/A";
                model.addRow(new Object[]{m.getId(), harvestLabel, m.getMarketName(), m.getPrice(), m.getStatus()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load market listings: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void addDialog() {
        try {
            List<Harvest> harvests;
            if (farmer != null) harvests = harvestDAO.findByFarmerId(farmer.getId());
            else harvests = harvestDAO.findAll();
            if (harvests.isEmpty()) { JOptionPane.showMessageDialog(this, "No harvests found."); return; }
            String[] items = new String[harvests.size()];
            for (int i = 0; i < harvests.size(); i++)
                items[i] = harvests.get(i).getId() + " - cropId:" +
                        (harvests.get(i).getCrop()!=null ? harvests.get(i).getCrop().getId() : "N/A");

            JComboBox combo = new JComboBox(items);
            JTextField marketName = new JTextField();
            JTextField price = new JTextField();
            JTextField status = new JTextField("Listed");

            Object[] msg = {"Harvest:", combo, "Market name:", marketName, "Price:", price, "Status:", status};
            int res = JOptionPane.showConfirmDialog(this, msg, "Add Market Listing", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            Market m = new Market();
            m.setHarvest(harvests.get(combo.getSelectedIndex()));
            m.setMarketName(marketName.getText().trim());
            try { m.setPrice(Double.parseDouble(price.getText().trim())); } catch (Exception ex) { m.setPrice(null); }
            m.setStatus(status.getText().trim());
            dao.insert(m);
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add market listing: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int getSelectedId() {
        int r = table.getSelectedRow();
        if (r < 0) return -1;
        Object o = model.getValueAt(r,0);
        return o instanceof Integer ? (Integer)o : Integer.parseInt(o.toString());
    }

    private void editSelected() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select one listing."); return; }
        try {
            Market m = dao.findById(id);
            if (m == null) { JOptionPane.showMessageDialog(this, "Not found."); return; }

            List<Harvest> harvests;
            if (farmer != null) harvests = harvestDAO.findByFarmerId(farmer.getId());
            else harvests = harvestDAO.findAll();
            String[] items = new String[harvests.size()];
            int sel = 0;
            for (int i = 0; i < harvests.size(); i++) {
                items[i] = harvests.get(i).getId() + " - cropId:" +
                        (harvests.get(i).getCrop()!=null ? harvests.get(i).getCrop().getId() : "N/A");
                if (m.getHarvest()!=null && harvests.get(i).getId() == m.getHarvest().getId()) sel = i;
            }
            JComboBox combo = new JComboBox(items);
            combo.setSelectedIndex(sel);
            JTextField marketName = new JTextField(m.getMarketName()!=null ? m.getMarketName() : "");
            JTextField price = new JTextField(m.getPrice()!=null ? m.getPrice().toString() : "");
            JTextField status = new JTextField(m.getStatus()!=null ? m.getStatus() : "");

            Object[] msg = {"Harvest:", combo, "Market name:", marketName, "Price:", price, "Status:", status};
            int res = JOptionPane.showConfirmDialog(this, msg, "Edit Market Listing", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            m.setHarvest(harvests.get(combo.getSelectedIndex()));
            m.setMarketName(marketName.getText().trim());
            try { m.setPrice(Double.parseDouble(price.getText().trim())); } catch (Exception ex) { m.setPrice(null); }
            m.setStatus(status.getText().trim());
            dao.update(m);
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to edit: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteSelected() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select one listing."); return; }
        int conf = JOptionPane.showConfirmDialog(this, "Delete listing id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(id);
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
