package com.agriportal.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;


public class AddCropView extends JPanel {

    private final JTextField txtCropName = new JTextField(30);
    private final JTextField txtVariety = new JTextField(20);
    private final JTextField txtPlanted = new JTextField(12);          // yyyy-MM-dd
    private final JTextField txtHarvest = new JTextField(12);          // yyyy-MM-dd
    private final JTextField txtExpectedYield = new JTextField(10);    // numeric

    private final JComboBox<String> comboFields = new JComboBox<String>();
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    public AddCropView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(250, 250, 250));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Add Crop");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(new Color(10, 90, 60));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(title);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; center.add(new JLabel("Crop name:"), gbc);
        gbc.gridx = 1; center.add(txtCropName, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; center.add(new JLabel("Variety:"), gbc);
        gbc.gridx = 1; center.add(txtVariety, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; center.add(new JLabel("Planted (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; center.add(txtPlanted, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; center.add(new JLabel("Expected harvest (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; center.add(txtHarvest, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; center.add(new JLabel("Expected yield (kg or tonnes):"), gbc);
        gbc.gridx = 1; center.add(txtExpectedYield, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; center.add(new JLabel("Select Field:"), gbc);
        gbc.gridx = 1; center.add(comboFields, gbc);
        row++;

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        styleButton(btnCancel, new Color(140, 140, 140));
        styleButton(btnSave, new Color(0, 120, 80));
        bottom.add(btnCancel);
        bottom.add(btnSave);
        add(bottom, BorderLayout.SOUTH);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    // === Mutators expected by controllers ===

    /**
     * Fill the field combo with display strings like "12 - FieldName".
     */
    public void setFieldItems(List<String> items) {
        comboFields.removeAllItems();
        if (items == null) return;
        for (int i = 0; i < items.size(); i++) {
            comboFields.addItem(items.get(i));
        }
        if (comboFields.getItemCount() > 0) comboFields.setSelectedIndex(0);
    }

    /**
     * Select a field by index (0-based).
     */
    public void setSelectedFieldIndex(int idx) {
        if (idx >= 0 && idx < comboFields.getItemCount()) comboFields.setSelectedIndex(idx);
    }

    /**
     * Disable the field selection (for farmers who only own one field).
     */
    public void disableFieldSelection() {
        comboFields.setEnabled(false);
    }

    // === Accessors for controller to read values ===

    public String getCropName() {
        return txtCropName.getText().trim();
    }

    public String getVariety() {
        return txtVariety.getText().trim();
    }

    public String getPlanted() {
        return txtPlanted.getText().trim();
    }

    public String getHarvest() {
        return txtHarvest.getText().trim();
    }

    public String getExpectedYield() {
        return txtExpectedYield.getText().trim();
    }

    
    
    public int getSelectedFieldIndex() {
        return comboFields.getSelectedIndex();
    }

    // === Listener wiring ===

    public void addSaveListener(ActionListener l) {
        btnSave.addActionListener(l);
    }

    public void addCancelListener(ActionListener l) {
        btnCancel.addActionListener(l);
    }

    // === Test demo ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("AddCropView Test");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                AddCropView v = new AddCropView();
                java.util.List<String> items = new java.util.ArrayList<String>();
                items.add("1 - Green Valley Field");
                items.add("2 - Hilltop Land");
                v.setFieldItems(items);
                f.getContentPane().add(v);
                f.setSize(640, 360);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
