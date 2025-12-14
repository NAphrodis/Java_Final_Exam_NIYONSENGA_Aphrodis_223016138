package com.agriportal.controller;

import com.agriportal.model.Payment;
import com.agriportal.model.Order;
import com.agriportal.model.dao.PaymentDAO;
import com.agriportal.model.dao.OrderDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;


public class PaymentController extends JPanel {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private final JTable table;
    private final DefaultTableModel model;

    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnViewOrder = new JButton("View Order");
    private final JButton btnMarkOrderPaid = new JButton("Mark Order Paid");
    private final JButton btnEditPayment = new JButton("Edit Payment");
    private final JButton btnDeletePayment = new JButton("Delete Payment");
    private final JButton btnExport = new JButton("Export CSV");

    private final JTextField txtSearch = new JTextField(12);
    private final JComboBox statusFilter;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PaymentController() {
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(245, 250, 255));

        JLabel header = new JLabel("💳 Payments Management");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setForeground(new Color(0, 90, 80));

        // Top panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(header);
        top.add(new JLabel("Search Order ID / Status:"));
        top.add(txtSearch);

        statusFilter = new JComboBox(new String[]{"All", "PENDING", "COMPLETED", "FAILED"});
        top.add(statusFilter);

        // Buttons styling
        styleButton(btnRefresh, new Color(0, 102, 204));
        styleButton(btnViewOrder, new Color(0, 153, 76));
        styleButton(btnMarkOrderPaid, new Color(0, 128, 64));
        styleButton(btnEditPayment, new Color(255, 165, 0));
        styleButton(btnDeletePayment, new Color(204, 0, 0));
        styleButton(btnExport, new Color(108, 117, 125));

        top.add(btnRefresh);
        top.add(btnViewOrder);
        top.add(btnMarkOrderPaid);
        top.add(btnEditPayment);
        top.add(btnDeletePayment);
        top.add(btnExport);

