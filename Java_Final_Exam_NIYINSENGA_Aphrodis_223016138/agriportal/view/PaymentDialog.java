package com.agriportal.view;

import com.agriportal.model.Payment;
import com.agriportal.model.dao.PaymentDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * PaymentDialog - collects payment info, simulates processing,
 * and records payment via PaymentDAO.
 */

public class PaymentDialog extends JDialog {

    private Integer orderId;
    private double amount;
    private String buyerName;

    private JTextField cardNumberField;
    private JTextField expiryField;
    private JTextField cvvField;
    private JTextField nameField;
    private JComboBox methodCombo;
    private JButton payButton;
    private boolean paymentSuccessful = false;

    public PaymentDialog(Window parent, Object orderObj, String buyerName) {
        // attempt to extract id/amount via reflection-safe approach:
        super(parent, "Payment Processing", ModalityType.APPLICATION_MODAL);
        this.buyerName = buyerName;
        if (orderObj != null) {
            try {
                // try common methods: getId() and getTotalAmount() or getAmount()
                java.lang.reflect.Method m1 = orderObj.getClass().getMethod("getId");
                Object oid = m1.invoke(orderObj);
                if (oid instanceof Number) this.orderId = ((Number) oid).intValue();
            } catch (Exception ex) { /* ignore */ }

            try {
                java.lang.reflect.Method m2 = null;
                try { m2 = orderObj.getClass().getMethod("getTotalAmount"); }
                catch (NoSuchMethodException ns) { try { m2 = orderObj.getClass().getMethod("getAmount"); } catch (Exception ex) { m2 = null; } }
                if (m2 != null) {
                    Object amt = m2.invoke(orderObj);
                    if (amt instanceof Number) this.amount = ((Number) amt).doubleValue();
                }
            } catch (Exception ex) { /* ignore */ }
        }
        initialize();
    }

    public PaymentDialog(Window parent, Integer orderId, double amount, String buyerName) {
        super(parent, "Payment Processing", ModalityType.APPLICATION_MODAL);
        this.orderId = orderId;
        this.amount = amount;
        this.buyerName = buyerName;
        initialize();
    }

    private void initialize() {
        setSize(480, 460);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(40, 130, 100));
        header.setPreferredSize(new Dimension(480, 60));
        JLabel h = new JLabel("Complete Payment", SwingConstants.CENTER);
        h.setForeground(Color.WHITE);
        h.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(h);
        add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Order / amount info
        JPanel info = new JPanel(new GridLayout(3, 1));
        String idText = (orderId == null) ? "-" : String.valueOf(orderId);
        info.add(new JLabel("Order ID: " + idText));
        info.add(new JLabel("Buyer: " + (buyerName == null ? "-" : buyerName)));
        info.add(new JLabel("Amount: " + amount + " RWF"));
        content.add(info, BorderLayout.NORTH);

        // payment method + fields
        JPanel form = new JPanel(new GridLayout(6, 2, 6, 6));

        form.add(new JLabel("Method:"));
        methodCombo = new JComboBox(new String[] { "Credit Card", "Mobile Money", "Cash" });
        form.add(methodCombo);

        form.add(new JLabel("Card Number:"));
        cardNumberField = new JTextField();
        form.add(cardNumberField);

        form.add(new JLabel("Expiry (MM/YY):"));
        expiryField = new JTextField();
        form.add(expiryField);

        form.add(new JLabel("CVV:"));
        cvvField = new JTextField();
        form.add(cvvField);

        form.add(new JLabel("Cardholder Name:"));
        nameField = new JTextField();
        nameField.setText(buyerName != null ? buyerName : "");
        form.add(nameField);

        content.add(form, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel();
        payButton = new JButton("Pay Now");
        JButton cancelBtn = new JButton("Cancel");
        styleButton(payButton, new Color(40,167,69), Color.WHITE);
        styleButton(cancelBtn, new Color(160,160,160), Color.WHITE);
        buttons.add(payButton);
        buttons.add(cancelBtn);
        add(buttons, BorderLayout.SOUTH);

        // initial state
        updateFormByMethod();

        // listeners
        methodCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { updateFormByMethod(); }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paymentSuccessful = false;
                dispose();
            }
        });

        payButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });
    }

    private void updateFormByMethod() {
        String method = (String) methodCombo.getSelectedItem();
        boolean card = "Credit Card".equals(method);
        cardNumberField.setEnabled(card);
        expiryField.setEnabled(card);
        cvvField.setEnabled(card);
        nameField.setEnabled(card);
        if (!card) {
            cardNumberField.setText("");
            expiryField.setText("");
            cvvField.setText("");
        }
    }

    private boolean validateCard() {
        String num = cardNumberField.getText().trim();
        String exp = expiryField.getText().trim();
        String cvv = cvvField.getText().trim();
        String nm = nameField.getText().trim();
        if (num.length() != 16) { JOptionPane.showMessageDialog(this, "Enter 16-digit card number"); return false; }
        if (exp.length() != 5) { JOptionPane.showMessageDialog(this, "Expiry must be MM/YY"); return false; }
        if (cvv.length() != 3) { JOptionPane.showMessageDialog(this, "Enter 3-digit CVV"); return false; }
        if (nm.length() == 0) { JOptionPane.showMessageDialog(this, "Enter cardholder name"); return false; }
        return true;
    }

    private void processPayment() {
        final String method = (String) methodCombo.getSelectedItem();
        if ("Credit Card".equals(method)) {
            if (!validateCard()) return;
        }
        payButton.setEnabled(false);
        payButton.setText("Processing...");

        // Simulate processing with a Swing Timer (no blocking)
        final javax.swing.Timer timer = new javax.swing.Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((javax.swing.Timer)e.getSource()).stop();
                // Simulated success outcome (you can randomize or add failure branch)
                boolean success = true;

                if (success) {
                    Payment p = new Payment();
                    p.setOrderId(orderId);
                    p.setAmount(amount);
                    if ("Credit Card".equals(method)) p.setMethod("CARD");
                    else if ("Mobile Money".equals(method)) p.setMethod("MOMO");
                    else p.setMethod("CASH");
                    p.setStatus("COMPLETED");
                    p.setPaidAt(new Timestamp(System.currentTimeMillis()));

                    PaymentDAO dao = new PaymentDAO();
                    try {
                        dao.insert(p);
                        paymentSuccessful = true;
                        JOptionPane.showMessageDialog(PaymentDialog.this,
                                "Payment successful!\nTransaction ID: " + p.getId(),
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        paymentSuccessful = false;
                        JOptionPane.showMessageDialog(PaymentDialog.this,
                                "Failed to record payment: " + ex.getMessage(),
                                "DB Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    paymentSuccessful = false;
                    JOptionPane.showMessageDialog(PaymentDialog.this,
                            "Payment failed. Please try again.",
                            "Payment Error", JOptionPane.ERROR_MESSAGE);
                }

                dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }
}
