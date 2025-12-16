package com.intive.aifirst.petspot.e2e.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.time.Duration;
import java.util.Map;

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
        
        // Allow geolocation - we'll mock it via CDP (Chrome DevTools Protocol)
        // 1 = allow, 2 = block
        java.util.Map<String, Object> prefs = new java.util.HashMap<>();
        prefs.put("profile.default_content_setting_values.geolocation", 1);  // 1 = allow
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
     * Also clears any pending mock geolocation to ensure clean state for next scenario.
     * 
     * <p>This method should be called in @After hooks to ensure proper cleanup.
     * Safe to call even if no driver exists (no-op in that case).
     */
    public static void quitDriver() {
        // Clear mock geolocation first (before driver is quit)
        pendingMockGeolocation.remove();
        
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();  // Prevent memory leaks
        }
    }
    
    /**
     * ThreadLocal storage for mock coordinates to apply via CDP.
     */
    private static final ThreadLocal<double[]> pendingMockGeolocation = new ThreadLocal<>();
    
    /**
     * Sets mock geolocation coordinates using Chrome DevTools Protocol (CDP).
     * This sets the geolocation at browser level, persisting across page navigations.
     * 
     * <p>Must be called BEFORE navigating to the page that requests location.
     * 
     * @param latitude GPS latitude coordinate
     * @param longitude GPS longitude coordinate
     */
    public static void setMockGeolocation(double latitude, double longitude) {
        pendingMockGeolocation.set(new double[]{latitude, longitude});
        System.out.println("📍 Set pending mock geolocation: " + latitude + ", " + longitude);
        
        // Apply immediately if driver exists
        if (hasDriver()) {
            applyGeolocationOverride(latitude, longitude);
        }
    }
    
    /**
     * Applies geolocation override using Chrome DevTools Protocol via executeCdpCommand.
     * This works with both local ChromeDriver and RemoteWebDriver (Selenium Grid).
     */
    private static void applyGeolocationOverride(double latitude, double longitude) {
        WebDriver webDriver = driver.get();
        if (webDriver == null) {
            System.out.println("⚠️ No driver available for CDP geolocation override");
            return;
        }
        
        try {
            // For RemoteWebDriver, we need to augment it to get CDP access
            WebDriver augmentedDriver = webDriver;
            if (webDriver instanceof RemoteWebDriver) {
                augmentedDriver = new Augmenter().augment(webDriver);
            }
            
            if (augmentedDriver instanceof HasCdp hasCdp) {
                // Use executeCdpCommand which works with any Chrome version
                Map<String, Object> params = Map.of(
                    "latitude", latitude,
                    "longitude", longitude,
                    "accuracy", 100.0
                );
                hasCdp.executeCdpCommand("Emulation.setGeolocationOverride", params);
                System.out.println("✅ CDP geolocation override set via executeCdpCommand: " + latitude + ", " + longitude);
            } else {
                System.out.println("⚠️ Driver does not support CDP - will use JS injection fallback");
            }
        } catch (Exception e) {
            System.err.println("⚠️ CDP geolocation override failed: " + e.getMessage());
            System.out.println("Will fall back to JavaScript injection after page load");
        }
    }
    
    /**
     * Injects JavaScript to override the browser's geolocation API.
     * Used as fallback when CDP is not available.
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
                    // Override getCurrentPosition
                    window.navigator.geolocation.getCurrentPosition = function(success, error, options) {
                        setTimeout(function() {
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
                        }, 10);
                    };
                    // Override watchPosition
                    window.navigator.geolocation.watchPosition = function(success, error, options) {
                        setTimeout(function() {
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
                        }, 10);
                        return 1;
                    };
                    // Override Permissions API
                    if (navigator.permissions) {
                        const originalQuery = navigator.permissions.query.bind(navigator.permissions);
                        navigator.permissions.query = function(desc) {
                            if (desc.name === 'geolocation') {
                                return Promise.resolve({ state: 'granted', onchange: null });
                            }
                            return originalQuery(desc);
                        };
                    }
                    window.__geolocationMocked = true;
                    console.log('Geolocation API mocked via JS: %f, %f');
                    """, coords[0], coords[1], coords[0], coords[1], coords[0], coords[1]);
                
                js.executeScript(script);
                System.out.println("✅ Injected geolocation mock via JavaScript: " + coords[0] + ", " + coords[1]);
            } catch (Exception e) {
                System.err.println("⚠️ JavaScript geolocation mock failed: " + e.getMessage());
            }
        }
    }
    
    /**
     * Checks if mock geolocation is set.
     * @return true if mock coordinates are pending
     */
    public static boolean hasMockGeolocation() {
        return pendingMockGeolocation.get() != null;
    }
    
    /**
     * Gets the pending mock geolocation coordinates.
     * @return array of [latitude, longitude] or null if not set
     */
    public static double[] getMockGeolocation() {
        return pendingMockGeolocation.get();
    }
    
    /**
     * Clears mock geolocation settings.
     */
    public static void clearMockGeolocation() {
        pendingMockGeolocation.remove();
        
        // Try to clear CDP geolocation override if driver exists
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            try {
                WebDriver augmentedDriver = webDriver;
                if (webDriver instanceof RemoteWebDriver) {
                    augmentedDriver = new Augmenter().augment(webDriver);
                }
                if (augmentedDriver instanceof HasCdp hasCdp) {
                    hasCdp.executeCdpCommand("Emulation.clearGeolocationOverride", Map.of());
                }
            } catch (Exception e) {
                // Ignore - driver may be closed
            }
        }
        System.out.println("🧹 Cleared mock geolocation");
    }
}


