package com.universitypredictor.repository;

import com.universitypredictor.model.PredictionRecord;
import com.universitypredictor.model.Region;
import com.universitypredictor.model.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) for persisting and retrieving prediction audit records.
 * 
 * In an interview, explain:
 * "HistoryRepository provides persistence for user prediction logs in SQLite.
 * Every time an applicant requests recommendations, the inputs, preferred regions,
 * and top recommendation are saved using JDBC PreparedStatements for full auditability."
 */
public class HistoryRepository {

    private final DatabaseManager databaseManager;

    public HistoryRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Persists a completed admission prediction into the SQLite database.
     * 
     * @param student The candidate's inputs
     * @param topResult Summary string of the top-ranked recommendation
     */
    public void savePrediction(Student student, String topResult) {
        String sql = "INSERT INTO predictions (gre, toefl, cgpa, research, regions, top_result) VALUES (?, ?, ?, ?, ?, ?)";

        String regionListStr = student.getPreferredRegions().stream()
                .map(Region::getDisplayName)
                .collect(Collectors.joining(", "));

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, student.getGre());
            pstmt.setInt(2, student.getToefl());
            pstmt.setDouble(3, student.getCgpa());
            pstmt.setInt(4, student.hasResearchExperience() ? 1 : 0);
            pstmt.setString(5, regionListStr);
            pstmt.setString(6, topResult);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[HistoryRepository] Error saving prediction: " + e.getMessage());
        }
    }

    /**
     * Retrieves the most recent prediction history records, up to the given limit (e.g. 10).
     * 
     * @param limit Maximum number of records to retrieve
     * @return List of recent PredictionRecord instances
     */
    public List<PredictionRecord> findRecent(int limit) {
        String sql = "SELECT id, gre, toefl, cgpa, research, regions, top_result, created_at " +
                     "FROM predictions ORDER BY id DESC LIMIT ?";
        List<PredictionRecord> records = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int gre = rs.getInt("gre");
                    int toefl = rs.getInt("toefl");
                    double cgpa = rs.getDouble("cgpa");
                    boolean research = rs.getInt("research") == 1;
                    String regions = rs.getString("regions");
                    String topResult = rs.getString("top_result");
                    String createdAt = rs.getString("created_at");

                    records.add(new PredictionRecord(id, gre, toefl, cgpa, research, regions, topResult, createdAt));
                }
            }
        } catch (SQLException e) {
            System.err.println("[HistoryRepository] Error in findRecent: " + e.getMessage());
        }
        return records;
    }
}
