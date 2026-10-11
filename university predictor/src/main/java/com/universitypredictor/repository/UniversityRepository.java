package com.universitypredictor.repository;

import com.universitypredictor.model.Region;
import com.universitypredictor.model.University;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Data Access Object (DAO) for retrieving University entities from the SQLite database.
 * 
 * In an interview, explain:
 * "UniversityRepository encapsulates all raw SQL queries related to universities.
 * It uses Java Database Connectivity (JDBC) with PreparedStatement to prevent SQL injection
 * and try-with-resources to ensure database connections and statements are reliably closed."
 */
public class UniversityRepository {

    private final DatabaseManager databaseManager;

    public UniversityRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Retrieves all universities currently stored in the database.
     * 
     * @return List of all University records
     */
    public List<University> findAll() {
        String sql = "SELECT id, name, country, region, min_gre, min_toefl, min_cgpa FROM universities ORDER BY name ASC";
        List<University> list = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToUniversity(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UniversityRepository] Error in findAll: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves universities belonging to any of the specified European regions.
     * 
     * @param regions Set of selected Region enums
     * @return List of matching University records
     */
    public List<University> findByRegions(Set<Region> regions) {
        if (regions == null || regions.isEmpty()) {
            return Collections.emptyList();
        }

        // Dynamically build the parameterized SQL query with IN (?, ?, ...)
        StringBuilder sql = new StringBuilder("SELECT id, name, country, region, min_gre, min_toefl, min_cgpa ")
                .append("FROM universities WHERE region IN (");
        for (int i = 0; i < regions.size(); i++) {
            sql.append(i > 0 ? ", ?" : "?");
        }
        sql.append(") ORDER BY min_gre DESC");

        List<University> list = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            for (Region region : regions) {
                pstmt.setString(paramIndex++, region.getDisplayName());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUniversity(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[UniversityRepository] Error in findByRegions: " + e.getMessage());
        }
        return list;
    }

    /**
     * Maps a single SQL ResultSet row to a University domain instance.
     */
    private University mapRowToUniversity(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String country = rs.getString("country");
        String regionStr = rs.getString("region");
        int minGre = rs.getInt("min_gre");
        int minToefl = rs.getInt("min_toefl");
        double minCgpa = rs.getDouble("min_cgpa");

        Region region = Region.fromString(regionStr);
        return new University(id, name, country, region, minGre, minToefl, minCgpa);
    }
}
