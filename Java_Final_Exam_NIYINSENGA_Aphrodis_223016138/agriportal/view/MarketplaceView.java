package com.agriportal.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * MarketplaceView - combines ProductListingView + simple purchase UI.
 * Used by both customers and admins.
 */
public class MarketplaceView extends JPanel {

    private final ProductListingView listingView = new ProductListingView();
    private final JTextField txtBuyerName = new JTextField(20);
    private final JTextField txtQuantity = new JTextField(8);
    private final JButton btnBuy = new JButton("Buy Selected");

    public MarketplaceView() {
        setLayout(new BorderLayout(10,10));
        setBackground(new Color(245,250,245));

        // Top panel with buyer info
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(new JLabel("Buyer:"));
        top.add(txtBuyerName);
        top.add(new JLabel("Quantity:"));
        top.add(txtQuantity);
        btnBuy.setBackground(new Color(0,120,0));
        btnBuy.setForeground(Color.WHITE);
        top.add(btnBuy);

        add(top, BorderLayout.NORTH);
        add(listingView, BorderLayout.CENTER);
    }

    // === Accessors for customer dashboard ===
    public ProductListingView getListingView() {
        return listingView;
    }

    public String getBuyerName() {
        return txtBuyerName.getText().trim();
    }

    public String getQuantityText() {
        return txtQuantity.getText().trim();
    }

    // === Mutators ===
    public void setBuyerName(String name) {
        txtBuyerName.setText(name);
    }

    public void disableBuyerField() {
        txtBuyerName.setEditable(false);
        txtBuyerName.setBackground(new Color(230, 230, 230));
    }

    // === Listener registration ===
    public void addBuyListener(ActionListener l) {
        btnBuy.addActionListener(l);
    }

    // Helper for admins/farmers to hide buyer/quantity inputs
    public void hideBuyerPanel() {
        txtBuyerName.setVisible(false);
        txtQuantity.setVisible(false);
        btnBuy.setVisible(false);
    }

    // Forwarded method for convenience (used by customer dashboard)
    public void hideManagementButtons() {
        listingView.hideManagementButtons();
    }

    // === Demo ===
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("Marketplace Test");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                MarketplaceView v = new MarketplaceView();
                v.setBuyerName("Test Buyer");
                v.disableBuyerField();
                v.getListingView().hideManagementButtons();
                f.getContentPane().add(v);
                f.setSize(900, 600);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
