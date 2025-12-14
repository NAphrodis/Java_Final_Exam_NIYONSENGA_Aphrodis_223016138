package com.agriportal.controller;

import com.agriportal.model.Order;
import com.agriportal.model.dao.OrderDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.List;


public class ReportController extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnExport = new JButton("Export CSV");
    private final OrderDAO orderDAO = new OrderDAO();

    public ReportController() {
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(250, 250, 250));

        model = new DefaultTableModel(new Object[]{"ID", "Product", "Buyer", "Qty", "Date", "Status", "Total"}, 0);
        table = new JTable(model);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(btnRefresh);
        top.add(btnExport);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });
        btnExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportCsv();
            }
        });

        load();
    }

    private void load() {
        model.setRowCount(0);
        try {
            List<Order> orders = orderDAO.findAll();
            if (orders != null) {
                for (Order o : orders) {
                    model.addRow(new Object[]{
                            o.getId(),
                            o.getProduct() != null ? o.getProduct().getName() : "N/A",
                            o.getBuyerName(),
                            o.getQuantityOrdered(),
                            o.getOrderDate() != null ? o.getOrderDate().toString() : "",
                            o.getStatus(),
                            o.totalCost()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load orders: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        java.io.File f = chooser.getSelectedFile();
        try (FileWriter fw = new FileWriter(f)) {
            // header
            fw.write("id,product,buyer,quantity,date,status,total\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                Object id = model.getValueAt(i, 0);
                Object product = model.getValueAt(i, 1);
                Object buyer = model.getValueAt(i, 2);
                Object qty = model.getValueAt(i, 3);
                Object date = model.getValueAt(i, 4);
                Object status = model.getValueAt(i, 5);
                Object total = model.getValueAt(i, 6);
                fw.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                        id, escapeCsv(product), escapeCsv(buyer), qty, date, status, total));
            }
            JOptionPane.showMessageDialog(this, "Exported to " + f.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String escapeCsv(Object o) {
        if (o == null) return "";
        String s = o.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"", "\"\"");
            s = "\"" + s + "\"";
        }
        return s;
    }
}
