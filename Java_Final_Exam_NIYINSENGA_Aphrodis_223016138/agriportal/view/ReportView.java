package com.agriportal.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * ReportView - simple UI to generate common reports (CSV / screen).
 */
public class ReportView extends JPanel {

    private final JComboBox<String> reportType = new JComboBox<String>(new String[]{"Sales by product", "Stock summary", "Farmer activity"});
    private final JButton btnGenerate = new JButton("Generate");
    private final JTextArea outputArea = new JTextArea();

    public ReportView() {
        setLayout(new BorderLayout(8,8));
        setBackground(new Color(245, 245, 255));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(new JLabel("Report:"));
        top.add(reportType);
        btnGenerate.setBackground(new Color(30,120,100)); btnGenerate.setForeground(Color.WHITE);
        top.add(btnGenerate);
        add(top, BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    public void addGenerateListener(ActionListener l) { btnGenerate.addActionListener(l); }
    public String getSelectedReport() { return (String)reportType.getSelectedItem(); }
    public void setOutput(String txt) { outputArea.setText(txt); }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("ReportView Test");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.getContentPane().add(new ReportView());
                f.setSize(800, 600);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}
