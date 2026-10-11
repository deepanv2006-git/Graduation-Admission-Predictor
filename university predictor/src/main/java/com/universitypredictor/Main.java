package com.universitypredictor;

import com.universitypredictor.api.AppServer;
import com.universitypredictor.repository.DatabaseManager;
import com.universitypredictor.repository.HistoryRepository;
import com.universitypredictor.repository.UniversityRepository;
import com.universitypredictor.service.RecommendationService;

/**
 * Main application entry point for the Graduation Admission and Regional Preference Predictor System.
 * 
 * In an interview, explain:
 * "The Main class is the composition root of the application. It wires together
 * the layers using Dependency Injection without requiring an inversion-of-control
 * container framework:
 * DatabaseManager -> Repositories -> RecommendationService -> AppServer -> HttpServer."
 */
public class Main {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Starting Graduation Admission and Regional Preference Predictor...");

        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid port parameter provided, defaulting to " + DEFAULT_PORT);
            }
        }

        try {
            // 1. Initialize SQLite Database (runs schema.sql and seed.sql if needed)
            DatabaseManager databaseManager = new DatabaseManager();
            databaseManager.initializeDatabase();

            // 2. Wire Repositories (Data Access Layer)
            UniversityRepository universityRepository = new UniversityRepository(databaseManager);
            HistoryRepository historyRepository = new HistoryRepository(databaseManager);

            // 3. Wire Service (Business Logic Layer)
            RecommendationService recommendationService = new RecommendationService(universityRepository, historyRepository);

            // 4. Start HTTP Server (Presentation / API Layer)
            AppServer appServer = new AppServer(port, recommendationService);
            appServer.start();

            // 5. Add JVM Shutdown Hook for clean lifecycle termination
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down application server...");
                appServer.stop();
            }));

        } catch (Exception e) {
            System.err.println("Fatal failure starting application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
