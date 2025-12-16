package com.intive.aifirst.petspot.e2e.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utility class to check if Docker is running and required containers are available.
 * 
 * <p>This class provides pre-flight checks for E2E tests that depend on Docker:
 * <ul>
 *   <li>Docker daemon is running</li>
 *   <li>Required containers (backend, frontend) are available</li>
 * </ul>
 * 
 * <h2>Usage:</h2>
 * <pre>
 * // Check if Docker is running (throws exception if not)
 * DockerChecker.ensureDockerRunning();
 * 
 * // Check if specific container is running
 * DockerChecker.ensureContainerRunning("qa-backend", 3000);
 * </pre>
 * 
 * <h2>Error Messages:</h2>
 * <p>If Docker is not running, throws RuntimeException with helpful message:
 * <pre>
 * ❌ Docker is not running!
 * 
 * Please start Docker Desktop:
 *   - macOS: Open Docker Desktop app from Applications
 *   - Linux: sudo systemctl start docker
 *   - Windows: Start Docker Desktop from Start Menu
 * 
 * Then re-run the tests.
 * </pre>
 */
public final class DockerChecker {
    
    private DockerChecker() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if Docker daemon is running.
     * 
     * @throws RuntimeException if Docker is not running with helpful instructions
     */
    public static void ensureDockerRunning() {
        if (!isDockerRunning()) {
            String osName = System.getProperty("os.name").toLowerCase();
            String startCommand;
            
            if (osName.contains("mac")) {
                startCommand = "Open Docker Desktop app from Applications";
            } else if (osName.contains("nix") || osName.contains("nux")) {
                startCommand = "sudo systemctl start docker";
            } else if (osName.contains("win")) {
                startCommand = "Start Docker Desktop from Start Menu";
            } else {
                startCommand = "Start Docker Desktop";
            }
            
            throw new RuntimeException(String.format(
                "\n\n" +
                "❌ Docker is not running!\n" +
                "\n" +
                "Web E2E tests require Docker to run backend and frontend services.\n" +
                "\n" +
                "Please start Docker:\n" +
                "  %s\n" +
                "\n" +
                "Then re-run the tests.\n" +
                "\n",
                startCommand
            ));
        }
        
        System.out.println("✅ Docker is running");
    }
    
    /**
     * Checks if a specific container is running on the expected port.
     * 
     * @param containerName Name of the Docker container (e.g., "qa-backend")
     * @param port Expected port number (e.g., 3000)
     * @throws RuntimeException if container is not running with instructions
     */
    public static void ensureContainerRunning(String containerName, int port) {
        if (!isContainerRunning(containerName)) {
            throw new RuntimeException(String.format(
                "\n\n" +
                "❌ Container '%s' is not running!\n" +
                "\n" +
                "Web E2E tests require Docker containers to be running.\n" +
                "\n" +
                "Start the QA environment:\n" +
                "  cd e2e-tests\n" +
                "  docker-compose -f docker-compose.qa-env.yml up -d\n" +
                "\n" +
                "Then re-run the tests.\n" +
                "\n",
                containerName
            ));
        }
        
        System.out.printf("✅ Container '%s' is running on port %d%n", containerName, port);
    }
    
