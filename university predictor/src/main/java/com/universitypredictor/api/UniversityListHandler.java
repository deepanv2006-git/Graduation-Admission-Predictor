package com.universitypredictor.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.universitypredictor.model.University;
import com.universitypredictor.service.RecommendationService;
import java.io.IOException;
import java.util.List;

/**
 * HTTP Handler for retrieving the full catalog of seeded European universities.
 * 
 * In an interview, explain:
 * "UniversityListHandler provides GET /api/universities, allowing clients to inspect
 * all institutions, minimum benchmarks, and country locations."
 */
public class UniversityListHandler implements HttpHandler {

    private final RecommendationService recommendationService;
    private final Gson gson;

    public UniversityListHandler(RecommendationService recommendationService) {
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
            List<University> list = recommendationService.getAllUniversities();
            String jsonOutput = gson.toJson(list);
            HttpUtils.sendJsonResponse(exchange, 200, jsonOutput);
        } catch (Exception e) {
            System.err.println("[UniversityListHandler] Error: " + e.getMessage());
            HttpUtils.sendErrorResponse(exchange, 500, "Unable to retrieve universities: " + e.getMessage());
        }
    }
}
