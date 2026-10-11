package com.universitypredictor.repository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages SQLite database connectivity, schema bootstrapping, and seed data initialization.
 * 
 * In an interview, explain:
 * "DatabaseManager provides centralized database lifecycle management. On application startup,
 * it verifies if tables exist. If empty or absent, it automatically executes schema.sql and seed.sql
 * from the classpath or filesystem, guaranteeing zero manual configuration for the user."
 */
public class DatabaseManager {

    private static final String DEFAULT_DB_URL = "jdbc:sqlite:university_predictor.db";
    private final String dbUrl;

    public DatabaseManager() {
        this(DEFAULT_DB_URL);
    }

    public DatabaseManager(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * Obtains a new JDBC Connection to the SQLite database.
     * 
     * @return Open SQL Connection
     * @throws SQLException If database connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Initializes the database schema and seeds initial universities if table is empty.
     */
    public void initializeDatabase() {
        try (Connection conn = getConnection()) {
            // 1. Create tables from schema.sql
            executeSqlScript(conn, "database/schema.sql");

            // 2. Check if universities table has records
            boolean hasData = false;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM universities")) {
                if (rs.next()) {
                    hasData = rs.getInt(1) > 0;
                }
            } catch (SQLException ignored) {
                // Table might not exist yet before schema run
            }

            // 3. If no universities present, seed sample data
            if (!hasData) {
                System.out.println("[DatabaseManager] Seeding sample European universities into database...");
                executeSqlScript(conn, "database/seed.sql");
                System.out.println("[DatabaseManager] Seed data inserted successfully.");
            } else {
                System.out.println("[DatabaseManager] Database already initialized with university records.");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseManager] Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads a SQL script file from classpath or filesystem and executes statements sequentially.
     */
    private void executeSqlScript(Connection conn, String scriptPath) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(scriptPath);
        if (is == null) {
            // Fallback to direct file loading if running from project root
            java.io.File file = new java.io.File(scriptPath);
            if (file.exists()) {
                is = new java.io.FileInputStream(file);
            } else {
                System.err.println("[DatabaseManager] Could not locate SQL script: " + scriptPath);
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             Statement stmt = conn.createStatement()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sb.append(line).append(" ");
                if (line.endsWith(";")) {
                    String sql = sb.toString().trim();
                    if (!sql.isEmpty()) {
                        stmt.execute(sql);
                    }
                    sb.setLength(0);
                }
            }
        }
    }
}
