package com.universitypredictor.predictor;

import com.universitypredictor.model.Student;
import com.universitypredictor.model.University;

/**
 * Abstract base class providing common calculation logic, normalization utilities,
 * and diagnostic heuristics for all admission predictors.
 * 
 * In an interview, explain:
 * "BasePredictor demonstrates Abstraction and Inheritance:
 * 1. Abstraction: It hides the mathematical complexity of normalizing raw test scales
 *    (GRE 260-340, TOEFL 0-120, CGPA 0-10) into standardized 0.0-1.0 ratios.
 * 2. Inheritance: Subclasses (WeightedScorePredictor and StrictPredictor) inherit these
 *    reusable normalization utilities and default diagnostic logic, eliminating code duplication."
 */
public abstract class BasePredictor implements AdmissionPredictor {

    /**
     * Normalizes a GRE score (range 260 to 340) to a 0.0 - 1.0 floating point scale.
     */
    protected double normalizeGre(int gre) {
        return (gre - 260.0) / (340.0 - 260.0);
    }

    /**
     * Normalizes a TOEFL score (range 0 to 120) to a 0.0 - 1.0 floating point scale.
     */
    protected double normalizeToefl(int toefl) {
        return toefl / 120.0;
    }

    /**
     * Normalizes a CGPA (range 0.0 to 10.0) to a 0.0 - 1.0 floating point scale.
     */
    protected double normalizeCgpa(double cgpa) {
        return cgpa / 10.0;
    }

    /**
     * Clamps a double value between specified lower and upper bounds.
     */
    protected double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Common diagnostic algorithm that analyzes which credential is the student's
     * primary limiting factor relative to this specific university.
     * 
     * @param student The candidate's verified profile
     * @param university The target institution and criteria
     * @return Advisory diagnostic explanation
     */
    @Override
    public String diagnoseWeakestFactor(Student student, University university) {
        // Calculate relative differences compared to minimum institutional benchmarks
        double greGap = (double) (student.getGre() - university.getMinGre()) / (340.0 - 260.0);
        double toeflGap = (double) (student.getToefl() - university.getMinToefl()) / 120.0;
        double cgpaGap = (student.getCgpa() - university.getMinCgpa()) / 10.0;

        // Check if any score is strictly below university benchmark
        boolean greDeficit = student.getGre() < university.getMinGre();
        boolean toeflDeficit = student.getToefl() < university.getMinToefl();
        boolean cgpaDeficit = student.getCgpa() < university.getMinCgpa();

        if (greDeficit || toeflDeficit || cgpaDeficit) {
            // Find the most severe deficit
            if (cgpaGap <= greGap && cgpaGap <= toeflGap) {
                return String.format("CGPA (%.2f / 10) falls below university benchmark (%.2f).", 
                        student.getCgpa(), university.getMinCgpa());
            } else if (greGap <= toeflGap) {
                return String.format("GRE score (%d / 340) is below minimum expectation (%d).", 
                        student.getGre(), university.getMinGre());
            } else {
                return String.format("TOEFL score (%d / 120) falls short of English language requirement (%d).", 
                        student.getToefl(), university.getMinToefl());
            }
        }

        // If all exam scores meet criteria, evaluate research experience
        if (!student.hasResearchExperience()) {
            if (university.getMinGre() >= 318 || university.getMinCgpa() >= 8.3) {
                return "Lack of research publications: highly competitive European research faculties heavily favor lab experience.";
            } else {
                return "No research experience: baseline test scores meet requirements, but research would strengthen application.";
            }
        }

        // If everything is above minimum and research is present, find the lowest surplus
        if (cgpaGap <= greGap && cgpaGap <= toeflGap) {
            return String.format("Profile is competitive! CGPA (%.2f) is closest to the cutoff (%.2f).", 
                    student.getCgpa(), university.getMinCgpa());
        } else if (greGap <= toeflGap) {
            return String.format("Profile is competitive! GRE (%d) is closest to the cutoff (%d).", 
                    student.getGre(), university.getMinGre());
        } else {
            return String.format("Profile is competitive! TOEFL (%d) is closest to the cutoff (%d).", 
                    student.getToefl(), university.getMinToefl());
        }
    }
}
