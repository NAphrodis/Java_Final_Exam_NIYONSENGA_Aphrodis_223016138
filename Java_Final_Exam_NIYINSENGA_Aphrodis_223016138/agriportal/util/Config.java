package com.agriportal.util;

/**
 * Application configuration constants.
 * Change these values to match your environment before running the application.
 */
public final class Config {

    private Config() {}

    // JDBC driver class for MySQL (keep on classpath)
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Database connection (edit DB name, host, port, user and password)
    public static final String DB_URL = "jdbc:mysql://localhost:3306/agriportaldb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String DB_USER = "agriuser";
    public static final String DB_PASSWORD = "change_me";

    // Application metadata
    public static final String APP_NAME = "AgriPortal";
    public static final String APP_VERSION = "1.0.0";

    // Common defaults
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final int DEFAULT_PAGE_SIZE = 20;
}
