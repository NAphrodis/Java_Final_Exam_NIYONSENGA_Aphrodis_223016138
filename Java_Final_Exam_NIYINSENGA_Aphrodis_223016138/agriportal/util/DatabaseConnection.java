package com.agriportal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class DatabaseConnection {
    private DatabaseConnection() {}

    // Update these values to match your MySQL instance
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://localhost:3306/agriportaldb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String DB_USER = "agriuser";     // <-- change as needed
    public static final String DB_PASSWORD = "123456";   // <-- change as needed

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC driver class not found: " + DB_DRIVER);
            e.printStackTrace();
            // still attempt DriverManager.getConnection (it may succeed if driver registered)
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
