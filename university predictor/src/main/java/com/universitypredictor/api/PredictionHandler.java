package com.universitypredictor.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.universitypredictor.exception.InvalidInputException;
import com.universitypredictor.model.PredictionResult;
import com.universitypredictor.model.Region;
import com.universitypredictor.model.Student;
import com.universitypredictor.service.RecommendationService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * HTTP Handler for processing student admission prediction requests.
 * 
 * In an interview, explain:
 * "PredictionHandler isolates web protocol communication from domain logic.
 * It reads the raw HTTP request payload, parses the JSON using Gson, invokes
 * domain validation on the Student model (catching InvalidInputException for clean 400 errors),
 * and delegates execution to the RecommendationService."
 */
public class PredictionHandler implements HttpHandler {

    private final RecommendationService recommendationService;
    private final Gson gson;

    public PredictionHandler(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtils.handleOptionsRequest(exchange)) {
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtils.sendErrorResponse(exchange, 405, "Method Not Allowed. Use POST.");
            return;
        }

        try {
            // 1. Read JSON request body
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }

            if (body.length() == 0) {
                throw new InvalidInputException("Request body cannot be empty.");
            }

            // 2. Parse JSON
            JsonObject json = JsonParser.parseString(body.toString()).getAsJsonObject();

            if (!json.has("gre") || !json.has("toefl") || !json.has("cgpa")) {
                throw new InvalidInputException("Missing required fields: gre, toefl, cgpa.");
            }

            int gre = json.get("gre").getAsInt();
            int toefl = json.get("toefl").getAsInt();
            double cgpa = json.get("cgpa").getAsDouble();
            boolean research = json.has("research") && json.get("research").getAsBoolean();
            String predictorChoice = json.has("predictor") ? json.get("predictor").getAsString() : "weighted";

            // 3. Parse preferred regions
            Set<Region> preferredRegions = new HashSet<>();
            if (json.has("regions") && json.get("regions").isJsonArray()) {
                for (JsonElement el : json.getAsJsonArray("regions")) {
                    preferredRegions.add(Region.fromString(el.getAsString()));
                }
            } else {
                throw new InvalidInputException("Please select at least one European region.");
            }

            // 4. Construct domain Student entity (triggers domain encapsulation & validation)
            Student student = new Student(gre, toefl, cgpa, research, preferredRegions);

            // 5. Execute recommendation pipeline
            List<PredictionResult> results = recommendationService.getTopRecommendations(student, predictorChoice, 5);

            // 6. Return response
            String jsonOutput = gson.toJson(results);
            HttpUtils.sendJsonResponse(exchange, 200, jsonOutput);

        } catch (InvalidInputException e) {
            HttpUtils.sendErrorResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            System.err.println("[PredictionHandler] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            HttpUtils.sendErrorResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }
}
