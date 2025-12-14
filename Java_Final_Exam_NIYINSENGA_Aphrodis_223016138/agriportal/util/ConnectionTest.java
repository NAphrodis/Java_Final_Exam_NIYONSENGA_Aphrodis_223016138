package com.agriportal.util;

import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionTest {

    public static void main(String[] args) {
        System.out.println("🔍 Testing database connection...");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ SUCCESS: Connected to MySQL database!");
                System.out.println("Database Name: " + conn.getCatalog());
            } else {
                System.out.println("⚠️ Connection is null or closed.");
            }
        } catch (SQLException e) {
            System.out.println("❌ FAILED: Could not connect to the database.");
            System.out.println("Error Message: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Test completed.");
    }
}
