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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper for taking debug screenshots before actions.
 * Enable with system property: -Ddebug.screenshots=true
 * 
 * <p>Screenshots are saved to: target/debug-screenshots/
 * 
 * <p>Usage:
 * <pre>
 * DebugScreenshotHelper.beforeAction(driver, "click_cancel_button");
 * // ... perform action ...
 * </pre>
 */
public class DebugScreenshotHelper {
    
    private static final boolean DEBUG_ENABLED = Boolean.getBoolean("debug.screenshots");
    private static final String SCREENSHOT_DIR = "target/debug-screenshots";
    private static int counter = 0;
    
    /**
     * Takes a screenshot before an action if debug mode is enabled.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param actionName Name of the action (e.g., "click_cancel_button")
     */
    public static void beforeAction(Object driver, String actionName) {
        if (!DEBUG_ENABLED) {
            return;
        }
        
        try {
            counter++;
            // Format: 001_20251216-152304-567_action_name.png
            // Date and time with milliseconds for precise ordering and time difference calculations
            String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS").format(new Date());
            String filename = String.format("%03d_%s_%s.png", counter, timestamp, sanitizeFilename(actionName));
            
            takeScreenshot(driver, filename);
            System.out.println("📸 Debug screenshot: " + filename);
            
        } catch (Exception e) {
            System.err.println("⚠️  Failed to take debug screenshot: " + e.getMessage());
        }
    }
    
    /**
     * Takes a screenshot with custom message.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param message Custom message for the screenshot
     */
    public static void capture(Object driver, String message) {
        beforeAction(driver, message);
    }
    
    /**
     * Resets the counter (called at start of each scenario).
     */
    public static void reset() {
        counter = 0;
    }
    
    private static void takeScreenshot(Object driver, String filename) throws IOException {
        // Create directory if it doesn't exist
        Path screenshotPath = Paths.get(SCREENSHOT_DIR);
        if (!Files.exists(screenshotPath)) {
            Files.createDirectories(screenshotPath);
        }
        
        // Take screenshot
        byte[] screenshotBytes;
        if (driver instanceof TakesScreenshot) {
            screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } else if (driver instanceof AppiumDriver) {
            screenshotBytes = ((AppiumDriver) driver).getScreenshotAs(OutputType.BYTES);
        } else if (driver instanceof WebDriver) {
            screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } else {
            throw new IllegalArgumentException("Driver must be WebDriver or AppiumDriver");
        }
        
        // Save to file
        Path targetFile = screenshotPath.resolve(filename);
        Files.write(targetFile, screenshotBytes);
    }
    
    private static String sanitizeFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Checks if debug screenshots are enabled.
     * 
     * @return true if -Ddebug.screenshots=true was set
     */
    public static boolean isEnabled() {
        return DEBUG_ENABLED;
    }
}

