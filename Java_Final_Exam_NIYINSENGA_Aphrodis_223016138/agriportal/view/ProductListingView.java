package com.agriportal.view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * ProductListingView - table of products with action buttons + live search.
 */
public class ProductListingView extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField txtSearch = new JTextField(25);
    private final JButton btnAdd = new JButton("Add Product");
    private final JButton btnEdit = new JButton("Edit Product");
    private final JButton btnDelete = new JButton("Delete Product");
    private final JButton btnRefresh = new JButton("Refresh");

    public ProductListingView() {
        setLayout(new BorderLayout(8,8));
        setBackground(new Color(255, 250, 245));

        // ===== Title Panel =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Marketplace Products");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(120, 60, 20));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 Search:"));
        searchPanel.add(txtSearch);

        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(searchPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        // ===== Table =====
        model = new DefaultTableModel(new Object[]{"ID","Name","Seller","Price","Qty","Category"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);
        btnAdd.setBackground(new Color(34,139,34)); btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(60,120,170)); btnEdit.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(180,40,40)); btnDelete.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(120,120,120)); btnRefresh.setForeground(Color.WHITE);
        p.add(btnRefresh); p.add(btnAdd); p.add(btnEdit); p.add(btnDelete);
        add(p, BorderLayout.SOUTH);
    }

    // === Accessors ===
    public DefaultTableModel getTableModel() { return model; }
    public JTable getTable() { return table; }
    public String getSearchText() { return txtSearch.getText().trim(); }

    // === Listener registration ===
    public void addAddListener(ActionListener l) { btnAdd.addActionListener(l); }
    public void addEditListener(ActionListener l) { btnEdit.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addRefreshListener(ActionListener l) { btnRefresh.addActionListener(l); }

    /** Live search listener */
    public void addSearchListener(final Runnable onSearch) {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onSearch.run(); }
            public void removeUpdate(DocumentEvent e) { onSearch.run(); }
            public void changedUpdate(DocumentEvent e) { onSearch.run(); }
        });
    }

    public int getSelectedProductId() {
        int r = table.getSelectedRow();
        if (r < 0) return -1;
        Object v = model.getValueAt(r, 0);
        return v == null ? -1 : Integer.parseInt(String.valueOf(v));
    }

    /** Hide management buttons (for customers / read-only view). */
    public void hideManagementButtons() {
        btnAdd.setVisible(false);
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
    }

    // Demo
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("ProductListing Test");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ProductListingView v = new ProductListingView();
                v.getTableModel().addRow(new Object[]{1,"Maize","Farmer A",150.0,20,"Grain"});
                v.getTableModel().addRow(new Object[]{2,"Tomato","Farmer B",50.0,100,"Vegetable"});
                f.getContentPane().add(v);
                f.setSize(900, 450); f.setLocationRelativeTo(null); f.setVisible(true);
            }
        });
    }
}
