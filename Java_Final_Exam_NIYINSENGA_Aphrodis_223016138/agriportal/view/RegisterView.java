package com.agriportal.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Simple registration form panel for farmers.
 */
public class RegisterView extends JPanel {

    private final JTextField nameField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    private final JButton btnRegister = new JButton("Register");
    private final JButton btnCancel = new JButton("Cancel");

    public RegisterView() {
        setLayout(new GridBagLayout());
        setBackground(new Color(255, 250, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel header = new JLabel("Register New Farmer");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setForeground(new Color(80, 45, 15));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(header, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++; gbc.gridx = 0; add(new JLabel("Full name:"), gbc); gbc.gridx = 1; add(nameField, gbc);
        gbc.gridy++; gbc.gridx = 0; add(new JLabel("Phone:"), gbc); gbc.gridx = 1; add(phoneField, gbc);
        gbc.gridy++; gbc.gridx = 0; add(new JLabel("Username:"), gbc); gbc.gridx = 1; add(usernameField, gbc);
        gbc.gridy++; gbc.gridx = 0; add(new JLabel("Password:"), gbc); gbc.gridx = 1; add(passwordField, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        btnRegister.setBackground(new Color(0,130,100)); btnRegister.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(180,40,40)); btnCancel.setForeground(Color.WHITE);
        buttons.add(btnCancel); buttons.add(btnRegister);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        add(buttons, gbc);
    }

    public String getFullName() { return nameField.getText().trim(); }
    public String getPhone() { return phoneField.getText().trim(); }
    public String getUsername() { return usernameField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }

    public void addRegisterListener(ActionListener l) { btnRegister.addActionListener(l); }
    public void addCancelListener(ActionListener l) { btnCancel.addActionListener(l); }
}
