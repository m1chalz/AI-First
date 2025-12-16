package com.intive.aifirst.petspot.e2e.utils;

import io.appium.java_client.AppiumDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber hooks for test lifecycle management.
 * 
 * <p>This class provides setup (@Before) and teardown (@After) hooks that run
 * automatically before and after each Cucumber scenario:
 * <ul>
 *   <li>@Before: Scenario initialization (currently placeholder)</li>
 *   <li>@After: Driver cleanup and screenshot capture on failure</li>
 * </ul>
 * 
 * <h2>Hook Execution Order:</h2>
 * <pre>
 * 1. @Before hook (setup)
 * 2. Scenario execution (Given/When/Then steps)
 * 3. @After hook (teardown, screenshot capture if failed, driver quit)
 * </pre>
 * 
 * <h2>Screenshot Capture:</h2>
 * <p>When a scenario fails, a screenshot is automatically captured and saved to
 * {@code target/screenshots/} with the scenario name and timestamp.
 * 
 * <h2>Example Output:</h2>
 * <pre>
 * Scenario: View pet list on web
 *   Given I am on the pet list page
 *   When I view the pet list
 *   Then I should see at least one pet   [FAILED]
 * 
 * Screenshot saved: target/screenshots/view-pet-list_2025-11-26_08-30-15.png
 * Quit WebDriver for scenario: View pet list on web
 * </pre>
 * 
 * @see io.cucumber.java.Before
 * @see io.cucumber.java.After
 * @see io.cucumber.java.Scenario
 */
public class Hooks {
    
    /**
     * Ensures Docker is running for web tests.
     * Executes with highest priority (-300) before any other setup.
     * 
     * <p>Web tests require Docker containers (qa-backend, qa-frontend).
     * This hook verifies Docker is running and throws a helpful error if not.
     * 
     * <p><strong>Web only:</strong> Mobile tests use local backend (no Docker needed).
     * 
     * @param scenario Cucumber scenario being executed
     * @throws RuntimeException if Docker is not running (with instructions how to start it)
     */
    @Before(order = -300)
    public void ensureDockerForWebTests(Scenario scenario) {
        // Only check Docker for web tests
        if (scenario.getSourceTagNames().stream().anyMatch(tag -> tag.equals("@web"))) {
            DockerChecker.ensureDockerRunning();
            
            // Auto-start QA environment if needed
            if (!DockerChecker.autoStartQaEnvironment()) {
                throw new RuntimeException(
                    "\n\n" +
                    "❌ Failed to start QA environment!\n" +
                    "\n" +
                    "Please start manually:\n" +
                    "  cd e2e-tests\n" +
                    "  docker-compose -f docker-compose.qa-env.yml up -d\n" +
                    "\n"
                );
            }
        }
    }
    
    /**
     * Ensures backend server is running before mobile tests.
     * Executes with highest priority to ensure backend is available for mobile apps.
     * 
     * <p>Automatically checks if backend is running at http://localhost:3000.
     * If not running, starts it using {@code npm run dev} in the background.
     * 
     * <p><strong>Mobile only:</strong> Mobile tests need local backend (127.0.0.1:3000).
     * Web tests use Docker QA environment (docker-compose.qa-env.yml).
     * 
     * <p>Backend logs are written to {@code target/backend.log}.
     */
    @Before(order = -200)
    public void ensureBackendRunning(Scenario scenario) {
        // Only auto-start backend for mobile tests
        // Web tests use Docker QA environment (docker-compose.qa-env.yml)
        if (scenario.getSourceTagNames().stream().anyMatch(tag -> 
                tag.equals("@ios") || tag.equals("@android") || tag.equals("@mobile"))) {
            BackendManager.ensureBackendRunning();
        }
    }
    
