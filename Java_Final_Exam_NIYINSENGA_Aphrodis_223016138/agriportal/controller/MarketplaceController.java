package com.agriportal.controller;

import com.agriportal.model.*;
import com.agriportal.model.dao.*;
import com.agriportal.view.MarketplaceView;
import com.agriportal.view.ProductListingView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class MarketplaceController extends JPanel {

    private final MarketplaceView view;
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final Customer loggedCustomer;
    private final Farmer loggedFarmer;

    // === Constructors ===
    public MarketplaceController() { this(null, null); }
    public MarketplaceController(Customer customer) { this(customer, null); }
    public MarketplaceController(Farmer farmer) { this(null, farmer); }

    private MarketplaceController(Customer customer, Farmer farmer) {
        this.loggedCustomer = customer;
        this.loggedFarmer = farmer;

        setLayout(new BorderLayout());
        view = new MarketplaceView();
        add(view, BorderLayout.CENTER);

        // === Refresh Button ===
        view.getListingView().addRefreshListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadProducts();
            }
        });

        // === Buy Button ===
        view.addBuyListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleBuy();
            }
        });

        // Hide management controls (Add/Edit/Delete)
        view.getListingView().hideManagementButtons();

        // Role setup
        if (loggedCustomer != null) {
            view.setBuyerName(loggedCustomer.getName());
            view.disableBuyerField();
        } else {
            // Farmer/admin mode – hide buyer panel
            view.hideBuyerPanel();
        }

        loadProducts();
        
     // === Live Search Integration ===
        view.getListingView().addSearchListener(new Runnable() {
            public void run() {
                performSearch(view.getListingView().getSearchText());
            }
        });

    }

    /** Loads all products into the table */
    private void loadProducts() {
        ProductListingView pl = view.getListingView();
        JTable table = pl.getTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            List<Product> list = productDAO.findAll();
            if (list != null && !list.isEmpty()) {
                for (Product p : list) {
                    String seller = (p.getSeller() != null) ? p.getSeller().getName() : "Unknown";
                    model.addRow(new Object[]{
                            p.getId(), p.getName(), seller, p.getPrice(), p.getQuantity(), p.getCategory()
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "No products available in the marketplace yet.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }

            revalidate();
            repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void performSearch(String keyword) {
        ProductListingView pl = view.getListingView();
        JTable table = pl.getTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            List<Product> list;
            if (keyword == null || keyword.trim().isEmpty()) {
                list = productDAO.findAll();
            } else {
                list = productDAO.search(keyword);
            }

            for (Product p : list) {
                String seller = (p.getSeller() != null) ? p.getSeller().getName() : "Unknown";
                model.addRow(new Object[]{p.getId(), p.getName(), seller, p.getPrice(), p.getQuantity(), p.getCategory()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    /** Handles customer purchase flow */
    private void handleBuy() {
        if (loggedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please login as a customer to buy products.");
            return;
        }

        ProductListingView pl = view.getListingView();
        JTable table = pl.getTable();
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.");
            return;
        }

        int productId = (Integer) table.getValueAt(row, 0);
        String qtyTxt = view.getQuantityText();
        int qty;

        try {
            qty = Integer.parseInt(qtyTxt.trim());
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format.");
            return;
        }

        try {
            Product p = productDAO.findById(productId);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Product not found in database.");
                return;
            }

            if (p.getQuantity() < qty) {
                JOptionPane.showMessageDialog(this, "Only " + p.getQuantity() + " items available.",
                        "Stock Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double total = p.getPrice() * qty;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirm Purchase:\nProduct: " + p.getName() +
                            "\nPrice: " + p.getPrice() +
                            "\nQuantity: " + qty +
                            "\nTotal: " + total + "\n\nProceed?",
                    "Purchase Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            Order o = new Order();
            o.setProduct(p);
            o.setBuyer(loggedCustomer);
            o.setBuyerName(loggedCustomer.getName());
            o.setQuantityOrdered(qty);
            o.setOrderDate(LocalDate.now());
            o.setStatus("Pending");

            boolean ok = orderDAO.insertAndReduceStock(o);
            if (ok) {
                JOptionPane.showMessageDialog(this, "✅ Order placed successfully!");
                loadProducts();

                // 🔁 Auto-refresh customer's order table if inside dashboard
                Container parent = getParent();
                while (parent != null && !(parent instanceof CustomerDashboardController))
                    parent = parent.getParent();
                if (parent instanceof CustomerDashboardController)
                    ((CustomerDashboardController) parent).loadOrders();

            } else {
                JOptionPane.showMessageDialog(this, "❌ Order failed. Possibly not enough stock.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
