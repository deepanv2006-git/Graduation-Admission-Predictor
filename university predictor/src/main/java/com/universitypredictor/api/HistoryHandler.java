package com.universitypredictor.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.universitypredictor.model.PredictionRecord;
import com.universitypredictor.service.RecommendationService;
import java.io.IOException;
import java.util.List;

/**
 * HTTP Handler for retrieving the last 10 prediction history records.
 * 
 * In an interview, explain:
 * "HistoryHandler exposes GET /api/history to provide the UI with an audit trail
 * of previous predictions directly from the SQLite database."
 */
public class HistoryHandler implements HttpHandler {

    private final RecommendationService recommendationService;
    private final Gson gson;

    public HistoryHandler(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtils.handleOptionsRequest(exchange)) {
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtils.sendErrorResponse(exchange, 405, "Method Not Allowed. Use GET.");
            return;
        }

        try {
            List<PredictionRecord> history = recommendationService.getRecentHistory(10);
            String jsonOutput = gson.toJson(history);
            HttpUtils.sendJsonResponse(exchange, 200, jsonOutput);
        } catch (Exception e) {
            System.err.println("[HistoryHandler] Error fetching history: " + e.getMessage());
            HttpUtils.sendErrorResponse(exchange, 500, "Unable to retrieve prediction history: " + e.getMessage());
        }
    }
}