        add(top, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{
                "Payment ID", "Order ID", "Amount", "Method", "Status", "Paid At"
        }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(0, 128, 96));
        table.getTableHeader().setForeground(Color.WHITE);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // === Listeners ===
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadPayments(); }
        });

        btnViewOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { viewSelectedOrder(); }
        });

        btnMarkOrderPaid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { markSelectedOrderPaid(); }
        });

        btnEditPayment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { editSelectedPayment(); }
        });

        btnDeletePayment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteSelectedPayment(); }
        });

        btnExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportToCSV(); }
        });

        txtSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadPayments(); }
        });
        statusFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadPayments(); }
        });

        loadPayments();
    }

    /** Apply consistent visual style to buttons */
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Load all payments, with search + status filters applied */
    public void loadPayments() {
        model.setRowCount(0);
        String q = txtSearch.getText().trim();
        String statusSel = (String) statusFilter.getSelectedItem();

        try {
            List<Payment> list = paymentDAO.findAll();
            for (Payment p : list) {
                boolean skip = false;

                if (!q.isEmpty()) {
                    try {
                        int oid = Integer.parseInt(q);
                        if (p.getOrderId() == null || p.getOrderId().intValue() != oid) skip = true;
                    } catch (NumberFormatException nf) {
                        if (p.getStatus() == null || p.getStatus().toLowerCase().indexOf(q.toLowerCase()) < 0)
                            skip = true;
                    }
                }

                if (skip) continue;

                if (statusSel != null && !"All".equals(statusSel)) {
                    String st = p.getStatus() == null ? "" : p.getStatus();
                    if (!statusSel.equalsIgnoreCase(st)) continue;
                }

                String paidAt = p.getPaidAt() == null ? "" : sdf.format(p.getPaidAt());
                model.addRow(new Object[]{
                        p.getId(),
                        p.getOrderId() == null ? "" : p.getOrderId(),
                        p.getAmount(),
                        p.getMethod(),
                        p.getStatus(),
                        paidAt
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load payments: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /** View order linked to selected payment */
    private void viewSelectedOrder() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a payment first.");
            return;
        }
        Object oidObj = model.getValueAt(r, 1);
        if (oidObj == null || oidObj.toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selected payment has no linked order.");
            return;
        }

        int oid = Integer.parseInt(oidObj.toString());
        try {
            Order o = orderDAO.findById(oid);
            if (o == null) {
                JOptionPane.showMessageDialog(this, "Order not found (ID=" + oid + ").");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Order ID: ").append(o.getId()).append("\n");
            sb.append("Product: ").append(o.getProduct() != null ? o.getProduct().getName() : "N/A").append("\n");
            sb.append("Buyer: ").append(o.getBuyerName()).append("\n");
            sb.append("Quantity: ").append(o.getQuantityOrdered()).append("\n");
            sb.append("Date: ").append(o.getOrderDate()).append("\n");
            sb.append("Status: ").append(o.getStatus()).append("\n");
            sb.append("Total: ").append(o.totalCost()).append("\n");

            JOptionPane.showMessageDialog(this, sb.toString(),
                    "Order Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /** Mark linked order as Paid */
    private void markSelectedOrderPaid() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a payment first.");
            return;
        }

        Object oidObj = model.getValueAt(r, 1);
        if (oidObj == null || oidObj.toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "This payment has no linked order.");
            return;
        }

        final int oid = Integer.parseInt(oidObj.toString());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark Order ID " + oid + " as Paid?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Order o = orderDAO.findById(oid);
            if (o == null) {
                JOptionPane.showMessageDialog(this, "Order not found.");
                return;
            }

            o.setStatus("Paid");
            boolean ok = orderDAO.update(o);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Order marked as Paid successfully.");
                loadPayments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order status.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating order: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /** Edit selected payment */
    private void editSelectedPayment() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a payment first.");
            return;
        }

        int id = (Integer) model.getValueAt(r, 0);
        try {
            Payment p = paymentDAO.findById(id);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Payment not found (ID=" + id + ").");
                return;
            }

            JTextField amountField = new JTextField(String.valueOf(p.getAmount()));
            JComboBox methodBox = new JComboBox(new String[]{"CARD", "MOMO", "CASH"});
            methodBox.setSelectedItem(p.getMethod());
            JComboBox statusBox = new JComboBox(new String[]{"PENDING", "COMPLETED", "FAILED"});
            statusBox.setSelectedItem(p.getStatus());

            Object[] msg = {
                    "Amount:", amountField,
                    "Method:", methodBox,
                    "Status:", statusBox
            };

            int res = JOptionPane.showConfirmDialog(this, msg, "Edit Payment",
                    JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            p.setAmount(Double.parseDouble(amountField.getText().trim()));
            p.setMethod((String) methodBox.getSelectedItem());
            p.setStatus((String) statusBox.getSelectedItem());

            boolean ok = paymentDAO.update(p);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Payment updated successfully!");
                loadPayments();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /** Delete selected payment */
    private void deleteSelectedPayment() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a payment first.");
            return;
        }

        int id = (Integer) model.getValueAt(r, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete Payment ID " + id + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = paymentDAO.delete(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Payment deleted successfully.");
                loadPayments();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /** Export displayed payments to CSV file */
    private void exportToCSV() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No payments to export.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save payments as CSV");
        int res = fc.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".csv")) {
            f = new File(f.getParentFile(), f.getName() + ".csv");
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            // header
            for (int c = 0; c < model.getColumnCount(); c++) {
                bw.write(model.getColumnName(c));
                if (c < model.getColumnCount() - 1) bw.write(",");
            }
            bw.newLine();

            // rows
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Object v = model.getValueAt(r, c);
                    String cell = v == null ? "" : v.toString().replaceAll("\"", "\"\"");
                    if (cell.indexOf(',') >= 0 || cell.indexOf('"') >= 0)
                        cell = "\"" + cell + "\"";
                    bw.write(cell);
                    if (c < model.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();
            }

            bw.flush();
            JOptionPane.showMessageDialog(this, "Exported successfully to: " + f.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if (bw != null) try { bw.close(); } catch (Exception ignore) {}
        }
    }

    /** Standalone testing */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("Payments");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.getContentPane().add(new PaymentController());
                f.setSize(950, 520);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