    /**
     * Checks if Docker daemon is running by executing {@code docker ps}.
     * 
     * @return true if Docker is running, false otherwise
     */
    private static boolean isDockerRunning() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"docker", "ps"});
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if a specific Docker container is running.
     * 
     * @param containerName Name of the container to check
     * @return true if container is running, false otherwise
     */
    private static boolean isContainerRunning(String containerName) {
        try {
            Process process = Runtime.getRuntime().exec(
                new String[]{"docker", "ps", "--filter", "name=" + containerName, "--format", "{{.Names}}"}
            );
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            reader.close();
            
            int exitCode = process.waitFor();
            return exitCode == 0 && line != null && line.contains(containerName);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Auto-starts QA environment containers if they are not running.
     * 
     * <p>This method attempts to start containers using docker-compose.
     * QA environment includes: backend, frontend, and Selenium Grid (for web tests).
     * If containers are already running, this is a no-op.
     * 
     * @return true if containers were started or already running, false if failed
     */
    public static boolean autoStartQaEnvironment() {
        // Check if all required containers are running
        boolean backendRunning = isContainerRunning("qa-backend");
        boolean frontendRunning = isContainerRunning("qa-frontend");
        boolean seleniumRunning = isContainerRunning("qa-selenium-router");
        
        if (backendRunning && frontendRunning && seleniumRunning) {
            System.out.println("✅ QA environment already running (backend + frontend + Selenium Grid)");
            return true;
        }
        
        System.out.println("🚀 Starting QA environment (backend + frontend + Selenium Grid)...");
        if (!backendRunning) {
            System.out.println("   - Backend: not running");
        }
        if (!frontendRunning) {
            System.out.println("   - Frontend: not running");
        }
        if (!seleniumRunning) {
            System.out.println("   - Selenium Grid: not running");
        }
        
        try {
            // Try docker compose (v2) first, fall back to docker-compose (v1)
            ProcessBuilder pb;
            try {
                // Test if docker compose (v2) is available
                Process testProcess = Runtime.getRuntime().exec(new String[]{"docker", "compose", "version"});
                testProcess.waitFor();
                if (testProcess.exitValue() == 0) {
                    // Use docker compose (v2)
                    pb = new ProcessBuilder(
                        "docker", "compose",
                        "-f", "docker-compose.qa-env.yml",
                        "up", "-d"
                    );
                } else {
                    throw new Exception("docker compose not available");
                }
            } catch (Exception e) {
                // Fall back to docker-compose (v1)
                pb = new ProcessBuilder(
                    "docker-compose",
                    "-f", "docker-compose.qa-env.yml",
                    "up", "-d"
                );
            }
            
            // Set working directory to e2e-tests (where docker-compose.qa-env.yml is located)
            // Try relative path first, then absolute
            java.io.File e2eDir = new java.io.File("e2e-tests");
            if (!e2eDir.exists()) {
                // Try from project root
                e2eDir = new java.io.File("../e2e-tests");
            }
            if (!e2eDir.exists()) {
                // Try absolute path (assuming we're in e2e-tests/java)
                e2eDir = new java.io.File(System.getProperty("user.dir")).getParentFile();
            }
            
            pb.directory(e2eDir);
            pb.redirectErrorStream(true);
            
            System.out.println("Working directory: " + e2eDir.getAbsolutePath());
            
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[docker-compose] " + line);
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ QA environment started successfully");
                
                // Wait for containers to be healthy
                System.out.println("⏳ Waiting for services to become healthy...");
                waitForContainerHealth("qa-backend", 30);
                waitForContainerHealth("qa-frontend", 30);
                
                // Selenium Grid doesn't have health check - wait for router port
                waitForSeleniumGrid(30);
                
                return true;
            } else {
                System.err.println("❌ Failed to start QA environment (exit code: " + exitCode + ")");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error starting QA environment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Waits for Selenium Grid to become available (max timeout seconds).
     * 
     * @param timeoutSeconds Maximum time to wait in seconds
     */
    private static void waitForSeleniumGrid(int timeoutSeconds) {
        System.out.println("⏳ Waiting for Selenium Grid to become ready...");
        
        long startTime = System.currentTimeMillis();
        long timeoutMillis = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try {
                // Check if Selenium Grid router is responding
                Process process = Runtime.getRuntime().exec(
                    new String[]{"curl", "-s", "-o", "/dev/null", "-w", "%{http_code}", "http://localhost:4444/status"}
                );
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String httpCode = reader.readLine();
                reader.close();
                process.waitFor();
                
                if ("200".equals(httpCode)) {
                    System.out.println("✅ Selenium Grid is ready at http://localhost:4444");
                    return;
                }
                
                Thread.sleep(1000);
            } catch (Exception e) {
                // Ignore and retry
            }
        }
        
        System.out.printf("⚠️  Selenium Grid did not become ready within %d seconds (may still be starting)%n",
            timeoutSeconds);
    }
    
    /**
     * Waits for a container to become healthy (max timeout seconds).
     * 
     * @param containerName Name of the container
     * @param timeoutSeconds Maximum time to wait in seconds
     */
    private static void waitForContainerHealth(String containerName, int timeoutSeconds) {
        System.out.printf("⏳ Waiting for %s to become healthy...%n", containerName);
        
        long startTime = System.currentTimeMillis();
        long timeoutMillis = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try {
                Process process = Runtime.getRuntime().exec(
                    new String[]{
                        "docker", "inspect",
                        "--format", "{{.State.Health.Status}}",
                        containerName
                    }
                );
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String health = reader.readLine();
                reader.close();
                process.waitFor();
                
                if ("healthy".equals(health)) {
                    System.out.printf("✅ %s is healthy%n", containerName);
                    return;
                }
                
                Thread.sleep(1000);
            } catch (Exception e) {
                // Ignore and retry
            }
        }
        
        System.out.printf("⚠️  %s did not become healthy within %d seconds (may still be starting)%n",
            containerName, timeoutSeconds);
    }
}

