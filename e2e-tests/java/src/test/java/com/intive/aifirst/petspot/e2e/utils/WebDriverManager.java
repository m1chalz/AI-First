package com.intive.aifirst.petspot.e2e.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Manages WebDriver instances with ThreadLocal isolation for parallel test execution.
 * 
 * <p>This class provides centralized WebDriver lifecycle management:
 * <ul>
 *   <li>Automatic ChromeDriver setup via WebDriverManager</li>
 *   <li>ThreadLocal storage for thread-safe parallel execution</li>
 *   <li>Configurable implicit wait timeouts</li>
 *   <li>Chrome-specific options (maximized window, no notifications)</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // In Cucumber hooks or step definitions
 * WebDriver driver = WebDriverManager.getDriver();
 * driver.get("http://localhost:3000/pets");
 * 
 * // In @After hook
 * WebDriverManager.quitDriver();
 * }</pre>
 * 
 * <p><strong>Important:</strong> Always call {@link #quitDriver()} in @After hooks
 * to prevent memory leaks and ensure proper cleanup.
 * 
 * @see org.openqa.selenium.WebDriver
 * @see io.github.bonigarcia.wdm.WebDriverManager
 */
public class WebDriverManager {
    
    /** ThreadLocal storage for WebDriver instances (one per thread for parallel execution) */
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
    /** Default implicit wait timeout in seconds - reduced from 10s for faster test execution */
    private static final int DEFAULT_IMPLICIT_WAIT_SECONDS = 3;
    
    /**
     * Checks if a WebDriver instance exists for the current thread without initializing one.
     * 
     * @return true if driver exists, false otherwise
     */
    public static boolean hasDriver() {
        return driver.get() != null;
    }
    
    /**
     * Gets the WebDriver instance for the current thread.
     * If no driver exists for this thread, initializes a new ChromeDriver.
     * 
     * @return WebDriver instance for current thread
     */
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }
    
    /**
     * Initializes a new ChromeDriver with recommended configuration.
     * 
     * <p>Configuration includes:
     * <ul>
     *   <li>Automatic ChromeDriver binary management</li>
     *   <li>Maximized browser window</li>
     *   <li>Disabled browser notifications</li>
     *   <li>Disabled popup blocking</li>
     *   <li>Implicit wait timeout ({@value DEFAULT_IMPLICIT_WAIT_SECONDS} seconds)</li>
     * </ul>
     */
    private static void initializeDriver() {
        // Automatic ChromeDriver setup (no manual driver download needed)
        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        
        // Chrome options for stable test execution
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-dev-shm-usage");  // Overcome limited resource problems
        options.addArguments("--remote-allow-origins=*");  // Allow remote connections
        
        // Initialize ChromeDriver
        WebDriver webDriver = new ChromeDriver(options);
        
        // Set implicit wait (WebDriver will poll for elements up to this timeout)
        webDriver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(DEFAULT_IMPLICIT_WAIT_SECONDS)
        );
        
        // Store in ThreadLocal for thread safety
        driver.set(webDriver);
    }
    
    /**
     * Quits the WebDriver instance and removes it from ThreadLocal storage.
     * 
     * <p>This method should be called in @After hooks to ensure proper cleanup.
     * Safe to call even if no driver exists (no-op in that case).
     */
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();  // Prevent memory leaks
        }
    }
}

