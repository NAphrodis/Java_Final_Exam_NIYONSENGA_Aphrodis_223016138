package com.agriportal.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


public class LoginView extends JPanel {

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Select Role", "Admin", "Farmer", "Customer"});


    private final JButton btnLogin = new JButton("Login");
    private final JButton btnRegister = new JButton("Register");

    public LoginView() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 245, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Agriculture Portal Login");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(0, 102, 102));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        add(roleCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        btnLogin.setBackground(new Color(0, 102, 51));
        btnLogin.setForeground(Color.WHITE);
        btnRegister.setBackground(new Color(51, 102, 153));
        btnRegister.setForeground(Color.WHITE);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnLogin);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    // === Accessor Methods ===
    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }

    public String getSelectedRole() {
        return roleCombo.getSelectedItem() != null ? roleCombo.getSelectedItem().toString() : "";
    }

    // === Listener registration ===
    public void addLoginListener(ActionListener l) {
        btnLogin.addActionListener(l);
    }

    public void addRegisterListener(ActionListener l) {
        btnRegister.addActionListener(l);
    }

    // === For testing standalone ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("LoginView Test");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.getContentPane().add(new LoginView());
                f.pack();
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}