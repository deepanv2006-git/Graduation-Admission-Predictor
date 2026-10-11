# Graduation Admission and Regional Preference Predictor

A web application that estimates admission chances for European universities based on applicant test scores and regional preferences.

## What It Does

1. **Student Input:** Enter GRE (260–340), TOEFL (0–120), CGPA (0.0–10.0), research experience (yes/no), and select one or more European regions (Western, Northern, Central, Southern, Eastern Europe).
2. **Prediction Models:**
   - **Weighted Score:** Evaluates scores using weights (GRE 35%, TOEFL 25%, CGPA 30%, Research 10%).
   - **Strict Minimums:** Applies penalties when scores do not meet university minimum cutoffs.
3. **Recommendations:** Computes admission chances across matching regional universities and displays the top 5 matches with a progress bar and a note on the applicant's weakest factor.
4. **History:** Saves each prediction into a SQLite database and displays the last 10 entries with an option to re-run past inputs.

## Requirements

- Java 17 or higher
- Maven 3.6+

## How to Build and Run

1. **Build the project:**
   ```bash
   mvn clean package
   ```
   Or on Windows using the included wrapper:
   ```cmd
   .\mvnw.cmd clean package
   ```

2. **Run the application:**
   ```bash
   java -jar target/university-predictor-1.0.0-jar-with-dependencies.jar
   ```
   Or directly via Maven:
   ```bash
   mvn exec:java
   ```

3. **Open the browser:**
   Navigate to:
   ```
   http://localhost:8080
   ```

*Note: The database (`university_predictor.db`) is automatically initialized and seeded with sample university data from `database/schema.sql` and `database/seed.sql` on first run.*

## Project Structure

```
university-predictor/
├── pom.xml
├── README.md
├── database/
│   ├── schema.sql
│   └── seed.sql
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── universitypredictor/
        │           ├── Main.java
        │           ├── api/
        │           │   ├── AppServer.java
        │           │   ├── HttpUtils.java
        │           │   ├── PredictionHandler.java
        │           │   ├── HistoryHandler.java
        │           │   ├── UniversityListHandler.java
        │           │   └── StaticFileHandler.java
        │           ├── exception/
        │           │   └── InvalidInputException.java
        │           ├── model/
        │           │   ├── Region.java
        │           │   ├── Student.java
        │           │   ├── University.java
        │           │   ├── PredictionRecord.java
        │           │   └── PredictionResult.java
        │           ├── predictor/
        │           │   ├── AdmissionPredictor.java
        │           │   ├── BasePredictor.java
        │           │   ├── WeightedScorePredictor.java
        │           │   └── StrictPredictor.java
        │           ├── repository/
        │           │   ├── DatabaseManager.java
        │           │   ├── UniversityRepository.java
        │           │   └── HistoryRepository.java
        │           └── service/
        │               └── RecommendationService.java
        └── resources/
            ├── database/
            │   ├── schema.sql
            │   └── seed.sql
            └── static/
                ├── index.html
                ├── css/
                │   └── style.css
                └── js/
                    └── app.js
```
