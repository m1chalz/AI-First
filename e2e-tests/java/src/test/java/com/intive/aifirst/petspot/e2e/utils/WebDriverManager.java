package com.intive.aifirst.petspot.e2e.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
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
     * Initializes a new WebDriver with recommended configuration.
     * 
     * <p>Uses RemoteWebDriver with Selenium Grid (Docker) by default.
     * Falls back to local ChromeDriver if Selenium Grid is not available.
     * 
     * <p>Configuration includes:
     * <ul>
     *   <li>RemoteWebDriver → Selenium Grid at http://localhost:4444 (Docker)</li>
     *   <li>Maximized browser window (1920x1080)</li>
     *   <li>Disabled browser notifications</li>
     *   <li>Disabled popup blocking</li>
     *   <li>Implicit wait timeout ({@value DEFAULT_IMPLICIT_WAIT_SECONDS} seconds)</li>
     * </ul>
     */
    private static void initializeDriver() {
        // Chrome options for stable test execution
        ChromeOptions options = new ChromeOptions();
        
        // Use headless mode based on system property (default: false for local debugging)
        String headless = System.getProperty("webdriver.chrome.headless", "false");
        if ("true".equals(headless) || System.getenv("CI") != null) {
            options.addArguments("--headless=new");
            System.out.println("Running Chrome in headless mode");
        }
        
        // Use fullscreen to maximize available viewport in Docker Selenium Grid
        // Docker screen: SE_SCREEN_WIDTH=1360, SE_SCREEN_HEIGHT=1020
        options.addArguments("--start-maximized");  // Start browser maximized
        options.addArguments("--kiosk");  // Fullscreen mode (no browser chrome)
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-dev-shm-usage");  // Overcome limited resource problems
        options.addArguments("--remote-allow-origins=*");  // Allow remote connections
        options.addArguments("--no-sandbox");  // Required for some environments
        
        // Block geolocation - this makes the app show ALL announcements (no location filter)
        // When CDP geolocation mock doesn't work (Chrome version mismatch), blocking is safer
        // 1 = allow, 2 = block
        java.util.Map<String, Object> prefs = new java.util.HashMap<>();
        prefs.put("profile.default_content_setting_values.geolocation", 2);  // 2 = block
        options.setExperimentalOption("prefs", prefs);
        
        // Check if we should use Selenium Grid (Docker) or local ChromeDriver
        String gridUrl = System.getProperty("selenium.grid.url", "http://localhost:4444");
        WebDriver webDriver;
        
        try {
            // Try to connect to Selenium Grid (Docker)
            System.out.println("🌐 Connecting to Selenium Grid at " + gridUrl);
            webDriver = new RemoteWebDriver(new URL(gridUrl), options);
            
            // Set webdriver.remote=true so TestConfig uses web.base.url.docker (http://frontend:8080)
            // instead of web.base.url (http://localhost:8080) which doesn't work from Docker container
            System.setProperty("webdriver.remote", "true");
            System.out.println("✅ Connected to Selenium Grid successfully");
        } catch (Exception e) {
            // Fall back to local ChromeDriver
            System.out.println("⚠️  Selenium Grid not available at " + gridUrl);
            System.out.println("⚠️  Falling back to local ChromeDriver: " + e.getMessage());
            webDriver = new ChromeDriver(options);
            System.out.println("✅ Using local ChromeDriver");
        }
        
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
    
    /**
     * ThreadLocal storage for mock coordinates to apply after page navigation.
     */
    private static final ThreadLocal<double[]> pendingMockGeolocation = new ThreadLocal<>();
    
    /**
     * Sets mock geolocation coordinates using JavaScript injection.
     * This approach works with any Chrome version (no CDP dependency).
     * 
     * <p>The coordinates are stored and applied when injectGeolocationMock() is called
     * after the page starts loading but before the app requests geolocation.
     * 
     * @param latitude GPS latitude coordinate
     * @param longitude GPS longitude coordinate
     */
    public static void setMockGeolocation(double latitude, double longitude) {
        pendingMockGeolocation.set(new double[]{latitude, longitude});
        System.out.println("Set pending mock geolocation: " + latitude + ", " + longitude);
    }
    
    /**
     * Injects JavaScript to override the browser's geolocation API.
     * Must be called after page navigation but before the app requests location.
     */
    public static void injectGeolocationMock() {
        double[] coords = pendingMockGeolocation.get();
        if (coords == null) {
            System.out.println("No mock geolocation set, skipping injection");
            return;
        }
        
        WebDriver webDriver = getDriver();
        if (webDriver instanceof JavascriptExecutor js) {
            try {
                String script = String.format("""
                    window.navigator.geolocation.getCurrentPosition = function(success, error, options) {
                        success({
                            coords: {
                                latitude: %f,
                                longitude: %f,
                                accuracy: 100,
                                altitude: null,
                                altitudeAccuracy: null,
                                heading: null,
                                speed: null
                            },
                            timestamp: Date.now()
                        });
                    };
                    window.navigator.geolocation.watchPosition = function(success, error, options) {
                        success({
                            coords: {
                                latitude: %f,
                                longitude: %f,
                                accuracy: 100,
                                altitude: null,
                                altitudeAccuracy: null,
                                heading: null,
                                speed: null
                            },
                            timestamp: Date.now()
                        });
                        return 1;
                    };
                    window.__geolocationMocked = true;
                    console.log('Geolocation API mocked: %f, %f');
                    """, coords[0], coords[1], coords[0], coords[1], coords[0], coords[1]);
                
                js.executeScript(script);
                System.out.println("Injected geolocation mock via JavaScript: " + coords[0] + ", " + coords[1]);
            } catch (Exception e) {
                System.err.println("Warning: JavaScript geolocation mock failed: " + e.getMessage());
            }
        }
    }
    
    /**
     * Clears mock geolocation settings.
     */
    public static void clearMockGeolocation() {
        pendingMockGeolocation.remove();
        System.out.println("Cleared mock geolocation");
    }
}

