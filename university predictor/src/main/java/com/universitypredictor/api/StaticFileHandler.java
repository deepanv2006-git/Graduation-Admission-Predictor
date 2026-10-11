package com.universitypredictor.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Built-in static file server for serving HTML, CSS, JavaScript, and asset files.
 * 
 * In an interview, explain:
 * "Since we are not allowed to use Spring Boot or other heavy web frameworks,
 * StaticFileHandler implements simple, secure static file delivery using Java's
 * HttpServer. It streams resources from either the JAR classpath or local file directory,
 * inspecting file extensions to apply correct MIME Content-Type headers."
 */
public class StaticFileHandler implements HttpHandler {

    private static final String CLASSPATH_PREFIX = "static";
    private static final String FILESYSTEM_PREFIX = "src/main/resources/static";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()) && !"HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtils.sendErrorResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath();

        // Default root path to index.html
        if (path == null || path.equals("/") || path.isEmpty()) {
            path = "/index.html";
        }

        // Security check: prevent path traversal attacks
        if (path.contains("..")) {
            HttpUtils.sendErrorResponse(exchange, 403, "Access Denied");
            return;
        }

        byte[] content = loadFileBytes(path);
        if (content == null) {
            String notFound = "<html><body><h1>404 Not Found</h1><p>Resource " + path + " was not found.</p></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(404, notFound.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFound.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }

        String mimeType = determineMimeType(path);
        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(200, content.length);

        if (!"HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        }
    }

    /**
     * Attempts to read file bytes from classpath first, then filesystem fallback.
     */
    private byte[] loadFileBytes(String rawPath) {
        String cleanPath = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;

        // 1. Try loading from classpath resources
        String resourcePath = CLASSPATH_PREFIX + "/" + cleanPath;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                return readAllBytes(is);
            }
        } catch (IOException ignored) {
        }

        // 2. Try loading from file system
        File fsFile = new File(FILESYSTEM_PREFIX + "/" + cleanPath);
        if (fsFile.exists() && fsFile.isFile()) {
            try (InputStream is = new FileInputStream(fsFile)) {
                return readAllBytes(is);
            } catch (IOException ignored) {
            }
        }

        return null;
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    private String determineMimeType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".ico")) return "image/x-icon";
        return "text/plain; charset=UTF-8";
    }
}
