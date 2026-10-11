package com.universitypredictor.api;

import com.sun.net.httpserver.HttpServer;
import com.universitypredictor.service.RecommendationService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Server wrapper configuring Java's built-in HttpServer and route handlers.
 * 
 * In an interview, explain:
 * "AppServer creates and configures the native com.sun.net.httpserver.HttpServer.
 * By mapping individual URL paths to dedicated HttpHandler implementations and attaching
 * a multi-threaded Executor, we achieve high throughput without relying on Spring Boot
 * or third-party servlet containers."
 */
public class AppServer {

    private final int port;
    private final RecommendationService recommendationService;
    private HttpServer server;

    public AppServer(int port, RecommendationService recommendationService) {
        this.port = port;
        this.recommendationService = recommendationService;
    }

    /**
     * Boots the HTTP server and binds routing endpoints.
     */
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Register API endpoints
        server.createContext("/api/predict", new PredictionHandler(recommendationService));
        server.createContext("/api/history", new HistoryHandler(recommendationService));
        server.createContext("/api/universities", new UniversityListHandler(recommendationService));

        // Register static file server for frontend assets at root
        server.createContext("/", new StaticFileHandler());

        // Allocate a cached thread pool to handle concurrent requests asynchronously
        server.setExecutor(Executors.newCachedThreadPool());

        server.start();
        System.out.println("========================================================================");
        System.out.println(" Graduation Admission & Regional Preference Predictor Server is ACTIVE!");
        System.out.println(" Local Web Application URL: http://localhost:" + port);
        System.out.println(" API Endpoints:");
        System.out.println("   - POST http://localhost:" + port + "/api/predict");
        System.out.println("   - GET  http://localhost:" + port + "/api/history");
        System.out.println("   - GET  http://localhost:" + port + "/api/universities");
        System.out.println("========================================================================");
    }

    /**
     * Gracefully stops the server.
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("[AppServer] Server stopped gracefully.");
        }
    }
}
