package com.agriportal.controller;

import com.agriportal.model.Forecast;
import com.agriportal.model.dao.ForecastDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;


public class FarmerForecastController extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;
    private final ForecastDAO forecastDAO = new ForecastDAO();

    public FarmerForecastController() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 255, 250));

        JLabel title = new JLabel("🌦 Weather Forecast & 🌱 Crop Recommendations", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(0, 102, 51));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{
                "Date", "Condition", "Temperature (°C)", "Rainfall (mm)", "Crop Recommendation"
        }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Admin-Published Forecasts"));
        add(sp, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(0, 153, 102));
        refreshBtn.setForeground(Color.WHITE);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);

        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                loadForecasts();
            }
        });

        loadForecasts();
    }

    private void loadForecasts() {
        model.setRowCount(0);
        try {
            List<Forecast> forecasts = forecastDAO.findAll();
            for (int i = 0; i < forecasts.size(); i++) {
                Forecast f = forecasts.get(i);
                model.addRow(new Object[]{
                        f.getDate(),
                        f.getCondition(),
                        f.getTemperature(),
                        f.getRainfall(),
                        f.getRecommendation()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load forecasts: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // quick standalone test
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("Farmer Forecast View");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.getContentPane().add(new FarmerForecastController());
                f.setSize(800, 500);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
