package com.agriportal.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * OrderView - lists orders and provides confirm / cancel actions.
 */
public class OrderView extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnConfirm = new JButton("Confirm");
    private final JButton btnCancelOrder = new JButton("Cancel");
    private final JButton btnRefresh = new JButton("Refresh");

    public OrderView() {
        setLayout(new BorderLayout(8,8));
        setBackground(new Color(255, 245, 250));
        JLabel title = new JLabel("Orders");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(120, 30, 60));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID","Product","Buyer","Qty","Date","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);
        btnConfirm.setBackground(new Color(30,130,30)); btnConfirm.setForeground(Color.WHITE);
        btnCancelOrder.setBackground(new Color(160,20,20)); btnCancelOrder.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(100,100,100)); btnRefresh.setForeground(Color.WHITE);
        p.add(btnRefresh); p.add(btnConfirm); p.add(btnCancelOrder);
        add(p, BorderLayout.SOUTH);
    }

    public void addConfirmListener(ActionListener l) { btnConfirm.addActionListener(l); }
    public void addCancelListener(ActionListener l) { btnCancelOrder.addActionListener(l); }
    public void addRefreshListener(ActionListener l) { btnRefresh.addActionListener(l); }
    public DefaultTableModel getTableModel() { return model; }
    public JTable getTable() { return table; }

    public int getSelectedOrderId() {
        int r = table.getSelectedRow();
        if (r < 0) return -1;
        return Integer.parseInt(String.valueOf(model.getValueAt(r, 0)));
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("OrderView Test");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                OrderView v = new OrderView();
                v.getTableModel().addRow(new Object[]{1,"Maize","Buyer A",5,"2025-10-10","Pending"});
                v.getTableModel().addRow(new Object[]{2,"Fertilizer","Buyer B",2,"2025-10-11","Confirmed"});
                f.getContentPane().add(v);
                f.setSize(900, 450); f.setLocationRelativeTo(null); f.setVisible(true);
            }
        });
    }
}
