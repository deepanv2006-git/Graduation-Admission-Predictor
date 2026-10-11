package com.universitypredictor.predictor;

import com.universitypredictor.model.Student;
import com.universitypredictor.model.University;

/**
 * Interface defining the contract for graduate admission chance prediction.
 * 
 * In an interview, explain:
 * "The AdmissionPredictor interface embodies the OOP Interface concept: it establishes
 * a formal contract with a single abstract method predictChance(s, u).
 * Any recommendation algorithm can be plugged into our service without changing
 * how recommendations are sorted or consumed, following the Strategy Design Pattern."
 */
public interface AdmissionPredictor {

    /**
     * Calculates the estimated probability percentage (0.0 to 100.0) that the given
     * student will be offered admission to the specified university.
     * 
     * @param student The candidate's verified profile
     * @param university The target institution and criteria
     * @return Admission probability percentage between 0.0 and 100.0
     */
    double predictChance(Student student, University university);

    /**
     * Evaluates the student's credentials against the university's criteria to identify
     * the applicant's single weakest factor and return an advisory diagnostic note.
     * 
     * @param student The candidate's verified profile
     * @param university The target institution and criteria
     * @return Plain-language explanation of the primary limiting factor
     */
    String diagnoseWeakestFactor(Student student, University university);

    /**
     * Returns the human-readable identifier of this prediction strategy.
     * 
     * @return Predictor name
     */
    String getPredictorName();
}
