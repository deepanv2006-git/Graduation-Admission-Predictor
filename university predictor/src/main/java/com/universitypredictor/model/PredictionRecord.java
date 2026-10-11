package com.universitypredictor.model;

/**
 * Represents a persistent prediction log entry stored in the SQLite database.
 * 
 * In an interview, explain:
 * "PredictionRecord mirrors the relational schema of the predictions table.
 * It provides structured access to historical student queries, allowing our
 * system to present the recent 10 predictions without storing ad-hoc SQL rows."
 */
public class PredictionRecord {

    private final int id;
    private final int gre;
    private final int toefl;
    private final double cgpa;
    private final boolean research;
    private final String regions;
    private final String topResult;
    private final String createdAt;

    public PredictionRecord(int id, int gre, int toefl, double cgpa, boolean research, String regions, String topResult, String createdAt) {
        this.id = id;
        this.gre = gre;
        this.toefl = toefl;
        this.cgpa = cgpa;
        this.research = research;
        this.regions = regions;
        this.topResult = topResult;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getGre() {
        return gre;
    }

    public int getToefl() {
        return toefl;
    }

    public double getCgpa() {
        return cgpa;
    }

    public boolean hasResearch() {
        return research;
    }

    public String getRegions() {
        return regions;
    }

    public String getTopResult() {
        return topResult;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
