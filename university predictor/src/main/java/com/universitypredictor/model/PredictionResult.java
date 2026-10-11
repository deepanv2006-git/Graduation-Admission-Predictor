package com.universitypredictor.model;

/**
 * Data transfer object encapsulating the prediction result for a specific university.
 * 
 * In an interview, explain:
 * "PredictionResult aggregates the University domain entity with its calculated admission
 * probability, a diagnostic assessment of the applicant's weakest factor, and the
 * predictor algorithm utilized. It encapsulates the full recommendation item."
 */
public class PredictionResult {

    private final University university;
    private final double chancePercentage;
    private final String weakestFactorNote;
    private final String predictorType;

    public PredictionResult(University university, double chancePercentage, String weakestFactorNote, String predictorType) {
        this.university = university;
        // Keep chance percentage within 0.0% to 100.0%, rounded to 1 decimal place
        this.chancePercentage = Math.round(Math.max(0.0, Math.min(100.0, chancePercentage)) * 10.0) / 10.0;
        this.weakestFactorNote = weakestFactorNote;
        this.predictorType = predictorType;
    }

    public University getUniversity() {
        return university;
    }

    public double getChancePercentage() {
        return chancePercentage;
    }

    public String getWeakestFactorNote() {
        return weakestFactorNote;
    }

    public String getPredictorType() {
        return predictorType;
    }

    @Override
    public String toString() {
        return "PredictionResult{" +
                "university=" + university.getName() +
                ", chance=" + chancePercentage + "%" +
                ", note='" + weakestFactorNote + '\'' +
                ", predictor='" + predictorType + '\'' +
                '}';
    }
}