    /**
     * Ensures apps are built before any test runs.
     * Executes once at the start of test suite with high priority.
     * 
     * <p>Automatically:
     * <ul>
     *   <li>Uninstalls old app from devices/simulators</li>
     *   <li>Builds fresh iOS and Android apps (in parallel)</li>
     *   <li>Copies apps to e2e-tests/java/apps/ directory</li>
     * </ul>
     * 
     * <p><strong>Mobile only:</strong> Web tests don't need mobile apps.
     * Platform is determined by checking the test runner class name.
     * 
     * <p>Set {@code -Dskip.app.build=true} to skip building (use existing apps).
     */
    @Before(order = -100)
    public void prepareApps(Scenario scenario) {
        // Check if app build should be skipped (set by WebTestRunner or via -Dskip.app.build=true)
        String skipBuild = System.getProperty("skip.app.build");
        if ("true".equals(skipBuild)) {
            System.out.println("✅ skip.app.build=true - skipping mobile app build");
            return;
        }
        
        // Mobile tests - detect platform from scenario
        String platform = detectPlatformFromScenario(scenario);
        
        System.out.println("📱 Building apps for platform: " + platform);
        
        // Build apps (runs only once per test run - singleton)
        AppBuilder.ensureAppsBuilt(platform);
    }
    
    /**
     * Executes before each Cucumber scenario.
     * 
     * <p>Performs setup including platform detection from Cucumber tags.
     * Driver initialization happens lazily in step definitions via
     * {@link WebDriverManager#getDriver()} or {@link AppiumDriverManager#getDriver(String)}.
     * 
     * @param scenario Cucumber scenario being executed
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println("========================================");
        System.out.println("Starting scenario: " + scenario.getName());
        System.out.println("Tags: " + scenario.getSourceTagNames());
        System.out.println("========================================");
        
        // Reset debug screenshot counter for this scenario
        DebugScreenshotHelper.reset();
        
        // Clear soft assertion failures from previous scenario
        SoftAssertContext.clear();
        
        // Detect platform from Cucumber tags and set as system property
        detectAndSetPlatform(scenario);
        
        // Detect @location tag and configure Appium permissions
        detectLocationTag(scenario);
    }
    
    /**
     * Detects platform from scenario tags without setting system property.
     * Used by AppBuilder to determine which app(s) to build.
     * 
     * @param scenario Cucumber scenario with tags
     * @return Platform name ("iOS", "Android", or null for both/web)
     */
    private String detectPlatformFromScenario(Scenario scenario) {
        var tags = scenario.getSourceTagNames();
        
        // Check external PLATFORM setting first
        String externalPlatform = System.getProperty("PLATFORM");
        if (externalPlatform != null && !externalPlatform.isEmpty()) {
            return externalPlatform;
        }
        
        boolean hasIos = tags.contains("@ios");
        boolean hasAndroid = tags.contains("@android");
        boolean hasWeb = tags.contains("@web");
        
        // Web-only scenario - no mobile app needed
        if (hasWeb && !hasIos && !hasAndroid) {
            return "Web";
        }
        
        // iOS-only scenario
        if (hasIos && !hasAndroid) {
            return "iOS";
        }
        
        // Android-only scenario
        if (hasAndroid && !hasIos) {
            return "Android";
        }
        
        // Cross-platform or unknown - build both
        return null;
    }
    
    /**
     * Detects @location and @locationDialog tags and configures Appium accordingly.
     * 
     * <p>By default, location permission is DENIED so app shows full animal list.
     * When @location tag is present, permission is GRANTED for location filtering tests.
     * When @locationDialog tag is present, permission is NOT auto-granted (shows real system popup).
     * 
     * @param scenario Cucumber scenario with tags
     */
    private void detectLocationTag(Scenario scenario) {
        var tags = scenario.getSourceTagNames();
        boolean hasLocationTag = tags.contains("@location");
        boolean hasLocationDialogTag = tags.contains("@locationDialog");
        
        // @locationDialog tag overrides @location tag (for testing permission dialogs)
        if (hasLocationDialogTag) {
            AppiumDriverManager.setGrantLocationPermission(false);
            AppiumDriverManager.setShowLocationDialog(true);
            System.out.println("@locationDialog tag detected - will show real location permission popup");
        } else {
            AppiumDriverManager.setGrantLocationPermission(hasLocationTag);
            AppiumDriverManager.setShowLocationDialog(false);
            
            if (hasLocationTag) {
                System.out.println("@location tag detected - will grant location permission silently");
            } else {
                System.out.println("No @location tag - location permission will be denied (full list)");
            }
        }
    }
    
