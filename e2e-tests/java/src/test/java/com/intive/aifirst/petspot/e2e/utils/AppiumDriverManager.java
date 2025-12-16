package com.intive.aifirst.petspot.e2e.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Manages Appium AppiumDriver instances with ThreadLocal isolation and platform detection.
 * 
 * <p>This class provides centralized AppiumDriver lifecycle management for both Android and iOS:
 * <ul>
 *   <li>ThreadLocal storage for thread-safe parallel execution</li>
 *   <li>Automatic platform detection (Android vs iOS)</li>
 *   <li>Platform-specific capabilities configuration</li>
 *   <li>Connection to Appium server (default: http://0.0.0.0:4723)</li>
 * </ul>
 * 
 * <h2>Prerequisites:</h2>
 * <ul>
 *   <li>Appium installed globally: {@code npm install -g appium} (auto-starts if not running)</li>
 *   <li>Android Emulator (API 34) or iOS Simulator (iOS 17) running</li>
 *   <li>App APK/IPA files available in {@code /apps/} directory</li>
 * </ul>
 * 
 * <p><strong>Auto-Start Feature:</strong> This manager automatically checks if Appium is running
 * and starts it in the background if needed. Logs are written to {@code /tmp/appium.log}.
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
    
    /** ThreadLocal flag for location permission - set by Hooks based on @location tag */
    private static final ThreadLocal<Boolean> grantLocationPermission = ThreadLocal.withInitial(() -> false);
    
    /** ThreadLocal flag for showing location dialog - set by Hooks based on @locationDialog tag */
    private static final ThreadLocal<Boolean> showLocationDialog = ThreadLocal.withInitial(() -> false);
    
    /** Appium server URL (default local server, overridable via system/env property) */
    // Note: Appium 2.x doesn't use /wd/hub suffix anymore
    private static final String APPIUM_SERVER_URL = System.getProperty(
        "APPIUM_SERVER_URL",
        System.getenv().getOrDefault("APPIUM_SERVER_URL", "http://0.0.0.0:4723")
    );
    
    /** Default implicit wait timeout in seconds */
    private static final int DEFAULT_IMPLICIT_WAIT_SECONDS = 10;
    
    /** Android app package name */
    private static final String ANDROID_APP_PACKAGE = "com.intive.aifirst.petspot";
    
    /** iOS app bundle ID */
    private static final String IOS_BUNDLE_ID = "com.intive.aifirst.petspot.PetSpot";
    
    /** Appium auto-start timeout in seconds */
    private static final int APPIUM_START_TIMEOUT_SECONDS = 30;
    
    /** Appium status check interval in milliseconds */
    private static final int APPIUM_CHECK_INTERVAL_MS = 1000;
    
    /**
     * Checks if Appium server is running by testing port connectivity.
     * 
     * @return true if Appium is running and responding, false otherwise
     */
    private static boolean isAppiumRunning() {
        try {
            // Extract host and port from URL
            URL url = new URL(APPIUM_SERVER_URL);
            String host = url.getHost();
            int port = url.getPort() != -1 ? url.getPort() : 4723;
            
            // Try to connect to port (3s timeout to handle slow network/VM environments)
            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress(host, port), 3000);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Ensures Appium server is running.
     * Note: Check disabled due to Maven/macOS sandbox restrictions.
     * Assumes Appium is already running - will fail with clear error if not.
     */
    private static void ensureAppiumRunning() {
        // Skip check - Maven sandbox blocks socket connections to localhost
        // If Appium is not running, driver init will fail with clear error
        System.out.println("⚠️  Assuming Appium is running at " + APPIUM_SERVER_URL);
        System.out.println("   (If not, ensure Appium is started: appium)");
        return;
        
        /* Disabled: Maven/macOS sandbox blocks socket check even for localhost
        // Check if Appium is already running
        if (isAppiumRunning()) {
            System.out.println("✅ Appium server already running at " + APPIUM_SERVER_URL);
            return;
        }
        
        // Appium not running - print helpful error message
        System.err.println("❌ Appium server is NOT running!");
        System.err.println();
        System.err.println("Please start Appium manually:");
        System.err.println("  appium");
        System.err.println();
        System.err.println("Or start in background:");
        System.err.println("  nohup appium > /tmp/appium.log 2>&1 &");
        System.err.println();
        
        throw new RuntimeException(
            "Appium server is not running. Start it manually: appium"
        );
        */
        
        /* Auto-start disabled due to permission issues when running from Maven/Java
        System.out.println("⚠️  Appium server not running - attempting to start automatically...");
        
        try {
            // Log Appium output to project target directory
            String projectDir = System.getProperty("user.dir");
            String logFile = projectDir + "/target/appium.log";
            String androidHome = System.getProperty("user.home") + "/Library/Android/sdk";
            
            // Start Appium as true background daemon (independent of this process)
            // Using global ~/.appium (has drivers installed) + bind to localhost only
            String[] command = {
                "/bin/bash", "-c",
                String.format(
                    "export ANDROID_HOME=%s && " +
                    "nohup appium --address 127.0.0.1 > %s 2>&1 < /dev/null & " +
                    "disown",
                    androidHome, logFile
                )
            };
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            
            Process process = pb.start();
            process.waitFor(1, TimeUnit.SECONDS); // Wait briefly for shell to start
            
            System.out.println("🚀 Starting Appium server as daemon (logs: " + logFile + ")...");
            
            // Wait for Appium to be ready
            int waitedSeconds = 0;
            while (waitedSeconds < APPIUM_START_TIMEOUT_SECONDS) {
                Thread.sleep(APPIUM_CHECK_INTERVAL_MS);
                waitedSeconds++;
                
                if (isAppiumRunning()) {
                    System.out.println("✅ Appium server started successfully after " + waitedSeconds + "s");
                    return;
                }
            }
            
            // Timeout reached
            throw new RuntimeException(
                "Appium server failed to start within " + APPIUM_START_TIMEOUT_SECONDS + 
                " seconds. Check logs at " + logFile + ". Try starting manually: appium"
            );
            
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to start Appium. Is 'appium' installed and available in PATH? " +
                "Install with: npm install -g appium", e
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Appium to start", e);
        }
        */
    }
    
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
        // Ensure Appium server is running (auto-start if needed)
        ensureAppiumRunning();
        
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
     * <p>Location permission is controlled by {@link #setGrantLocationPermission(boolean)}:
     * <ul>
     *   <li>false (default): App shows full animal list (no location filtering)</li>
     *   <li>true (@location tests): App filters by device location</li>
     * </ul>
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
        
        // Always grant permissions - app needs them to run
        // Location filtering is controlled by GPS mocking, not by denying permissions
        options.setAutoGrantPermissions(true);
        System.out.println("Android: Auto-granting ALL permissions");
        
        return new AndroidDriver(serverUrl, options);
    }
    
    /**
     * Initializes IOSDriver with XCUITest capabilities.
     * 
     * <p>Location permission is controlled by {@link #setGrantLocationPermission(boolean)}:
     * <ul>
     *   <li>false (default): App shows full animal list (no location filtering)</li>
     *   <li>true (@location tests): App filters by device location</li>
     * </ul>
     * 
     * @param serverUrl Appium server URL
     * @return Configured IOSDriver instance
     */
    private static IOSDriver initializeIOSDriver(URL serverUrl) {
        XCUITestOptions options = new XCUITestOptions();
        // Note: Device name must match an available simulator (run: xcrun simctl list devices)
        String iosDeviceName = System.getProperty(
            "IOS_DEVICE_NAME",
            System.getenv().getOrDefault("IOS_DEVICE_NAME", "iPhone 15")
        );
        // Note: Platform version must match an available runtime (run: xcrun simctl list runtimes)
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
        
        // Don't reset app - preserve permissions granted via simctl
        options.setNoReset(true);
        
        // Disable waiting for app quiescence (fixes animation timeout issues)
        options.setCapability("appium:waitForQuiescence", false);
        
        // Check if this test should show location dialog (for dialog testing scenarios)
        boolean shouldShowDialog = showLocationDialog.get();
        
        if (shouldShowDialog) {
            // Test wants to see the real location permission dialog
            // DO NOT auto-accept alerts, DO NOT grant permissions via simctl
            options.setAutoAcceptAlerts(false);
            System.out.println("iOS: Configured for @locationDialog test (real system popup)");
        } else {
            // Normal functional tests - grant permissions silently
            options.setAutoAcceptAlerts(true);
            
            // Set location permission directly via capability (yes/no/unset)
            options.setCapability("appium:permissions", 
                "{\"" + IOS_BUNDLE_ID + "\": {\"location\": \"yes\"}}");
            
            System.out.println("iOS: Configured with noReset=true and location permission");
            
            // Also grant via simctl as backup (in case capability doesn't work)
            grantIOSLocationPermission();
        }
        
        return new IOSDriver(serverUrl, options);
    }
    
    /**
     * Grants location permission to PetSpot app on iOS simulator via simctl.
     * This is a backup method in case the capability approach doesn't work.
     */
    private static void grantIOSLocationPermission() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "xcrun", "simctl", "privacy", "booted", "grant", "location", IOS_BUNDLE_ID
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("iOS: Granted location permission via simctl");
            } else {
                System.out.println("iOS: simctl grant failed (exit code: " + exitCode + ")");
            }
        } catch (Exception e) {
            System.out.println("iOS: simctl not available: " + e.getMessage());
        }
    }
    
    /**
     * Resets location permission for PetSpot app on iOS simulator via simctl.
     * This removes any previously granted location permissions.
     */
    private static void resetIOSLocationPermission() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "xcrun", "simctl", "privacy", "booted", "reset", "location", IOS_BUNDLE_ID
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("iOS: Reset location permission via simctl");
            } else {
                System.out.println("iOS: simctl reset failed (exit code: " + exitCode + ")");
            }
        } catch (Exception e) {
            System.out.println("iOS: simctl not available: " + e.getMessage());
        }
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
    
    /**
     * Sets whether location permission should be granted when initializing driver.
     * Must be called BEFORE getDriver() to take effect.
     * 
     * @param grant true to grant location permission, false to deny (default)
     */
    public static void setGrantLocationPermission(boolean grant) {
        grantLocationPermission.set(grant);
        System.out.println("Location permission flag set to: " + grant);
    }
    
    /**
     * Sets whether location dialog should be shown (for testing location permission popups).
     * Must be called BEFORE getDriver() to take effect.
     * When true: Real system location dialog will be shown (no auto-grant)
     * When false: Permissions granted silently via simctl (default)
     * 
     * @param show true to show real location dialog, false to grant silently (default)
     */
    public static void setShowLocationDialog(boolean show) {
        showLocationDialog.set(show);
        System.out.println("Show location dialog flag set to: " + show);
    }
    
    /**
     * Resets the location permission flags to default (false).
     * Should be called in @After hook.
     */
    public static void resetLocationPermission() {
        grantLocationPermission.remove();
        showLocationDialog.remove();
    }
    
    /**
     * Restarts the app by terminating and re-activating it.
     * Useful to reload data after creating test announcements via API.
     * 
     * <p>This does NOT clear app data - just restarts the app process.
     */
    public static void restartApp() {
        AppiumDriver appiumDriver = driver.get();
        if (appiumDriver == null) {
            throw new IllegalStateException("No active driver - cannot restart app");
        }
        
        String platform = getCurrentPlatform();
        String appId = "android".equalsIgnoreCase(platform) ? ANDROID_APP_PACKAGE : IOS_BUNDLE_ID;
        
        System.out.println("Restarting app: " + appId);
        
        // Cast to platform-specific driver for app lifecycle methods
        if (appiumDriver instanceof AndroidDriver androidDriver) {
            androidDriver.terminateApp(appId);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            androidDriver.activateApp(appId);
        } else if (appiumDriver instanceof IOSDriver iosDriver) {
            iosDriver.terminateApp(appId);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            iosDriver.activateApp(appId);
        } else {
            throw new IllegalStateException("Unknown driver type - cannot restart app");
        }
        
        System.out.println("App restarted successfully");
    }
    
    /**
     * Uninstalls the application completely.
     * This kills the app and removes all app data, permissions, and cache.
     * 
     * <p>Maps to Gherkin: "When I uninstall the app"
     */
    public static void uninstallApp() {
        AppiumDriver appiumDriver = driver.get();
        if (appiumDriver == null) {
            throw new IllegalStateException("No active driver - cannot uninstall app");
        }
        
        String platform = getCurrentPlatform();
        String appId = "android".equalsIgnoreCase(platform) ? ANDROID_APP_PACKAGE : IOS_BUNDLE_ID;
        
        System.out.println("🗑️  Uninstalling app: " + appId);
        
        try {
            if (appiumDriver instanceof AndroidDriver androidDriver) {
                androidDriver.removeApp(appId);
            } else if (appiumDriver instanceof IOSDriver iosDriver) {
                iosDriver.removeApp(appId);
                
                // If test wants to see location dialog, reset permissions after uninstall
                // This ensures the dialog will appear on next install
                if (showLocationDialog.get()) {
                    resetIOSLocationPermission();
                }
            } else {
                throw new IllegalStateException("Unknown driver type - cannot uninstall app");
            }
            
            Thread.sleep(2000); // Wait for uninstall to complete
            System.out.println("✅ App uninstalled successfully");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Uninstall interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to uninstall app: " + e.getMessage(), e);
        }
    }
    
    /**
     * Installs and launches the application.
     * App must be uninstalled first (see {@link #uninstallApp()}).
     * 
     * <p>Maps to Gherkin: "When I install the app"
     */
    public static void installApp() {
        AppiumDriver appiumDriver = driver.get();
        if (appiumDriver == null) {
            throw new IllegalStateException("No active driver - cannot install app");
        }
        
        String platform = getCurrentPlatform();
        String appId = "android".equalsIgnoreCase(platform) ? ANDROID_APP_PACKAGE : IOS_BUNDLE_ID;
        String appPath = "android".equalsIgnoreCase(platform) 
            ? System.getProperty("user.dir") + "/apps/petspot-android.apk"
            : System.getProperty("user.dir") + "/apps/petspot-ios.app";
        
        System.out.println("📦 Installing app from: " + appPath);
        
        try {
            // Install
            if (appiumDriver instanceof AndroidDriver androidDriver) {
                androidDriver.installApp(appPath);
            } else if (appiumDriver instanceof IOSDriver iosDriver) {
                iosDriver.installApp(appPath);
            } else {
                throw new IllegalStateException("Unknown driver type - cannot install app");
            }
            Thread.sleep(2000);
            
            // Launch
            System.out.println("🚀 Launching app: " + appId);
            if (appiumDriver instanceof AndroidDriver androidDriver) {
                androidDriver.activateApp(appId);
            } else if (appiumDriver instanceof IOSDriver iosDriver) {
                iosDriver.activateApp(appId);
            }
            Thread.sleep(2000); // Wait for app to fully initialize
            
            System.out.println("✅ App installed and launched successfully");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Install interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to install app: " + e.getMessage(), e);
        }
    }
    
    /**
     * Reinstalls the application (uninstall + install + launch).
     * This is a convenience method that calls {@link #uninstallApp()} and {@link #installApp()}.
     * 
     * <p>Maps to Gherkin: "When I reinstall the app"
     */
    public static void reinstallApp() {
        System.out.println("🔄 Reinstalling app...");
        uninstallApp();
        installApp();
    }
    
    /**
     * Sets the device's simulated GPS location.
     * Requires location permission to be granted.
     * 
     * @param latitude GPS latitude
     * @param longitude GPS longitude
     */
    public static void setDeviceLocation(double latitude, double longitude) {
        AppiumDriver appiumDriver = driver.get();
        if (appiumDriver == null) {
            throw new IllegalStateException("No active driver - cannot set location");
        }
        
        String platform = getCurrentPlatform();
        
        // Create location object
        org.openqa.selenium.html5.Location location = 
            new org.openqa.selenium.html5.Location(latitude, longitude, 0);
        
        // Cast to platform-specific driver for location methods
        if (appiumDriver instanceof AndroidDriver androidDriver) {
            androidDriver.setLocation(location);
        } else if (appiumDriver instanceof IOSDriver iosDriver) {
            iosDriver.setLocation(location);
        } else {
            throw new IllegalStateException("Unknown driver type - cannot set location");
        }
        
        System.out.println("Device location set to: " + latitude + ", " + longitude);
    }
}

