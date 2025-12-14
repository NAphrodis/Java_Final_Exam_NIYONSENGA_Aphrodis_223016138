package com.agriportal.view;

import com.agriportal.model.Field;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FarmInsightPanel extends JPanel {

    private final JTable weatherTable;
    private final DefaultTableModel weatherModel;
    private final JTextArea recommendationArea;
    
    // Note: These utility classes would need to be implemented separately
    // For now, we'll create simple mock implementations within this class

    public FarmInsightPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== Weather Forecast Panel =====
        JPanel weatherPanel = new JPanel(new BorderLayout());
        weatherPanel.setBorder(BorderFactory.createTitledBorder("🌦️ Weather Forecast"));
        
        weatherModel = new DefaultTableModel(new Object[]{"Date", "Condition", "Temp (°C)", "Rain (mm)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        weatherTable = new JTable(weatherModel);
        weatherTable.setRowHeight(25);
        weatherTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane weatherScroll = new JScrollPane(weatherTable);
        weatherPanel.add(weatherScroll, BorderLayout.CENTER);
        
        // ===== Recommendations Panel =====
        JPanel recommendationPanel = new JPanel(new BorderLayout());
        recommendationPanel.setBorder(BorderFactory.createTitledBorder("🌱 Crop Recommendations"));
        
        recommendationArea = new JTextArea();
        recommendationArea.setEditable(false);
        recommendationArea.setLineWrap(true);
        recommendationArea.setWrapStyleWord(true);
        recommendationArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        recommendationArea.setBackground(new Color(255, 255, 240));
        
        JScrollPane recScroll = new JScrollPane(recommendationArea);
        recScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        recommendationPanel.add(recScroll, BorderLayout.CENTER);
        
        // ===== Layout Setup =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, weatherPanel, recommendationPanel);
        splitPane.setDividerLocation(400);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Add default message
        recommendationArea.setText("Select a field to view weather forecast and crop recommendations.");
    }

    public void updateForField(final Field field) {
        weatherModel.setRowCount(0);
        
        if (field == null) {
            recommendationArea.setText("No field selected. Please select a field to view insights.");
            return;
        }

        try {
            // Fetch mock weather data (in real implementation, this would come from WeatherService)
            recommendationArea.setText("Loading weather data and recommendations for " + field.getName() + "...");
            
            // Simulate delay for data loading
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    loadWeatherData(field);
                    loadCropRecommendations(field);
                }
            });
            
        } catch (Exception e) {
            recommendationArea.setText("Error loading insights: " + e.getMessage());
        }
    }
    
    private void loadWeatherData(Field field) {
        // Mock weather data - in real implementation, use WeatherService
        weatherModel.addRow(new Object[]{"Today", "Sunny", "28°C", "0mm"});
        weatherModel.addRow(new Object[]{"Tomorrow", "Partly Cloudy", "26°C", "2mm"});
        weatherModel.addRow(new Object[]{"Day 3", "Rainy", "22°C", "15mm"});
        weatherModel.addRow(new Object[]{"Day 4", "Cloudy", "24°C", "5mm"});
        weatherModel.addRow(new Object[]{"Day 5", "Sunny", "27°C", "1mm"});
    }
    
    private void loadCropRecommendations(Field field) {
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("=== RECOMMENDATIONS FOR ").append(field.getName().toUpperCase()).append(" ===\n\n");
        
        recommendations.append("📍 Location: ").append(field.getLocation()).append("\n");
        recommendations.append("📏 Area: ").append(String.format("%.2f", field.getAreaHectares())).append(" hectares\n\n");
        
        // Mock crop recommendations based on field characteristics
        recommendations.append("🌾 RECOMMENDED CROPS:\n");
        recommendations.append("1. Maize: Suitable for your soil type and current season\n");
        recommendations.append("2. Beans: Good intercrop with maize, improves soil fertility\n");
        recommendations.append("3. Potatoes: High market demand, suitable for your climate\n\n");
        
        recommendations.append("💧 IRRIGATION ADVICE:\n");
        recommendations.append("- Water early morning (6-8 AM)\n");
        recommendations.append("- 2-3 times per week in dry season\n");
        recommendations.append("- Use drip irrigation for water conservation\n\n");
        
        recommendations.append("📅 PLANTING SCHEDULE:\n");
        recommendations.append("- Maize: Plant within next 2 weeks\n");
        recommendations.append("- Beans: Can be planted after maize germination\n");
        recommendations.append("- Potatoes: Best planted in cooler months\n\n");
        
        recommendations.append("⚠️ ALERTS:\n");
        recommendations.append("- Monitor for pests during rainy season\n");
        recommendations.append("- Soil testing recommended every 6 months\n");
        recommendations.append("- Consider crop rotation to maintain soil health");
        
        recommendationArea.setText(recommendations.toString());
    }
    
    // Utility method to clear all data
    public void clearData() {
        weatherModel.setRowCount(0);
        recommendationArea.setText("No field selected. Please select a field to view insights.");
    }
    
    // Test method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Farm Insight Panel Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                final FarmInsightPanel panel = new FarmInsightPanel();
                
                // Create a mock field for testing
                final Field testField = new Field();
                testField.setId(1);
                testField.setName("North Field");
                testField.setLocation("Rubavu District");
                testField.setAreaHectares(5.5);
                
                frame.getContentPane().add(panel);
                frame.setSize(900, 500);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                // Update panel with test data after a short delay
                Timer timer = new Timer(1000, new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        panel.updateForField(testField);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }
}