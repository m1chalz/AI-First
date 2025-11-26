package com.intive.aifirst.petspot.e2e.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized test configuration management.
 * 
 * <p>Loads configuration from {@code test.properties} file (if present) or falls back to default values.
 * Configuration includes:
 * <ul>
 *   <li>Base URLs for web application (local, staging, production)</li>
 *   <li>Timeout values for implicit/explicit waits</li>
 *   <li>Appium capabilities (platform versions, device names)</li>
 *   <li>App file paths for mobile testing</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // In step definitions
 * String baseUrl = TestConfig.getWebBaseUrl();
 * driver.get(baseUrl + "/pets");
 * 
 * // Get timeout
 * int timeout = TestConfig.getImplicitWaitSeconds();
 * }</pre>
 * 
 * <h2>Configuration File (test.properties):</h2>
 * <pre>
 * # Web configuration
 * web.base.url=http://localhost:3000
 * 
 * # Mobile configuration
 * android.platform.version=14
 * android.device.name=Android Emulator
 * ios.platform.version=17.0
 * ios.device.name=iPhone 15
 * 
 * # Timeout configuration (seconds)
 * timeout.implicit.wait=10
 * timeout.explicit.wait=20
 * </pre>
 * 
 * @see java.util.Properties
 */
public class TestConfig {
    
    /** Properties instance (loaded from test.properties if present) */
    private static final Properties properties = new Properties();
    
    /** Properties file name */
    private static final String PROPERTIES_FILE = "test.properties";
    
    // Static initialization block - loads properties on class load
    static {
        loadProperties();
    }
    
    /**
     * Loads properties from test.properties file (if present).
     * If file is missing, uses default configuration values.
     */
    private static void loadProperties() {
        try (InputStream inputStream = TestConfig.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            
            if (inputStream != null) {
                properties.load(inputStream);
                System.out.println("Loaded test configuration from " + PROPERTIES_FILE);
            } else {
                System.out.println(PROPERTIES_FILE + " not found, using default configuration");
            }
            
        } catch (IOException e) {
            System.err.println("Error loading " + PROPERTIES_FILE + ": " + e.getMessage());
            System.err.println("Using default configuration values");
        }
    }
    
    // ===== Web Configuration =====
    
    /**
     * Gets the base URL for web application.
     * 
     * @return Base URL (default: http://localhost:3000)
     */
    public static String getWebBaseUrl() {
        return properties.getProperty("web.base.url", "http://localhost:3000");
    }
    
    // ===== Android Configuration =====
    
    /**
     * Gets Android platform version for Appium tests.
     * 
     * @return Android version (default: "14")
     */
    public static String getAndroidPlatformVersion() {
        return properties.getProperty("android.platform.version", "14");
    }
    
    /**
     * Gets Android device name for Appium tests.
     * 
     * @return Device name (default: "Android Emulator")
     */
    public static String getAndroidDeviceName() {
        return properties.getProperty("android.device.name", "Android Emulator");
    }
    
    /**
     * Gets Android app file path for Appium tests.
     * 
     * @return App path (default: ./apps/petspot-android.apk relative to project root)
     */
    public static String getAndroidAppPath() {
        String defaultPath = System.getProperty("user.dir") + "/apps/petspot-android.apk";
        return properties.getProperty("android.app.path", defaultPath);
    }
    
    // ===== iOS Configuration =====
    
    /**
     * Gets iOS platform version for Appium tests.
     * 
     * @return iOS version (default: "17.0")
     */
    public static String getIOSPlatformVersion() {
        return properties.getProperty("ios.platform.version", "17.0");
    }
    
    /**
     * Gets iOS device name for Appium tests.
     * 
     * @return Device name (default: "iPhone 15")
     */
    public static String getIOSDeviceName() {
        return properties.getProperty("ios.device.name", "iPhone 15");
    }
    
    /**
     * Gets iOS app file path for Appium tests.
     * 
     * @return App path (default: ./apps/petspot-ios.app relative to project root)
     */
    public static String getIOSAppPath() {
        String defaultPath = System.getProperty("user.dir") + "/apps/petspot-ios.app";
        return properties.getProperty("ios.app.path", defaultPath);
    }
    
    // ===== Appium Server Configuration =====
    
    /**
     * Gets Appium server URL.
     * 
     * @return Server URL (default: http://127.0.0.1:4723)
     */
    public static String getAppiumServerUrl() {
        return properties.getProperty("appium.server.url", "http://127.0.0.1:4723");
    }
    
    // ===== Timeout Configuration =====
    
    /**
     * Gets implicit wait timeout in seconds.
     * Used for WebDriver/AppiumDriver element polling.
     * 
     * @return Timeout in seconds (default: 10)
     */
    public static int getImplicitWaitSeconds() {
        String value = properties.getProperty("timeout.implicit.wait", "10");
        return Integer.parseInt(value);
    }
    
    /**
     * Gets explicit wait timeout in seconds.
     * Used for explicit WebDriverWait conditions.
     * 
     * @return Timeout in seconds (default: 20)
     */
    public static int getExplicitWaitSeconds() {
        String value = properties.getProperty("timeout.explicit.wait", "20");
        return Integer.parseInt(value);
    }
    
    /**
     * Gets page load timeout in seconds.
     * Used for browser page load operations.
     * 
     * @return Timeout in seconds (default: 30)
     */
    public static int getPageLoadTimeoutSeconds() {
        String value = properties.getProperty("timeout.page.load", "30");
        return Integer.parseInt(value);
    }
    
    // ===== Utility Methods =====
    
    /**
     * Gets a custom property value with fallback default.
     * 
     * @param key Property key
     * @param defaultValue Default value if key not found
     * @return Property value or default
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Prints all loaded configuration values (for debugging).
     */
    public static void printConfiguration() {
        System.out.println("========== Test Configuration ==========");
        System.out.println("Web Base URL: " + getWebBaseUrl());
        System.out.println("Android Platform: " + getAndroidPlatformVersion());
        System.out.println("Android Device: " + getAndroidDeviceName());
        System.out.println("iOS Platform: " + getIOSPlatformVersion());
        System.out.println("iOS Device: " + getIOSDeviceName());
        System.out.println("Appium Server: " + getAppiumServerUrl());
        System.out.println("Implicit Wait: " + getImplicitWaitSeconds() + "s");
        System.out.println("Explicit Wait: " + getExplicitWaitSeconds() + "s");
        System.out.println("========================================");
    }
}

