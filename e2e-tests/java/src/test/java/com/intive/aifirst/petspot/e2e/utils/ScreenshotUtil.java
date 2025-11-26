package com.intive.aifirst.petspot.e2e.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for capturing screenshots on test failure for debugging purposes.
 * 
 * <p>Supports both WebDriver (web tests) and AppiumDriver (mobile tests).
 * Screenshots are saved to {@code target/screenshots/} with timestamped filenames.
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // In Cucumber @After hook
 * @After
 * public void afterScenario(Scenario scenario) {
 *     if (scenario.isFailed()) {
 *         WebDriver driver = WebDriverManager.getDriver();
 *         ScreenshotUtil.captureScreenshot(driver, scenario.getName());
 *     }
 *     WebDriverManager.quitDriver();
 * }
 * }</pre>
 * 
 * <h2>Screenshot Storage:</h2>
 * <ul>
 *   <li>Directory: {@code target/screenshots/}</li>
 *   <li>Filename format: {@code <scenarioName>_<timestamp>.png}</li>
 *   <li>Example: {@code view-pet-list_2025-11-26_08-30-15.png}</li>
 * </ul>
 * 
 * @see org.openqa.selenium.TakesScreenshot
 */
public class ScreenshotUtil {
    
    /** Directory for storing screenshots (relative to project root) */
    private static final String SCREENSHOT_DIR = "target/screenshots";
    
    /** Timestamp format for screenshot filenames */
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Captures a screenshot from WebDriver or AppiumDriver and saves it to disk.
     * 
     * <p>If screenshot capture fails, logs the error but does not throw exception
     * to prevent secondary test failures.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param scenarioName Name of the test scenario (used in filename)
     * @return Path to saved screenshot file, or null if capture failed
     */
    public static Path captureScreenshot(WebDriver driver, String scenarioName) {
        if (driver == null) {
            System.err.println("Cannot capture screenshot: driver is null");
            return null;
        }
        
        try {
            // Ensure screenshot directory exists
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Generate timestamped filename
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String sanitizedScenarioName = sanitizeFilename(scenarioName);
            String filename = String.format("%s_%s.png", sanitizedScenarioName, timestamp);
            Path destinationPath = Paths.get(SCREENSHOT_DIR, filename);
            
            // Capture screenshot (works for both WebDriver and AppiumDriver)
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            
            // Copy to destination
            Files.copy(screenshotFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Screenshot saved: " + destinationPath.toAbsolutePath());
            return destinationPath;
            
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ClassCastException e) {
            System.err.println("Driver does not support screenshots: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Captures a screenshot from AppiumDriver with platform-specific filename.
     * 
     * @param driver AppiumDriver instance
     * @param scenarioName Name of the test scenario
     * @param platform Platform name ("Android" or "iOS")
     * @return Path to saved screenshot file, or null if capture failed
     */
    public static Path captureScreenshot(AppiumDriver driver, String scenarioName, String platform) {
        String platformPrefix = platform != null ? platform + "_" : "";
        String fullScenarioName = platformPrefix + scenarioName;
        return captureScreenshot((WebDriver) driver, fullScenarioName);
    }
    
    /**
     * Sanitizes scenario name for use in filenames.
     * Replaces unsafe characters with underscores.
     * 
     * @param scenarioName Original scenario name
     * @return Sanitized filename-safe string
     */
    private static String sanitizeFilename(String scenarioName) {
        if (scenarioName == null || scenarioName.isEmpty()) {
            return "unknown-scenario";
        }
        // Replace unsafe filename characters with underscores
        return scenarioName
            .replaceAll("[^a-zA-Z0-9-_]", "_")
            .replaceAll("_{2,}", "_")  // Replace multiple underscores with single
            .toLowerCase();
    }
    
    /**
     * Cleans up old screenshots (optional utility method).
     * Deletes all .png files in the screenshot directory.
     * 
     * <p>Can be called in @BeforeAll hook to start with clean directory.
     * 
     * @return Number of files deleted
     */
    public static int cleanupScreenshots() {
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            return 0;
        }
        
        File[] screenshots = screenshotDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (screenshots == null) {
            return 0;
        }
        
        int deletedCount = 0;
        for (File screenshot : screenshots) {
            if (screenshot.delete()) {
                deletedCount++;
            }
        }
        
        System.out.println("Cleaned up " + deletedCount + " old screenshot(s)");
        return deletedCount;
    }
}

