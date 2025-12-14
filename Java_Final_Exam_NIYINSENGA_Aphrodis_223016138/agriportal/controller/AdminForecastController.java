package com.agriportal.controller;

import com.agriportal.model.Forecast;
import com.agriportal.model.dao.ForecastDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * AdminForecastController - connects to database via ForecastDAO.
 */
public class AdminForecastController extends JPanel {

    private final ForecastDAO dao = new ForecastDAO();
    private final DefaultTableModel model;
    private final JTable table;

    private final JTextField txtDate = new JTextField(10);
    private final JTextField txtCondition = new JTextField(10);
    private final JTextField txtTemp = new JTextField(5);
    private final JTextField txtRain = new JTextField(5);
    private final JTextField txtRecommendation = new JTextField(20);

    private final JButton btnAdd = new JButton("Add Forecast");
    private final JButton btnDelete = new JButton("Delete Selected");
    private final JButton btnRefresh = new JButton("Refresh");

    public AdminForecastController() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(250, 250, 255));

        JLabel title = new JLabel("🌦 Manage Weather Forecasts & Crop Recommendations");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(40, 70, 130));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Date", "Condition", "Temp (°C)", "Rain (mm)", "Recommendation"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 6, 8, 5));
        form.setBorder(BorderFactory.createTitledBorder("Add New Forecast"));
        form.setOpaque(false);

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(txtDate);
        form.add(new JLabel("Condition:"));
        form.add(txtCondition);
        form.add(new JLabel("Temp °C:"));
        form.add(txtTemp);

        form.add(new JLabel("Rain (mm):"));
        form.add(txtRain);
        form.add(new JLabel("Recommendation:"));
        form.add(txtRecommendation);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnAdd.setBackground(new Color(0, 153, 102));
        btnAdd.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(200, 50, 50));
        btnDelete.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(120, 120, 120));
        btnRefresh.setForeground(Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(form, BorderLayout.CENTER);
        south.add(btnPanel, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        try {
            dao.createTable(); // Ensure table exists
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Listeners without lambdas
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addForecast();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelected();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadForecasts();
            }
        });

        loadForecasts();
    }

    private void addForecast() {
        try {
            LocalDate date = LocalDate.parse(txtDate.getText().trim());
            String cond = txtCondition.getText().trim();
            double temp = txtTemp.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtTemp.getText().trim());
            double rain = txtRain.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtRain.getText().trim());
            String rec = txtRecommendation.getText().trim();

            Forecast f = new Forecast(date, cond, temp, rain, rec);
            dao.insert(f);
            JOptionPane.showMessageDialog(this, "Forecast saved successfully!");
            loadForecasts();
            clearInputs();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a forecast to delete.");
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete forecast ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.delete(id);
                loadForecasts();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    }

    private void loadForecasts() {
        model.setRowCount(0);
        try {
            List<Forecast> list = dao.findAll();
            for (Forecast f : list) {
                model.addRow(new Object[]{
                        f.getId(),
                        f.getDate(),
                        f.getCondition(),
                        f.getTemperature(),
                        f.getRainfall(),
                        f.getRecommendation()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load forecasts: " + ex.getMessage());
        }
    }

    private void clearInputs() {
        txtDate.setText("");
        txtCondition.setText("");
        txtTemp.setText("");
        txtRain.setText("");
        txtRecommendation.setText("");
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("Admin Forecast Manager (DB Version)");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(new AdminForecastController());
                f.setSize(950, 500);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
