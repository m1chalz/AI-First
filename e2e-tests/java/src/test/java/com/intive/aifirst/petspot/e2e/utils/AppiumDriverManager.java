package com.intive.aifirst.petspot.e2e.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Manages Appium AppiumDriver instances with ThreadLocal isolation and platform detection.
 * 
 * <p>This class provides centralized AppiumDriver lifecycle management for both Android and iOS:
 * <ul>
 *   <li>ThreadLocal storage for thread-safe parallel execution</li>
 *   <li>Automatic platform detection (Android vs iOS)</li>
 *   <li>Platform-specific capabilities configuration</li>
 *   <li>Connection to Appium server (default: http://127.0.0.1:4723)</li>
 * </ul>
 * 
 * <h2>Prerequisites:</h2>
 * <ul>
 *   <li>Appium server running on port 4723: {@code npm run appium:start}</li>
 *   <li>Android Emulator (API 34) or iOS Simulator (iOS 17) running</li>
 *   <li>App APK/IPA files available in {@code /apps/} directory</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // In Cucumber hooks or step definitions (Android)
 * AppiumDriver driver = AppiumDriverManager.getDriver("Android");
 * driver.findElement(By.id("petList.addButton.click")).click();
 * 
 * // In Cucumber hooks or step definitions (iOS)
 * AppiumDriver driver = AppiumDriverManager.getDriver("iOS");
 * driver.findElement(By.id("petList.addButton.click")).click();
 * 
 * // In @After hook
 * AppiumDriverManager.quitDriver();
 * }</pre>
 * 
 * <p><strong>Important:</strong> Always call {@link #quitDriver()} in @After hooks
 * to close the app session and prevent resource leaks.
 * 
 * @see io.appium.java_client.AppiumDriver
 * @see io.appium.java_client.android.AndroidDriver
 * @see io.appium.java_client.ios.IOSDriver
 */
public class AppiumDriverManager {
    
    /** ThreadLocal storage for AppiumDriver instances (one per thread for parallel execution) */
    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    
    /** Appium server URL (default local server, overridable via system/env property) */
    private static final String APPIUM_SERVER_URL = System.getProperty(
        "APPIUM_SERVER_URL",
        System.getenv().getOrDefault("APPIUM_SERVER_URL", "http://127.0.0.1:4723/wd/hub")
    );
    
    /** Default implicit wait timeout in seconds */
    private static final int DEFAULT_IMPLICIT_WAIT_SECONDS = 10;
    
    /**
     * Gets the AppiumDriver instance for the current thread.
     * If no driver exists for this thread, initializes a new driver for the specified platform.
     * 
     * @param platform Platform name: "Android" or "iOS" (case-insensitive)
     * @return AppiumDriver instance for current thread
     * @throws IllegalArgumentException if platform is not "Android" or "iOS"
     * @throws RuntimeException if Appium server connection fails
     */
    public static AppiumDriver getDriver(String platform) {
        if (driver.get() == null) {
            initializeDriver(platform);
        }
        return driver.get();
    }
    
    /**
     * Initializes a new AppiumDriver with platform-specific configuration.
     * 
     * <p><strong>Android Configuration:</strong></p>
     * <ul>
     *   <li>Platform: Android 14 (API 34)</li>
     *   <li>Automation: UiAutomator2</li>
     *   <li>Device: Android Emulator</li>
     *   <li>App: {@code /apps/petspot-android.apk}</li>
     * </ul>
     * 
     * <p><strong>iOS Configuration:</strong></p>
     * <ul>
     *   <li>Platform: iOS 17.0</li>
     *   <li>Automation: XCUITest</li>
     *   <li>Device: iPhone 15 Simulator</li>
     *   <li>App: {@code /apps/petspot-ios.app}</li>
     * </ul>
     * 
     * @param platform Platform name: "Android" or "iOS"
     * @throws IllegalArgumentException if platform is not recognized
     * @throws RuntimeException if Appium server URL is malformed or connection fails
     */
    private static void initializeDriver(String platform) {
        AppiumDriver appiumDriver;
        
        try {
            URL serverUrl = new URL(APPIUM_SERVER_URL);
            
            if ("android".equalsIgnoreCase(platform)) {
                appiumDriver = initializeAndroidDriver(serverUrl);
            } else if ("ios".equalsIgnoreCase(platform)) {
                appiumDriver = initializeIOSDriver(serverUrl);
            } else {
                throw new IllegalArgumentException(
                    "Unsupported platform: " + platform + ". Expected 'Android' or 'iOS'"
                );
            }
            
            // Set implicit wait (Appium will poll for elements up to this timeout)
            appiumDriver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(DEFAULT_IMPLICIT_WAIT_SECONDS)
            );
            
            // Store in ThreadLocal for thread safety
            driver.set(appiumDriver);
            
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + APPIUM_SERVER_URL, e);
        }
    }
    
    /**
     * Initializes AndroidDriver with UiAutomator2 capabilities.
     * 
     * @param serverUrl Appium server URL
     * @return Configured AndroidDriver instance
     */
    private static AndroidDriver initializeAndroidDriver(URL serverUrl) {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setPlatformVersion("14");                // Android 14 (API 34)
        options.setDeviceName("Android Emulator");
        options.setAutomationName("UiAutomator2");
        
        // App path (relative to project root)
        String appPath = System.getProperty("user.dir") + "/apps/petspot-android.apk";
        options.setApp(appPath);
        
        // Optional: Auto-grant permissions to avoid permission dialogs during tests
        options.setAutoGrantPermissions(true);
        
        return new AndroidDriver(serverUrl, options);
    }
    
    /**
     * Initializes IOSDriver with XCUITest capabilities.
     * 
     * @param serverUrl Appium server URL
     * @return Configured IOSDriver instance
     */
    private static IOSDriver initializeIOSDriver(URL serverUrl) {
        XCUITestOptions options = new XCUITestOptions();
        String iosDeviceName = System.getProperty(
            "IOS_DEVICE_NAME",
            System.getenv().getOrDefault("IOS_DEVICE_NAME", "iPhone 17 Pro")
        );
        String iosPlatformVersion = System.getProperty(
            "IOS_PLATFORM_VERSION",
            System.getenv().getOrDefault("IOS_PLATFORM_VERSION", "18.1")
        );

        options.setPlatformName("iOS");
        options.setPlatformVersion(iosPlatformVersion);
        options.setDeviceName(iosDeviceName);
        options.setAutomationName("XCUITest");
        
        // App path (relative to project root)
        String appPath = System.getProperty(
            "IOS_APP_PATH",
            System.getenv().getOrDefault(
                "IOS_APP_PATH",
                System.getProperty("user.dir") + "/apps/petspot-ios.app"
            )
        );
        options.setApp(appPath);
        
        // Optional: Auto-accept alerts to avoid blocking tests
        options.setAutoAcceptAlerts(true);
        
        return new IOSDriver(serverUrl, options);
    }
    
    /**
     * Quits the AppiumDriver instance and removes it from ThreadLocal storage.
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
     * Gets the current platform name from the active driver.
     * 
     * @return "Android" or "iOS", or null if no driver is active
     */
    public static String getCurrentPlatform() {
        if (driver.get() == null) {
            return null;
        }
        return driver.get().getCapabilities().getPlatformName().toString();
    }
}

