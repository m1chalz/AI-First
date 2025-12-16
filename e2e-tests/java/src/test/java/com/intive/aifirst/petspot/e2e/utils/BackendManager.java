package com.intive.aifirst.petspot.e2e.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Manages backend server lifecycle for E2E mobile tests.
 * 
 * <p>Automatically checks if the backend is running and starts it if needed.
 * For mobile tests, the backend is started in Docker using server/Dockerfile.qa.
 * This ensures consistent environment and no dependency on local Node.js setup.
 * 
 * <p>Health check endpoint: http://localhost:3000/api/health
 * 
 * <p><strong>Why Docker for mobile tests?</strong>
 * <ul>
 *   <li>iOS simulator connects to host via 127.0.0.1:3000</li>
 *   <li>Docker backend publishes port 3000 to host</li>
 *   <li>No need for npm install or local Node.js dependencies</li>
 *   <li>Consistent with QA environment setup</li>
 * </ul>
 */
public final class BackendManager {
    
    private static final String BACKEND_URL = "http://localhost:3000";
    private static final String HEALTH_CHECK_ENDPOINT = BACKEND_URL + "/api/health";
    private static final int BACKEND_STARTUP_TIMEOUT_SECONDS = 30;
    private static final int HEALTH_CHECK_INTERVAL_MS = 1000;
    
    private static Process backendProcess = null;
    private static boolean isBackendStartedByUs = false;
    
    static {
        // Register shutdown hook to stop backend when JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stopBackendIfStartedByUs();
        }));
    }
    
    /** Private constructor to prevent instantiation. */
    private BackendManager() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Checks if backend is running by making a GET request to /api/announcements.
     * 
     * @return true if backend responds with 2xx status code, false otherwise
     */
    public static boolean isBackendRunning() {
        try {
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HEALTH_CHECK_ENDPOINT))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Ensures backend is running. If not, starts it as a background process.
     * 
     * <p>The backend is started using {@code npm run dev} in the server/ directory.
     * Output is redirected to target/backend.log for debugging.
     * 
     * @throws RuntimeException if backend fails to start within timeout
     */
    public static synchronized void ensureBackendRunning() {
        // Check if already running
        if (isBackendRunning()) {
            System.out.println("✅ Backend server already running at " + BACKEND_URL);
            return;
        }
        
        System.out.println("⚙️  Backend not running. Starting Docker backend...");
        
        // Get project root (assuming we're in e2e-tests/java/)
        String projectRoot = Paths.get(System.getProperty("user.dir"))
            .getParent() // e2e-tests/
            .getParent() // project root
            .toString();
        
        String e2eTestsDir = Paths.get(projectRoot, "e2e-tests").toString();
        File e2eTestsDirFile = new File(e2eTestsDir);
        File logFile = new File("target/backend.log");
        
        try {
            // Start backend using docker-compose (only backend service, not full QA env)
            ProcessBuilder pb = new ProcessBuilder(
                "docker-compose",
                "-f", "docker-compose.qa-env.yml",
                "up", "-d", "backend"
            );
            pb.directory(e2eTestsDirFile);
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
            
            backendProcess = pb.start();
            int exitCode = backendProcess.waitFor();
            
            if (exitCode != 0) {
                String errorLog = readLastLines(logFile, 30);
                throw new RuntimeException(
                    "Docker backend failed to start (exit code: " + exitCode + ")"
                    + ". Last log lines:\n" + errorLog
                );
            }
            
            isBackendStartedByUs = true;
            System.out.println("🚀 Backend container started (logs: docker logs qa-backend)");
            System.out.println("⏳ Waiting for backend to be healthy...");
            
            // Wait for backend to be ready
            int waitedSeconds = 0;
            while (waitedSeconds < BACKEND_STARTUP_TIMEOUT_SECONDS) {
                if (isBackendRunning()) {
                    System.out.println("✅ Backend healthy and ready after " + waitedSeconds + "s");
                    return;
                }
                
                Thread.sleep(HEALTH_CHECK_INTERVAL_MS);
                waitedSeconds++;
            }
            
            // Timeout
            String log = readLastLines(logFile, 50);
            throw new RuntimeException(
                "Backend failed to start within " + BACKEND_STARTUP_TIMEOUT_SECONDS
                + "s. Check logs at: " + logFile.getAbsolutePath() + "\n\nLast lines:\n" + log
            );
            
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to start backend: " + e.getMessage()
                + "\n\nMake sure npm is installed and server/package.json exists.", 
                e
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Backend startup interrupted", e);
        }
    }
    
    /**
     * Stops the backend Docker container if it was started by this manager.
     * If the backend was already running before tests, it will not be stopped.
     */
    public static synchronized void stopBackendIfStartedByUs() {
        if (isBackendStartedByUs) {
            System.out.println("🛑 Stopping backend Docker container (started by tests)...");
            
            try {
                // Get e2e-tests directory for docker-compose
                String projectRoot = Paths.get(System.getProperty("user.dir"))
                    .getParent()
                    .getParent()
                    .toString();
                String e2eTestsDir = Paths.get(projectRoot, "e2e-tests").toString();
                
                // Stop backend container
                ProcessBuilder pb = new ProcessBuilder(
                    "docker-compose",
                    "-f", "docker-compose.qa-env.yml",
                    "stop", "backend"
                );
                pb.directory(new File(e2eTestsDir));
                
                Process stopProcess = pb.start();
                boolean terminated = stopProcess.waitFor(10, TimeUnit.SECONDS);
                
                if (!terminated) {
                    System.out.println("⚠️  Backend container didn't stop gracefully, killing...");
                    stopProcess.destroyForcibly();
                }
                
                System.out.println("✅ Backend container stopped");
            } catch (Exception e) {
                System.err.println("⚠️  Failed to stop backend container: " + e.getMessage());
            }
            
            backendProcess = null;
            isBackendStartedByUs = false;
        } else {
            System.out.println("ℹ️  Backend was already running before tests - leaving it running");
        }
    }
    
    /**
     * Reads last N lines from a file.
     * 
     * @param file the file to read
     * @param lines number of lines to read
     * @return last N lines as a string
     */
    private static String readLastLines(File file, int lines) {
        try {
            ProcessBuilder pb = new ProcessBuilder("tail", "-n", String.valueOf(lines), file.getAbsolutePath());
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            
            process.waitFor();
            return sb.toString();
        } catch (Exception e) {
            return "Failed to read log file: " + e.getMessage();
        }
    }
}

