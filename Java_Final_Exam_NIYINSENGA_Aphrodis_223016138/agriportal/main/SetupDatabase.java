package com.agriportal.main;

import com.agriportal.model.dao.AdminDAO;
import com.agriportal.model.dao.FarmerDAO;
import com.agriportal.model.dao.ProductDAO;
import com.agriportal.model.dao.OrderDAO;
import com.agriportal.model.dao.PaymentDAO;

import java.sql.SQLException;

/**
 * SetupDatabase - creates all tables used in the system.
 * Run once at startup (usually in main or initialization section).
 */
public class SetupDatabase {

    public static void init() {
        try {
            System.out.println("Setting up database tables...");

            new AdminDAO().createTable();
            new FarmerDAO().createTable();
            new ProductDAO().createTable();
            new OrderDAO().createTable();

            // ✅ NEW LINE: create payments table
            new PaymentDAO().createTable();

            System.out.println("All database tables are ready.");

        } catch (SQLException e) {
            System.err.println("Database setup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init();
    }
}
