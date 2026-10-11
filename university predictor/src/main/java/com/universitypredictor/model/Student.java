package com.universitypredictor.model;

import com.universitypredictor.exception.InvalidInputException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates the academic profile, test scores, and regional preferences of an applicant.
 * 
 * In an interview, explain:
 * "The Student class exemplifies Encapsulation: all fields are declared private to
 * prevent arbitrary external modification. Data integrity is strictly enforced at
 * the boundary inside the constructor and setters by validating that scores fall
 * within valid official standard ranges (GRE: 260-340, TOEFL: 0-120, CGPA: 0.0-10.0)."
 */
public class Student {

    // Private member variables - hidden from direct external manipulation
    private int gre;
    private int toefl;
    private double cgpa;
    private boolean researchExperience;
    private Set<Region> preferredRegions;

    /**
     * Constructs a fully-validated Student object.
     * 
     * @param gre GRE General Test score (260 - 340)
     * @param toefl TOEFL iBT score (0 - 120)
     * @param cgpa Cumulative Grade Point Average on a 10.0 scale (0.0 - 10.0)
     * @param researchExperience true if applicant has prior published papers or research lab experience
     * @param preferredRegions Set of European regions selected by applicant
     * @throws InvalidInputException if any score or selection fails domain validation
     */
    public Student(int gre, int toefl, double cgpa, boolean researchExperience, Set<Region> preferredRegions) {
        setGre(gre);
        setToefl(toefl);
        setCgpa(cgpa);
        this.researchExperience = researchExperience;
        setPreferredRegions(preferredRegions);
    }

    /**
     * Convenience constructor accepting a List of Region enums.
     */
    public Student(int gre, int toefl, double cgpa, boolean researchExperience, List<Region> preferredRegions) {
        this(gre, toefl, cgpa, researchExperience, preferredRegions == null ? null : new HashSet<>(preferredRegions));
    }

    // --- Validation and Mutator Methods ---

    public void setGre(int gre) {
        if (gre < 260 || gre > 340) {
            throw new InvalidInputException("GRE score must be between 260 and 340. Provided: " + gre);
        }
        this.gre = gre;
    }

    public void setToefl(int toefl) {
        if (toefl < 0 || toefl > 120) {
            throw new InvalidInputException("TOEFL score must be between 0 and 120. Provided: " + toefl);
        }
        this.toefl = toefl;
    }

    public void setCgpa(double cgpa) {
        if (Double.isNaN(cgpa) || cgpa < 0.0 || cgpa > 10.0) {
            throw new InvalidInputException("CGPA must be between 0.0 and 10.0. Provided: " + cgpa);
        }
        // Round to 2 decimal places to avoid floating-point artifacts
        this.cgpa = Math.round(cgpa * 100.0) / 100.0;
    }

    public void setResearchExperience(boolean researchExperience) {
        this.researchExperience = researchExperience;
    }

    public void setPreferredRegions(Set<Region> preferredRegions) {
        if (preferredRegions == null || preferredRegions.isEmpty()) {
            throw new InvalidInputException("At least one European region preference must be selected.");
        }
        this.preferredRegions = Collections.unmodifiableSet(new HashSet<>(preferredRegions));
    }

    // --- Accessor (Getter) Methods ---

    public int getGre() {
        return gre;
    }

    public int getToefl() {
        return toefl;
    }

    public double getCgpa() {
        return cgpa;
    }

    public boolean hasResearchExperience() {
        return researchExperience;
    }

    public Set<Region> getPreferredRegions() {
        return preferredRegions;
    }

    @Override
    public String toString() {
        return "Student{" +
                "gre=" + gre +
                ", toefl=" + toefl +
                ", cgpa=" + cgpa +
                ", researchExperience=" + researchExperience +
                ", preferredRegions=" + preferredRegions +
                '}';
    }
}
