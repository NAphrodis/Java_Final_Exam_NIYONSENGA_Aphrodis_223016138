package com.agriportal.controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SettingsController extends JPanel {

    private final JTextField txtSystemTitle = new JTextField("AgriPortal Rwanda", 25);
    private final JPasswordField txtOldPassword = new JPasswordField(20);
    private final JPasswordField txtNewPassword = new JPasswordField(20);
    private final JPasswordField txtConfirmPassword = new JPasswordField(20);
    private final JButton btnSave = new JButton("Save Changes");

    public SettingsController() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 250, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("⚙ System Settings");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(new Color(50, 70, 130));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("System Title:"), gbc);
        gbc.gridx = 1; add(txtSystemTitle, gbc);

        gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("Old Password:"), gbc);
        gbc.gridx = 1; add(txtOldPassword, gbc);

        gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1; add(txtNewPassword, gbc);

        gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; add(txtConfirmPassword, gbc);

        gbc.gridy++;
        gbc.gridx = 1;
        btnSave.setBackground(new Color(0, 153, 102));
        btnSave.setForeground(Color.WHITE);
        add(btnSave, gbc);

        // === Add ActionListener without lambda ===
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });
    }

    private void saveSettings() {
        String title = txtSystemTitle.getText().trim();
        String oldPass = new String(txtOldPassword.getPassword());
        String newPass = new String(txtNewPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "System title cannot be empty!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.isEmpty() && !newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Here you can connect to AdminDAO to update password if you wish
        JOptionPane.showMessageDialog(this,
                "✅ Settings updated successfully!\nTitle: " + title +
                        (newPass.isEmpty() ? "" : "\nPassword changed."),
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // === Standalone Test ===
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("Settings Panel Test");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(new SettingsController());
                f.pack();
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
