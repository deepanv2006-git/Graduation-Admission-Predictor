package com.universitypredictor.api;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility helper methods for HTTP exchange responses, headers, and encoding.
 */
public class HttpUtils {

    /**
     * Sends a JSON response with specified HTTP status code and CORS headers.
     */
    public static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Handles CORS preflight OPTIONS requests.
     */
    public static boolean handleOptionsRequest(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    /**
     * Sends a structured JSON error response.
     */
    public static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        String safeMsg = message == null ? "Unknown server error" : message.replace("\"", "\\\"");
        String json = String.format("{\"error\":\"%s\"}", safeMsg);
        sendJsonResponse(exchange, statusCode, json);
    }
}
