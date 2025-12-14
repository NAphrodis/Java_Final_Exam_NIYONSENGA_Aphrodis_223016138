package com.agriportal.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * AddFieldView - simple form for Add / Edit Field
 * Capacity label uses "Capacity (ha)" as requested.
 */
public class AddFieldView extends JPanel {

    private final JTextField nameField = new JTextField(25);
    private final JTextField addressField = new JTextField(30);
    private final JTextField capacityField = new JTextField(8);
    private final JTextField managerField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);

    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    public AddFieldView() {
        setLayout(new GridBagLayout());
        setBackground(new Color(250, 250, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,8,6,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Add / Edit Field");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("Name:"), gbc); gbc.gridx = 1; add(nameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("Address:"), gbc); gbc.gridx = 1; add(addressField, gbc);

        gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("Capacity (ha):"), gbc); gbc.gridx = 1; add(capacityField, gbc);

        gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("Manager:"), gbc); gbc.gridx = 1; add(managerField, gbc);

        gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("Contact:"), gbc); gbc.gridx = 1; add(contactField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnSave.setBackground(new Color(10,130,90)); btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(180,40,40)); btnCancel.setForeground(Color.WHITE);
        btnPanel.add(btnCancel); btnPanel.add(btnSave);

        gbc.gridy++;
        gbc.gridx = 0; gbc.gridwidth = 2; add(btnPanel, gbc);
    }

    // getters
    public String getNameValue() { return nameField.getText().trim(); }
    public String getAddressValue() { return addressField.getText().trim(); }
    public String getCapacityValue() { return capacityField.getText().trim(); }
    public String getManagerValue() { return managerField.getText().trim(); }
    public String getContactValue() { return contactField.getText().trim(); }

    // setters - used when editing
    public void setNameValue(String s) { nameField.setText(s); }
    public void setAddressValue(String s) { addressField.setText(s); }
    public void setCapacityValue(String s) { capacityField.setText(s); }
    public void setManagerValue(String s) { managerField.setText(s); }
    public void setContactValue(String s) { contactField.setText(s); }

    // listeners
    public void addSaveListener(ActionListener l) { btnSave.addActionListener(l); }
    public void addCancelListener(ActionListener l) { btnCancel.addActionListener(l); }
}