    /**
     * Detects platform (Android/iOS/Web) from test runner or Cucumber scenario tags.
     * 
     * <p>Priority:
     * <ol>
     *   <li>If PLATFORM system property already set (from -DPLATFORM=xxx) → use it</li>
     *   <li>If scenario has ONLY @ios tag (not @android) → iOS</li>
     *   <li>If scenario has ONLY @android tag (not @ios) → Android</li>
     *   <li>If scenario has BOTH @ios and @android → use runner context (from system property)</li>
     *   <li>If @mobile tag without @ios/@android → defaults to Android</li>
     *   <li>If @web tag only → Web (clears PLATFORM)</li>
     * </ol>
     * 
     * <p>For cross-platform scenarios (@web @ios @android), the TestRunner sets PLATFORM
     * via its configuration, so we honor that setting.
     * 
     * @param scenario Cucumber scenario with tags
     */
    private void detectAndSetPlatform(Scenario scenario) {
        var tags = scenario.getSourceTagNames();
        
        // Check if PLATFORM was set externally (e.g., from TestRunner or -DPLATFORM)
        String externalPlatform = System.getProperty("PLATFORM");
        if (externalPlatform != null && !externalPlatform.isEmpty()) {
            System.out.println("Platform from system property: " + externalPlatform);
            return; // Honor external setting
        }
        
        boolean hasIos = tags.contains("@ios");
        boolean hasAndroid = tags.contains("@android");
        boolean hasWeb = tags.contains("@web");
        boolean hasMobile = tags.contains("@mobile");
        
        // Cross-platform scenario (has both @ios and @android) - detect from runner
        if (hasIos && hasAndroid) {
            // Detect platform from runner class name in Maven test parameter
            String testRunner = System.getProperty("test");
            if (testRunner != null) {
                if (testRunner.toLowerCase().contains("ios")) {
                    System.setProperty("PLATFORM", "iOS");
                    System.out.println("Platform detected from runner: iOS (test=" + testRunner + ")");
                } else if (testRunner.toLowerCase().contains("android")) {
                    System.setProperty("PLATFORM", "Android");
                    System.out.println("Platform detected from runner: Android (test=" + testRunner + ")");
                } else {
                    System.out.println("Platform: Cross-platform scenario, unknown runner: " + testRunner);
                }
            } else {
                System.out.println("Platform: Cross-platform scenario (no -Dtest runner specified)");
            }
        } else if (hasIos && !hasAndroid) {
            // iOS-only scenario
            System.setProperty("PLATFORM", "iOS");
            System.out.println("Platform detected from tags: iOS");
        } else if (hasAndroid && !hasIos) {
            // Android-only scenario
            System.setProperty("PLATFORM", "Android");
            System.out.println("Platform detected from tags: Android");
        } else if (hasMobile) {
            // Mobile but no specific platform - default to Android
            System.setProperty("PLATFORM", "Android");
            System.out.println("Platform defaulted to: Android (mobile tag without ios/android)");
        } else if (hasWeb) {
            // Web test - clear platform
            System.clearProperty("PLATFORM");
            System.out.println("Platform: Web (not mobile)");
        }
        // If none of the above, leave PLATFORM as-is (may be set externally)
    }
    
