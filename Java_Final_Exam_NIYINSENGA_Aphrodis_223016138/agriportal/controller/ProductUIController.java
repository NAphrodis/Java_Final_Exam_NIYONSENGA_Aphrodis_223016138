package com.agriportal.controller;

import com.agriportal.model.Product;
import com.agriportal.model.Farmer;
import com.agriportal.model.dao.FarmerDAO;
import com.agriportal.model.dao.ProductDAO;
import com.agriportal.view.ProductListingView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;


public class ProductUIController extends JFrame {

    private final ProductListingView view;
    private final ProductDAO productDAO;
    private final FarmerDAO farmerDAO;

    public ProductUIController() {
        super("Product Management");
        view = new ProductListingView();
        productDAO = new ProductDAO();
        farmerDAO = new FarmerDAO();

        getContentPane().add(view);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.addRefreshListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadProducts();
            }
        });

        view.addAddListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddProductDialog();
            }
        });

        view.addEditListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedProduct();
            }
        });

        view.addDeleteListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedProduct();
            }
        });

        loadProducts();
    }

    private void loadProducts() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        try {
            List<Product> list = productDAO.findAll();
            if (list != null) {
                for (Product p : list) {
                    String seller = p.getSeller() != null ? (p.getSeller().getName() + " (id:" + p.getSeller().getId() + ")") : "Unknown";
                    model.addRow(new Object[]{p.getId(), p.getName(), seller, p.getPrice(), p.getQuantity(), p.getCategory()});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddProductDialog() {
        try {
            List<Farmer> farmers = farmerDAO.findAll();
            if (farmers == null || farmers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No farmers available. Add farmers first.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // simple dialog using JOptionPane
            JTextField nameF = new JTextField();
            JTextField descF = new JTextField();
            JTextField priceF = new JTextField();
            JTextField qtyF = new JTextField();
            JTextField catF = new JTextField();

            String[] farmerItems = new String[farmers.size()];
            for (int i = 0; i < farmers.size(); i++) {
                Farmer fr = farmers.get(i);
                farmerItems[i] = fr.getId() + " - " + fr.getName();
            }
            JComboBox farmerBox = new JComboBox(farmerItems);

            Object[] fields = {
                    "Name:", nameF,
                    "Description:", descF,
                    "Price:", priceF,
                    "Quantity:", qtyF,
                    "Category:", catF,
                    "Seller:", farmerBox
            };

            int res = JOptionPane.showConfirmDialog(this, fields, "Add Product", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            String name = nameF.getText().trim();
            String desc = descF.getText().trim();
            double price;
            int qty;
            try {
                price = Double.parseDouble(priceF.getText().trim());
                qty = Integer.parseInt(qtyF.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price or quantity invalid.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int sel = farmerBox.getSelectedIndex();
            Farmer seller = farmers.get(sel);

            Product p = new Product();
            p.setName(name);
            p.setDescription(desc.isEmpty() ? null : desc);
            p.setPrice(price);
            p.setQuantity(qty);
            p.setCategory(catF.getText().trim().isEmpty() ? null : catF.getText().trim());
            p.setSeller(seller);

            productDAO.insert(p);
            loadProducts();
            JOptionPane.showMessageDialog(this, "Product added.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add product: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void editSelectedProduct() {
        int id = view.getSelectedProductId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            Product p = productDAO.findById(id);
            if (p == null) { JOptionPane.showMessageDialog(this, "Product not found.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            JTextField nameF = new JTextField(p.getName());
            JTextField descF = new JTextField(p.getDescription() != null ? p.getDescription() : "");
            JTextField priceF = new JTextField(String.valueOf(p.getPrice()));
            JTextField qtyF = new JTextField(String.valueOf(p.getQuantity()));
            JTextField catF = new JTextField(p.getCategory() != null ? p.getCategory() : "");

            Object[] fields = {
                "Name:", nameF,
                "Description:", descF,
                "Price:", priceF,
                "Quantity:", qtyF,
                "Category:", catF
            };

            int res = JOptionPane.showConfirmDialog(this, fields, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            double price = Double.parseDouble(priceF.getText().trim());
            int qty = Integer.parseInt(qtyF.getText().trim());
            p.setName(nameF.getText().trim());
            p.setDescription(descF.getText().trim().isEmpty() ? null : descF.getText().trim());
            p.setPrice(price);
            p.setQuantity(qty);
            p.setCategory(catF.getText().trim().isEmpty() ? null : catF.getText().trim());

            productDAO.update(p);
            loadProducts();
            JOptionPane.showMessageDialog(this, "Product updated.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to edit product: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Price or quantity invalid.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedProduct() {
        int id = view.getSelectedProductId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete product id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = productDAO.delete(id);
            if (ok) {
                loadProducts();
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
