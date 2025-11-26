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
     * Executes before each Cucumber scenario.
     * 
     * <p>Currently a placeholder for future setup logic (e.g., logging, database reset).
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
        
        // Future: Add setup logic here (e.g., test data preparation)
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
        try {
            // Capture screenshot if scenario failed
            if (scenario.isFailed()) {
                System.err.println("Scenario FAILED: " + scenario.getName());
                captureFailureScreenshot(scenario);
            } else {
                System.out.println("Scenario PASSED: " + scenario.getName());
            }
            
        } finally {
            // Always quit drivers to prevent resource leaks
            quitAllDrivers(scenario);
            
            System.out.println("========================================");
            System.out.println("Finished scenario: " + scenario.getName());
            System.out.println("Status: " + scenario.getStatus());
            System.out.println("========================================");
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
            // WebDriverManager returns null if driver not initialized (ThreadLocal.get())
            // We need to check without triggering initialization
            return WebDriverManager.getDriver();
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

