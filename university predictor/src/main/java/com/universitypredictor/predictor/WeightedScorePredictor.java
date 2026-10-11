package com.universitypredictor.predictor;

import com.universitypredictor.model.Student;
import com.universitypredictor.model.University;

/**
 * Concrete predictor implementing a balanced weighted scoring model.
 * Weights: GRE 35%, TOEFL 25%, CGPA 30%, Research Experience 10%.
 * 
 * In an interview, explain:
 * "WeightedScorePredictor demonstrates Polymorphism and Inheritance:
 * It extends BasePredictor and implements AdmissionPredictor. It applies the specific
 * weighted formula requested: 35% GRE, 25% TOEFL, 30% CGPA, and 10% Research.
 * It compares the student's weighted composite against the university's target benchmark
 * to produce a balanced, continuous admission probability."
 */
public class WeightedScorePredictor extends BasePredictor {

    // Defined business weights
    private static final double WEIGHT_GRE = 0.35;
    private static final double WEIGHT_TOEFL = 0.25;
    private static final double WEIGHT_CGPA = 0.30;
    private static final double WEIGHT_RESEARCH = 0.10;

    @Override
    public double predictChance(Student student, University university) {
        // 1. Calculate normalized student composite index (0.0 to 1.0)
        double normGre = normalizeGre(student.getGre());
        double normToefl = normalizeToefl(student.getToefl());
        double normCgpa = normalizeCgpa(student.getCgpa());
        double normResearch = student.hasResearchExperience() ? 1.0 : 0.0;

        double studentScore = (WEIGHT_GRE * normGre)
                            + (WEIGHT_TOEFL * normToefl)
                            + (WEIGHT_CGPA * normCgpa)
                            + (WEIGHT_RESEARCH * normResearch);

        // 2. Calculate university benchmark index from its minimum requirements
        double uniGre = normalizeGre(university.getMinGre());
        double uniToefl = normalizeToefl(university.getMinToefl());
        double uniCgpa = normalizeCgpa(university.getMinCgpa());
        // Assume university benchmark expects baseline student with moderate research expectation (0.5)
        double uniBenchmark = (WEIGHT_GRE * uniGre)
                            + (WEIGHT_TOEFL * uniToefl)
                            + (WEIGHT_CGPA * uniCgpa)
                            + (WEIGHT_RESEARCH * 0.40);

        // 3. Score difference translates to admission chance centered around 65% for matching benchmark
        double scoreDiff = studentScore - uniBenchmark;
        double baseChance = 65.0 + (scoreDiff * 95.0);

        // 4. Moderate bonus/penalty based on whether minimum thresholds are satisfied
        if (student.getGre() >= university.getMinGre() &&
            student.getToefl() >= university.getMinToefl() &&
            student.getCgpa() >= university.getMinCgpa()) {
            baseChance += 5.0; // Baseline qualification bonus
        }

        // Clamp final probability between 10.0% and 98.0%
        return clamp(baseChance, 10.0, 98.0);
    }

    @Override
    public String getPredictorName() {
        return "Weighted Score Predictor (GRE 35%, TOEFL 25%, CGPA 30%, Research 10%)";
    }
}
