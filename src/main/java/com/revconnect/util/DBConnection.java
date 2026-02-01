package com.revconnect.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "sandy";
    private static final String PASSWORD = "sandy143";

    private DBConnection() {} // prevent instantiation

    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to Oracle DB: " + e.getMessage());
            return null;
        }
    }
}