    /**
     * Executes after each Cucumber scenario.
     * 
     * <p>Performs cleanup and failure handling:
     * <ol>
     *   <li>If scenario failed: Capture screenshot for debugging</li>
     *   <li>Quit WebDriver/AppiumDriver to free resources</li>
     *   <li>Log scenario completion status</li>
     * </ol>
     * 
     * <p>This hook handles both web and mobile tests automatically by detecting
     * which driver manager has an active driver instance.
     * 
     * @param scenario Cucumber scenario that just completed
     */
    @After
    public void afterScenario(Scenario scenario) {
        // Store soft failures before clearing
        boolean hadSoftFailures = SoftAssertContext.hasFailures();
        String softFailureSummary = hadSoftFailures ? SoftAssertContext.getFailureSummary() : null;
        
        try {
            // Report soft assertion failures (if any)
            if (hadSoftFailures) {
                System.err.println(softFailureSummary);
                scenario.log(softFailureSummary);
                // Attach as test output for reporting
                scenario.attach(softFailureSummary.getBytes(), "text/plain", "Soft Assertion Failures");
            }
            
            // Capture screenshot if scenario failed
            if (scenario.isFailed()) {
                System.err.println("Scenario FAILED: " + scenario.getName());
                captureFailureScreenshot(scenario);
            } else if (hadSoftFailures) {
                System.err.println("Scenario FAILED (soft assertions): " + scenario.getName());
                captureFailureScreenshot(scenario);
            } else {
                System.out.println("Scenario PASSED: " + scenario.getName());
            }
            
        } finally {
            // Clear soft assertions for next scenario
            SoftAssertContext.clear();
            
            // Cleanup test data created via API (ensures cleanup even on failure)
            cleanupTestData();
            
            // Reset location permission flag for next scenario
            AppiumDriverManager.resetLocationPermission();
            
            // Always quit drivers to prevent resource leaks
            quitAllDrivers(scenario);
            
            System.out.println("========================================");
            System.out.println("Finished scenario: " + scenario.getName());
            System.out.println("Status: " + (hadSoftFailures ? "FAILED (soft assertions)" : scenario.getStatus()));
            System.out.println("========================================");
            
            // FAIL the scenario if there were soft assertion failures
            // This must be at the very end, after all cleanup
            if (hadSoftFailures) {
                throw new AssertionError("Soft assertion failures:\n" + softFailureSummary);
            }
        }
    }
    
    /**
     * Cleans up any test data created during the scenario via API.
     * This ensures announcements created for testing are deleted even if the test fails.
     */
    private void cleanupTestData() {
        try {
            TestDataApiHelper.cleanupAllCreatedAnnouncements();
        } catch (Exception e) {
            System.err.println("Failed to cleanup test data: " + e.getMessage());
            // Don't throw - cleanup failure should not mask original test failure
        }
    }
    
    /**
     * Captures screenshot on test failure for debugging.
     * Tries both WebDriver and AppiumDriver (whichever is active).
     * 
     * @param scenario Failed scenario
     */
    private void captureFailureScreenshot(Scenario scenario) {
        try {
            // Try web driver first
            WebDriver webDriver = getWebDriverSafely();
            if (webDriver != null) {
                ScreenshotUtil.captureScreenshot(webDriver, scenario.getName());
                return;
            }
            
            // Try mobile driver (check both Android and iOS)
            AppiumDriver appiumDriver = getAppiumDriverSafely();
            if (appiumDriver != null) {
                String platform = AppiumDriverManager.getCurrentPlatform();
                ScreenshotUtil.captureScreenshot(appiumDriver, scenario.getName(), platform);
                return;
            }
            
            System.err.println("No active driver found - cannot capture screenshot");
            
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            // Don't throw - screenshot failure should not cause secondary test failure
        }
    }
    
    /**
     * Quits all active drivers (web and mobile).
     * 
     * @param scenario Scenario being cleaned up
     */
    private void quitAllDrivers(Scenario scenario) {
        try {
            // Quit web driver if active
            if (getWebDriverSafely() != null) {
                WebDriverManager.quitDriver();
                System.out.println("Quit WebDriver for scenario: " + scenario.getName());
            }
            
            // Quit mobile driver if active
            if (getAppiumDriverSafely() != null) {
                AppiumDriverManager.quitDriver();
                System.out.println("Quit AppiumDriver for scenario: " + scenario.getName());
            }
            
        } catch (Exception e) {
            System.err.println("Error quitting drivers: " + e.getMessage());
            // Continue cleanup even if quit fails
        }
    }
    
    /**
     * Safely gets WebDriver instance without throwing if not initialized.
     * 
     * @return WebDriver instance or null if not active
     */
    private WebDriver getWebDriverSafely() {
        try {
            // Use hasDriver() to check without triggering initialization
            return WebDriverManager.hasDriver() ? WebDriverManager.getDriver() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Safely gets AppiumDriver instance without throwing if not initialized.
     * 
     * @return AppiumDriver instance or null if not active
     */
    private AppiumDriver getAppiumDriverSafely() {
        try {
            // AppiumDriverManager has getCurrentPlatform() which returns null if no driver
            return AppiumDriverManager.getCurrentPlatform() != null ? 
                AppiumDriverManager.getDriver(AppiumDriverManager.getCurrentPlatform()) : null;
        } catch (Exception e) {
            return null;
        }
    }
}

