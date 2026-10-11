package com.universitypredictor.predictor;

import com.universitypredictor.model.Student;
import com.universitypredictor.model.University;

/**
 * Concrete predictor implementing a strict cutoff-enforcing evaluation model.
 * 
 * In an interview, explain:
 * "StrictPredictor demonstrates Polymorphism:
 * It implements the same predictChance(Student, University) method from AdmissionPredictor,
 * but with radically different business rules. It mimics stringent admission committees
 * that enforce hard cutoffs: any deficit below university minimums incurs severe penalties,
 * sharply depressing the candidate's admission probability."
 */
public class StrictPredictor extends BasePredictor {

    @Override
    public double predictChance(Student student, University university) {
        // Start from a standard weighted foundation
        double normGre = normalizeGre(student.getGre());
        double normToefl = normalizeToefl(student.getToefl());
        double normCgpa = normalizeCgpa(student.getCgpa());
        double normResearch = student.hasResearchExperience() ? 1.0 : 0.0;

        double studentScore = (0.35 * normGre) + (0.25 * normToefl) + (0.30 * normCgpa) + (0.10 * normResearch);

        double uniGre = normalizeGre(university.getMinGre());
        double uniToefl = normalizeToefl(university.getMinToefl());
        double uniCgpa = normalizeCgpa(university.getMinCgpa());
        double uniBenchmark = (0.35 * uniGre) + (0.25 * uniToefl) + (0.30 * uniCgpa) + 0.04;

        double baseChance = 60.0 + ((studentScore - uniBenchmark) * 85.0);

        // Strict penalty logic: enforce institutional minimums with heavy deductions
        double penalty = 0.0;

        if (student.getGre() < university.getMinGre()) {
            int greDeficit = university.getMinGre() - student.getGre();
            penalty += 15.0 + (greDeficit * 1.5);
        }

        if (student.getToefl() < university.getMinToefl()) {
            int toeflDeficit = university.getMinToefl() - student.getToefl();
            penalty += 15.0 + (toeflDeficit * 2.0);
        }

        if (student.getCgpa() < university.getMinCgpa()) {
            double cgpaDeficit = university.getMinCgpa() - student.getCgpa();
            penalty += 20.0 + (cgpaDeficit * 18.0);
        }

        // Additional penalty if top research institute requires research experience
        if (!student.hasResearchExperience() && university.getMinGre() >= 318) {
            penalty += 8.0;
        }

        // Apply strict penalties or full-compliance bonus
        if (penalty > 0.0) {
            baseChance -= penalty;
        } else {
            // Full compliance bonus: student successfully cleared all minimum criteria
            baseChance += 8.0;
        }

        // Clamp final probability between 5.0% and 99.0%
        return clamp(baseChance, 5.0, 99.0);
    }

    @Override
    public String getPredictorName() {
        return "Strict Predictor (Penalises Scores Below Minimum)";
    }
}
