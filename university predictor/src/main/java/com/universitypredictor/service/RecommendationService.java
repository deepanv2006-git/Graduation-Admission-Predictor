package com.universitypredictor.service;

import com.universitypredictor.model.PredictionRecord;
import com.universitypredictor.model.PredictionResult;
import com.universitypredictor.model.Student;
import com.universitypredictor.model.University;
import com.universitypredictor.predictor.AdmissionPredictor;
import com.universitypredictor.predictor.StrictPredictor;
import com.universitypredictor.predictor.WeightedScorePredictor;
import com.universitypredictor.repository.HistoryRepository;
import com.universitypredictor.repository.UniversityRepository;
import java.util.Comparator;
import java.util.List;

/**
 * Core business service orchestrating admission matching, prediction calculations,
 * and result ranking using Java 17+ Stream pipelines.
 * 
 * In an interview, explain:
 * "RecommendationService coordinates business logic separate from HTTP and Database concerns.
 * It uses the Strategy Pattern via AdmissionPredictor (polymorphism), streams and lambdas
 * for functional data transformation, and logs audit entries in HistoryRepository.
 * Java Streams elegantly handle filtering, mapping, sorting, and pagination in a single readable pipeline."
 */
public class RecommendationService {

    private final UniversityRepository universityRepository;
    private final HistoryRepository historyRepository;
    private final AdmissionPredictor weightedPredictor;
    private final AdmissionPredictor strictPredictor;

    public RecommendationService(UniversityRepository universityRepository, HistoryRepository historyRepository) {
        this.universityRepository = universityRepository;
        this.historyRepository = historyRepository;
        this.weightedPredictor = new WeightedScorePredictor();
        this.strictPredictor = new StrictPredictor();
    }

    /**
     * Recommends the top N universities for a candidate within their preferred regions.
     * 
     * @param student The candidate's validated profile
     * @param predictorChoice "weighted" or "strict" algorithm selector
     * @param topN Number of top matches to return (e.g. 5)
     * @return Ranked list of PredictionResult objects
     */
    public List<PredictionResult> getTopRecommendations(Student student, String predictorChoice, int topN) {
        // 1. Polymorphic selection of predictor algorithm (Strategy Pattern)
        AdmissionPredictor activePredictor = resolvePredictor(predictorChoice);

        // 2. Fetch candidate universities belonging to chosen regions
        List<University> candidateUnis = universityRepository.findByRegions(student.getPreferredRegions());

        // 3. Process candidate universities through Java Stream pipeline
        List<PredictionResult> topResults = candidateUnis.stream()
                .map(uni -> {
                    double chance = activePredictor.predictChance(student, uni);
                    String weakestNote = activePredictor.diagnoseWeakestFactor(student, uni);
                    return new PredictionResult(uni, chance, weakestNote, activePredictor.getPredictorName());
                })
                .sorted(Comparator.comparingDouble(PredictionResult::getChancePercentage).reversed())
                .limit(topN)
                .toList();

        // 4. Record this prediction query in persistent history if results were generated
        if (!topResults.isEmpty()) {
            PredictionResult best = topResults.get(0);
            String topSummary = String.format("%s (%.1f%% chance)", best.getUniversity().getName(), best.getChancePercentage());
            historyRepository.savePrediction(student, topSummary);
        }

        return topResults;
    }

    /**
     * Resolves the concrete predictor strategy based on user selection.
     */
    public AdmissionPredictor resolvePredictor(String predictorChoice) {
        if ("strict".equalsIgnoreCase(predictorChoice)) {
            return strictPredictor;
        }
        return weightedPredictor;
    }

    /**
     * Retrieves the latest prediction history entries.
     */
    public List<PredictionRecord> getRecentHistory(int limit) {
        return historyRepository.findRecent(limit);
    }

    /**
     * Retrieves all available universities in the system.
     */
    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }
}
