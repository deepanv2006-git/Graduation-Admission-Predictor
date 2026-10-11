-- ====================================================================
-- Database Schema for Graduation Admission and Regional Preference Predictor
-- SQLite 3 Database Definition
-- ====================================================================

-- 1. Universities table: Stores master data for European universities
CREATE TABLE IF NOT EXISTS universities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    country TEXT NOT NULL,
    region TEXT NOT NULL,
    min_gre INTEGER NOT NULL,
    min_toefl INTEGER NOT NULL,
    min_cgpa REAL NOT NULL
);

-- 2. Predictions table: Stores prediction history for audit and analytics
CREATE TABLE IF NOT EXISTS predictions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    gre INTEGER NOT NULL,
    toefl INTEGER NOT NULL,
    cgpa REAL NOT NULL,
    research INTEGER NOT NULL,
    regions TEXT NOT NULL,
    top_result TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
