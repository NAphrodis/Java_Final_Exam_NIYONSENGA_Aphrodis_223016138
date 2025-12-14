package com.agriportal.view;

import com.agriportal.controller.*;
import com.agriportal.model.Admin;
import com.agriportal.model.Farmer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


  public class AgriPortalApp extends JFrame {

  private final JTabbedPane tabbedPane;
  private final Object loggedUser;

  public AgriPortalApp(final Admin admin) {
  super("AgriPortal - Admin: " + (admin != null ? admin.getName() : "Admin"));
  this.loggedUser = admin;
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setSize(1100, 700);
  setLocationRelativeTo(null);
  setLayout(new BorderLayout());

  
   // === MENU BAR ===
   JMenuBar menuBar = new JMenuBar();

   JMenu fileMenu = new JMenu("File");
   JMenuItem logout = new JMenuItem("Logout");
   JMenuItem exit = new JMenuItem("Exit");
   fileMenu.add(logout);
   fileMenu.addSeparator();
   fileMenu.add(exit);

   JMenu editMenu = new JMenu("Edit");
   JMenuItem refreshAll = new JMenuItem("Refresh All Tabs");
   JMenuItem clearCache = new JMenuItem("Clear Cache");
   editMenu.add(refreshAll);
   editMenu.add(clearCache);

   JMenu helpMenu = new JMenu("Help");
   JMenuItem about = new JMenuItem("About AgriPortal");
   JMenuItem support = new JMenuItem("Contact Support");
   helpMenu.add(about);
   helpMenu.add(support);

   menuBar.add(fileMenu);
   menuBar.add(editMenu);
   menuBar.add(helpMenu);
   setJMenuBar(menuBar);

   tabbedPane = new JTabbedPane();
   add(tabbedPane, BorderLayout.CENTER);

   // === MENU ACTIONS ===
   logout.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           doLogout();
       }
   });

   exit.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           System.exit(0);
       }
   });

   refreshAll.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(AgriPortalApp.this, "All data reloaded (demo).");
       }
   });

   clearCache.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(AgriPortalApp.this, "Local cache cleared (demo).");
       }
   });

   about.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(AgriPortalApp.this,
                   "AgriPortal Rwanda v2.0\nSmart Agriculture Management System\n© 2025 BioShroom & Team",
                   "About", JOptionPane.INFORMATION_MESSAGE);
       }
   });

   support.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(AgriPortalApp.this,
                   "For support, email: support@agriportal.rw",
                   "Contact Support", JOptionPane.INFORMATION_MESSAGE);
       }
   });

   buildAdminTabs(admin);
  

  }

  public AgriPortalApp(final Farmer farmer) {
  super("AgriPortal - Farmer: " + (farmer != null ? farmer.getName() : "Farmer"));
  this.loggedUser = farmer;
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setSize(1100, 700);
  setLocationRelativeTo(null);
  setLayout(new BorderLayout());

  
   JMenuBar menuBar = new JMenuBar();

   JMenu fileMenu = new JMenu("File");
   JMenuItem logout = new JMenuItem("Logout");
   JMenuItem exit = new JMenuItem("Exit");
   fileMenu.add(logout);
   fileMenu.addSeparator();
   fileMenu.add(exit);

   JMenu editMenu = new JMenu("Edit");
   JMenuItem refreshDashboard = new JMenuItem("Refresh Dashboard");
   editMenu.add(refreshDashboard);

   JMenu helpMenu = new JMenu("Help");
   JMenuItem about = new JMenuItem("About AgriPortal");
   helpMenu.add(about);

   menuBar.add(fileMenu);
   menuBar.add(editMenu);
   menuBar.add(helpMenu);
   setJMenuBar(menuBar);

   tabbedPane = new JTabbedPane();
   add(tabbedPane, BorderLayout.CENTER);

   logout.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           doLogout();
       }
   });

   exit.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           System.exit(0);
       }
   });

   refreshDashboard.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(AgriPortalApp.this, "Dashboard refreshed successfully!");
       }
   });

   about.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(AgriPortalApp.this,
                   "AgriPortal Rwanda v2.0\nEmpowering Farmers with Smart Insights",
                   "About", JOptionPane.INFORMATION_MESSAGE);
       }
   });

   buildFarmerTabs(farmer);
  

  }

  /** Build admin tabs */
  private void buildAdminTabs(final Admin admin) {
  try {
  final AdminDashboardView dash = new AdminDashboardView(admin);
  tabbedPane.addTab("Dashboard", dash);

  
       final AdminController adminCtrl = new AdminController();
       final FarmerController farmerCtrl = new FarmerController();
       final ProductController productCtrl = new ProductController();
       final CropController cropCtrl = new CropController();
       final OrderController adminOrderCtrl = new OrderController();
       final MarketplaceController marketplaceCtrl = new MarketplaceController();
       final ReportController reportCtrl = new ReportController();
       final AdminForecastController forecastCtrl = new AdminForecastController();
       final SettingsController settingsCtrl = new SettingsController();
       final AdminHarvestController harvestCtrl = new AdminHarvestController();
       final AdminIrrigationController irrigationCtrl = new AdminIrrigationController();
       final CustomerController customerCtrl = new CustomerController();

       tabbedPane.addTab("Admins", adminCtrl);
       tabbedPane.addTab("Farmers", farmerCtrl);
       tabbedPane.addTab("Customers", customerCtrl); // ✅ NEW
       tabbedPane.addTab("Products", productCtrl);
       tabbedPane.addTab("Crops", cropCtrl);
       tabbedPane.addTab("Orders", adminOrderCtrl);
       tabbedPane.addTab("Harvests", harvestCtrl);
       tabbedPane.addTab("Irrigation", irrigationCtrl);
       tabbedPane.addTab("Marketplace", marketplaceCtrl);
       tabbedPane.addTab("Reports", reportCtrl);
       tabbedPane.addTab("Forecasts", forecastCtrl);
       tabbedPane.addTab("Settings", settingsCtrl);

       // Connect dashboard buttons
       dash.addManageAdminsListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               tabbedPane.setSelectedComponent(adminCtrl);
           }
       });

       dash.addManageFarmersListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               tabbedPane.setSelectedComponent(farmerCtrl);
           }
       });

       dash.addReportsListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               tabbedPane.setSelectedComponent(reportCtrl);
           }
       });

       dash.addManageForecastsListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               tabbedPane.setSelectedComponent(forecastCtrl);
           }
       });

       dash.addSettingsListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               tabbedPane.setSelectedComponent(settingsCtrl);
           }
       });

       dash.addManageHarvestsListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               tabbedPane.setSelectedComponent(harvestCtrl);
           }
       });

       dash.addManageIrrigationListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               tabbedPane.setSelectedComponent(irrigationCtrl);
           }
       });

       dash.addManageFieldsListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               JFrame f = new JFrame("Manage Fields");
               f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
               f.getContentPane().add(new FieldController(null));
               f.setSize(900, 550);
               f.setLocationRelativeTo(null);
               f.setVisible(true);
           }
       });

   } catch (Exception ex) {
	    ex.printStackTrace();  // keep this
	    JOptionPane.showMessageDialog(this,
	        "Failed to initialize admin panels: " + ex.getClass().getSimpleName() + " → " + ex.getMessage(),
	        "Error", JOptionPane.ERROR_MESSAGE);
	}
}
  

  /** Build farmer tabs */
  private void buildFarmerTabs(final Farmer farmer) {
  try {
  final FarmerUIController farmerDashboard = new FarmerUIController(farmer);
  tabbedPane.addTab("Dashboard", farmerDashboard);

  
       final CropController cropCtrl = new CropController(farmer);
       final ProductController productCtrl = new ProductController(farmer);
       final OrderController farmerOrderCtrl = new OrderController(farmer);
       final MarketplaceController marketplaceCtrl = new MarketplaceController();
       final FarmerForecastController forecastCtrl = new FarmerForecastController();

       tabbedPane.addTab("My Crops", cropCtrl);
       tabbedPane.addTab("My Products", productCtrl);
       tabbedPane.addTab("Orders", farmerOrderCtrl);
       tabbedPane.addTab("Marketplace", marketplaceCtrl);
       tabbedPane.addTab("Weather & Advice", forecastCtrl);

   } catch (Exception ex) {
       JOptionPane.showMessageDialog(this, "Failed to initialize farmer panels: " + ex.getMessage(),
               "Error", JOptionPane.ERROR_MESSAGE);
       ex.printStackTrace();
   }
  

  }

  private void doLogout() {
  int confirm = JOptionPane.showConfirmDialog(this, "Logout and return to login screen?",
  "Confirm Logout", JOptionPane.YES_NO_OPTION);
  if (confirm != JOptionPane.YES_OPTION) return;
  dispose();
  final com.agriportal.controller.LoginController login = new com.agriportal.controller.LoginController();
  SwingUtilities.invokeLater(new Runnable() {
  public void run() {
  login.setVisible(true);
  }
  });
  }

  public static void main(String[] args) {
  SwingUtilities.invokeLater(new Runnable() {
  public void run() {
  AgriPortalApp app = new AgriPortalApp((Admin) null);
  app.setVisible(true);
  }
  });
  }
  }
