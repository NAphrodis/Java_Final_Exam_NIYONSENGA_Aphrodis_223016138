package com.agriportal.controller;

import com.agriportal.model.Farmer;
import com.agriportal.model.Product;
import com.agriportal.model.dao.FarmerDAO;
import com.agriportal.model.dao.ProductDAO;
import com.agriportal.view.ProductListingView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;


public class ProductController extends JPanel {

    private final ProductListingView view;
    private final ProductDAO productDAO;
    private final FarmerDAO farmerDAO;
    private final Farmer farmer; // may be null => admin mode

    public ProductController() {
        this(null);
    }

    public ProductController(Farmer farmer) {
        this.farmer = farmer;
        this.productDAO = new ProductDAO();
        this.farmerDAO = new FarmerDAO();

        setLayout(new BorderLayout());
        view = new ProductListingView();
        add(view, BorderLayout.CENTER);

        // wire actions
        view.addRefreshListener(new ActionListener() { public void actionPerformed(ActionEvent e) { load(); }});
        view.addAddListener(new ActionListener() { public void actionPerformed(ActionEvent e) { addProductDialog(); }});
        view.addEditListener(new ActionListener() { public void actionPerformed(ActionEvent e) { editSelectedProduct(); }});
        view.addDeleteListener(new ActionListener() { public void actionPerformed(ActionEvent e) { deleteSelectedProduct(); }});

        load();
    }

    public void load() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        try {
            List<Product> list = productDAO.findAll();
            if (list != null) {
                for (Product p : list) {
                    if (farmer != null) {
                        // filter to only this farmer's products
                        if (p.getSeller() == null || p.getSeller().getId() != farmer.getId()) continue;
                    }
                    String seller = p.getSeller() != null ? p.getSeller().getName() : "Unknown";
                    model.addRow(new Object[]{p.getId(), p.getName(), seller, p.getPrice(), p.getQuantity(), p.getCategory()});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void addProductDialog() {
        try {
            // If in farmer mode, the seller is fixed; else allow selection.
            Farmer seller = null;
            JComboBox sellerBox = null;
            java.util.List<Farmer> farmers = null;
            if (this.farmer == null) {
                // admin mode -> choose seller from farmers
                farmers = farmerDAO.findAll();
                if (farmers == null || farmers.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No farmers available. Add farmers first.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String[] farmerItems = new String[farmers.size()];
                for (int i = 0; i < farmers.size(); i++) farmerItems[i] = farmers.get(i).getId() + " - " + farmers.get(i).getName();
                sellerBox = new JComboBox(farmerItems);
            } else {
                // farmer mode -> seller fixed
                seller = this.farmer;
            }

            JTextField nameField = new JTextField();
            JTextField descField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField qtyField = new JTextField();
            JTextField categoryField = new JTextField();

            java.util.List<Object> fields = new java.util.ArrayList<Object>();
            fields.add("Name:"); fields.add(nameField);
            fields.add("Description:"); fields.add(descField);
            fields.add("Price:"); fields.add(priceField);
            fields.add("Quantity:"); fields.add(qtyField);
            fields.add("Category:"); fields.add(categoryField);
            if (sellerBox != null) { fields.add("Seller:"); fields.add(sellerBox); }

            int res = JOptionPane.showConfirmDialog(this, fields.toArray(), "Add Product", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            double price;
            int qty;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                qty = Integer.parseInt(qtyField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price and quantity must be numeric.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (seller == null && sellerBox != null) {
                int sel = sellerBox.getSelectedIndex();
                seller = farmers.get(sel);
            }

            Product p = new Product();
            p.setName(name);
            p.setDescription(desc.isEmpty() ? null : desc);
            p.setPrice(price);
            p.setQuantity(qty);
            p.setCategory(categoryField.getText().trim().isEmpty() ? null : categoryField.getText().trim());
            p.setSeller(seller);

            productDAO.insert(p);
            load();
            JOptionPane.showMessageDialog(this, "Product added.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add product: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private int getSelectedProductId() {
        return view.getSelectedProductId();
    }

    private void editSelectedProduct() {
        int id = getSelectedProductId();
        if (id <= 0) { JOptionPane.showMessageDialog(this, "Select a product first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        try {
            Product p = productDAO.findById(id);
            if (p == null) { JOptionPane.showMessageDialog(this, "Product not found.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            // If farmer mode, ensure this product belongs to this farmer
            if (farmer != null && (p.getSeller() == null || p.getSeller().getId() != farmer.getId())) {
                JOptionPane.showMessageDialog(this, "You can only edit your own products.", "Permission", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField nameField = new JTextField(p.getName());
            JTextField descField = new JTextField(p.getDescription() != null ? p.getDescription() : "");
            JTextField priceField = new JTextField(String.valueOf(p.getPrice()));
            JTextField qtyField = new JTextField(String.valueOf(p.getQuantity()));
            JTextField catField = new JTextField(p.getCategory() != null ? p.getCategory() : "");

            Object[] fields = {
                    "Name:", nameField,
                    "Description:", descField,
                    "Price:", priceField,
                    "Quantity:", qtyField,
                    "Category:", catField
            };

            int res = JOptionPane.showConfirmDialog(this, fields, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            double price = Double.parseDouble(priceField.getText().trim());
            int qty = Integer.parseInt(qtyField.getText().trim());
            p.setName(nameField.getText().trim());
            p.setDescription(descField.getText().trim().isEmpty() ? null : descField.getText().trim());
            p.setPrice(price);
            p.setQuantity(qty);
            p.setCategory(catField.getText().trim().isEmpty() ? null : catField.getText().trim());

            productDAO.update(p);
            load();
            JOptionPane.showMessageDialog(this, "Product updated.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to edit product: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Price or quantity invalid.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedProduct() {
        int id = getSelectedProductId();
        if (id <= 0) { JOptionPane.showMessageDialog(this, "Select a product first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        try {
            Product p = productDAO.findById(id);
            if (p == null) { JOptionPane.showMessageDialog(this, "Product not found.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            // If farmer mode, ensure own product
            if (farmer != null && (p.getSeller() == null || p.getSeller().getId() != farmer.getId())) {
                JOptionPane.showMessageDialog(this, "You can only delete your own products.", "Permission", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Delete product id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            boolean ok = productDAO.delete(id);
            if (ok) {
                load();
                JOptionPane.showMessageDialog(this, "Product deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Product not deleted.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete product: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
