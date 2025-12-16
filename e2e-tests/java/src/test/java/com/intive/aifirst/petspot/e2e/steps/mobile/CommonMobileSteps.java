package com.intive.aifirst.petspot.e2e.steps.mobile;

import com.intive.aifirst.petspot.e2e.utils.AppiumDriverManager;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Common step definitions for mobile scenarios (Android + iOS).
 * 
 * <p>This class contains reusable Cucumber step definitions that can be shared
 * across multiple mobile features:
 * <ul>
 *   <li>App launch and navigation steps</li>
 *   <li>Wait and delay steps</li>
 *   <li>Platform detection steps</li>
 *   <li>Common gestures (swipe, tap, scroll)</li>
 * </ul>
 * 
 * <h2>Purpose:</h2>
 * <p>Avoid duplication by centralizing common steps that appear in multiple
 * feature files. Feature-specific steps should go in dedicated step classes
 * (e.g., {@link PetListMobileSteps}).
 * 
 * <h2>Example Usage:</h2>
 * <pre>
 * Given I am using "Android" platform
 * When I wait 2 seconds
 * Then the app should be running
 * </pre>
 * 
 * @see PetListMobileSteps
 * @see com.intive.aifirst.petspot.e2e.utils.AppiumDriverManager
 */
public class CommonMobileSteps {
    
    private AppiumDriver driver;
    
    /**
     * Constructor - initializes AppiumDriver if available.
     */
    public CommonMobileSteps() {
        // Driver will be initialized in specific steps or hooks
    }
    
    // ========================================
    // Platform Detection Steps
    // ========================================
    
    /**
     * Sets the platform for the current test scenario.
     * 
     * <p>Maps to Gherkin: "Given I am using {string} platform"
     * 
     * @param platform Platform name ("Android" or "iOS")
     */
    @Given("I am using {string} platform")
    public void setPlatform(String platform) {
        System.setProperty("PLATFORM", platform);
        System.out.println("Platform set to: " + platform);
    }
    
    /**
     * Verifies the current platform matches expected value.
     * 
     * <p>Maps to Gherkin: "Then the platform should be {string}"
     * 
     * @param expectedPlatform Expected platform name
     */
    @Then("the platform should be {string}")
    public void verifyPlatform(String expectedPlatform) {
        String actualPlatform = AppiumDriverManager.getCurrentPlatform();
        assertNotNull(actualPlatform, "Platform should be detected");
        assertTrue(actualPlatform.equalsIgnoreCase(expectedPlatform),
            "Platform should be " + expectedPlatform + " but was " + actualPlatform);
        System.out.println("Verified: Platform is " + actualPlatform);
    }
    
    // ========================================
    // App State Steps
    // ========================================
    
    /**
     * Verifies that the app is running.
     * 
     * <p>Maps to Gherkin: "Then the app should be running"
     */
    @Then("the app should be running")
    public void appShouldBeRunning() {
        String platform = AppiumDriverManager.getCurrentPlatform();
        assertNotNull(platform, "App should be running with active platform");
        System.out.println("Verified: App is running on " + platform);
    }
    
    /**
     * Closes the current app.
     * 
     * <p>Maps to Gherkin: "When I close the app"
     */
    @When("I close the app")
    public void closeApp() {
        AppiumDriverManager.quitDriver();
        System.out.println("App closed");
    }
    
    /**
     * Relaunches the app (quit and relaunch).
     * 
     * <p>Maps to Gherkin: "When I relaunch the app"
     */
    @When("I relaunch the app")
    public void relaunchApp() {
        String platform = AppiumDriverManager.getCurrentPlatform();
        AppiumDriverManager.quitDriver();
        
        try {
            Thread.sleep(1000); // Wait before relaunch
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        this.driver = AppiumDriverManager.getDriver(platform);
        System.out.println("App relaunched on " + platform);
    }
    
    /**
     * Sends the app to background for specified seconds.
     * 
     * <p>Maps to Gherkin: "When I send the app to background for {int} second(s)"
     * 
     * @param seconds Number of seconds to background the app
     */
    @When("I send the app to background for {int} second(s)")
    public void sendAppToBackground(int seconds) {
        if (driver == null) {
            driver = AppiumDriverManager.getDriver(AppiumDriverManager.getCurrentPlatform());
        }
        try {
            // Appium 9.x: Use platform-specific methods
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).runAppInBackground(java.time.Duration.ofSeconds(seconds));
            } else if (driver instanceof io.appium.java_client.ios.IOSDriver) {
                ((io.appium.java_client.ios.IOSDriver) driver).runAppInBackground(java.time.Duration.ofSeconds(seconds));
            }
            System.out.println("App sent to background for " + seconds + " second(s)");
        } catch (Exception e) {
            System.err.println("Could not background app: " + e.getMessage());
        }
    }
    
    // ========================================
    // Wait Steps
    // ========================================
    
    /**
     * Waits for a specified number of seconds.
     * 
     * <p>Maps to Gherkin: "When I wait {int} second(s)"
     * 
     * <p><strong>Note:</strong> Use explicit waits instead when possible.
     * This step is provided for edge cases where a fixed delay is necessary.
     * 
     * @param seconds Number of seconds to wait
     */
    @When("I wait {int} second(s)")
    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            System.out.println("Waited " + seconds + " second(s)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Wait interrupted: " + e.getMessage());
        }
    }
    
    // ========================================
    // Device Orientation Steps
    // ========================================
    
    /**
     * Rotates the device to landscape orientation.
     * 
     * <p>Maps to Gherkin: "When I rotate to landscape"
     */
    @When("I rotate to landscape")
    public void rotateToLandscape() {
        if (driver == null) {
            driver = AppiumDriverManager.getDriver(AppiumDriverManager.getCurrentPlatform());
        }
        try {
            // Appium 9.x: Use platform-specific methods
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).rotate(org.openqa.selenium.ScreenOrientation.LANDSCAPE);
            } else if (driver instanceof io.appium.java_client.ios.IOSDriver) {
                ((io.appium.java_client.ios.IOSDriver) driver).rotate(org.openqa.selenium.ScreenOrientation.LANDSCAPE);
            }
            System.out.println("Rotated to landscape orientation");
        } catch (Exception e) {
            System.err.println("Could not rotate device: " + e.getMessage());
        }
    }
    
    /**
     * Rotates the device to portrait orientation.
     * 
     * <p>Maps to Gherkin: "When I rotate to portrait"
     */
    @When("I rotate to portrait")
    public void rotateToPortrait() {
        if (driver == null) {
            driver = AppiumDriverManager.getDriver(AppiumDriverManager.getCurrentPlatform());
        }
        try {
            // Appium 9.x: Use platform-specific methods
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).rotate(org.openqa.selenium.ScreenOrientation.PORTRAIT);
            } else if (driver instanceof io.appium.java_client.ios.IOSDriver) {
                ((io.appium.java_client.ios.IOSDriver) driver).rotate(org.openqa.selenium.ScreenOrientation.PORTRAIT);
            }
            System.out.println("Rotated to portrait orientation");
        } catch (Exception e) {
            System.err.println("Could not rotate device: " + e.getMessage());
        }
    }
    
    /**
     * Verifies the current device orientation.
     * 
     * <p>Maps to Gherkin: "Then the orientation should be {string}"
     * 
     * @param expectedOrientation Expected orientation ("landscape" or "portrait")
     */
    @Then("the orientation should be {string}")
    public void verifyOrientation(String expectedOrientation) {
        if (driver == null) {
            driver = AppiumDriverManager.getDriver(AppiumDriverManager.getCurrentPlatform());
        }
        
        try {
            org.openqa.selenium.ScreenOrientation currentOrientation = null;
            // Appium 9.x: Use platform-specific methods
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                currentOrientation = ((io.appium.java_client.android.AndroidDriver) driver).getOrientation();
            } else if (driver instanceof io.appium.java_client.ios.IOSDriver) {
                currentOrientation = ((io.appium.java_client.ios.IOSDriver) driver).getOrientation();
            }
            
            assertNotNull(currentOrientation, "Could not get device orientation");
            assertEquals(expectedOrientation.toUpperCase(), currentOrientation.name(),
                "Orientation should match expected value");
            System.out.println("Verified: Orientation is " + currentOrientation);
        } catch (Exception e) {
            fail("Could not verify orientation: " + e.getMessage());
        }
    }
    
    // ========================================
    // Keyboard Steps
    // ========================================
    
    /**
     * Hides the keyboard if it's visible.
     * 
     * <p>Maps to Gherkin: "When I hide the keyboard"
     */
    @When("I hide the keyboard")
    public void hideKeyboard() {
        if (driver == null) {
            driver = AppiumDriverManager.getDriver(AppiumDriverManager.getCurrentPlatform());
        }
        
        try {
            // Appium 9.x: Use platform-specific methods
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
            } else if (driver instanceof io.appium.java_client.ios.IOSDriver) {
                ((io.appium.java_client.ios.IOSDriver) driver).hideKeyboard();
            }
            System.out.println("Keyboard hidden");
        } catch (Exception e) {
            System.out.println("Keyboard already hidden or not present");
        }
    }
    
    /**
     * Verifies the keyboard is visible.
     * 
     * <p>Maps to Gherkin: "Then the keyboard should be visible"
     */
    @Then("the keyboard should be visible")
    public void keyboardShouldBeVisible() {
        if (driver == null) {
            driver = AppiumDriverManager.getDriver(AppiumDriverManager.getCurrentPlatform());
        }
        
        try {
            boolean isKeyboardShown = false;
            // Appium 9.x: Use platform-specific methods
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                isKeyboardShown = ((io.appium.java_client.android.AndroidDriver) driver).isKeyboardShown();
            } else if (driver instanceof io.appium.java_client.ios.IOSDriver) {
                isKeyboardShown = ((io.appium.java_client.ios.IOSDriver) driver).isKeyboardShown();
            }
            assertTrue(isKeyboardShown, "Keyboard should be visible");
            System.out.println("Verified: Keyboard is visible");
        } catch (Exception e) {
            fail("Could not verify keyboard state: " + e.getMessage());
        }
    }
    
    /**
     * Verifies the keyboard is hidden.
     * 
     * <p>Maps to Gherkin: "Then the keyboard should be hidden"
     */
    @Then("the keyboard should be hidden")
    public void keyboardShouldBeHidden() {
        if (driver == null) {
            driver = AppiumDriverManager.getDriver(AppiumDriverManager.getCurrentPlatform());
        }
        
        try {
            boolean isKeyboardShown = false;
            // Appium 9.x: Use platform-specific methods
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                isKeyboardShown = ((io.appium.java_client.android.AndroidDriver) driver).isKeyboardShown();
            } else if (driver instanceof io.appium.java_client.ios.IOSDriver) {
                isKeyboardShown = ((io.appium.java_client.ios.IOSDriver) driver).isKeyboardShown();
            }
            assertFalse(isKeyboardShown, "Keyboard should be hidden");
            System.out.println("Verified: Keyboard is hidden");
        } catch (Exception e) {
            fail("Could not verify keyboard state: " + e.getMessage());
        }
    }
    
    // ========================================
    // Debug Steps
    // ========================================
    
    /**
     * Prints the current page source (for debugging).
     * 
     * <p>Maps to Gherkin: "When I print the page source"
     */
    @When("I print the page source")
    public void printPageSource() {
        if (driver == null) {
            driver = AppiumDriverManager.getDriver(AppiumDriverManager.getCurrentPlatform());
        }
        
        String pageSource = driver.getPageSource();
        System.out.println("========== PAGE SOURCE ==========");
        System.out.println(pageSource);
        System.out.println("=================================");
    }
    
    /**
     * Prints the current platform name (for debugging).
     * 
     * <p>Maps to Gherkin: "When I print the current platform"
     */
    @When("I print the current platform")
    public void printCurrentPlatform() {
        String platform = AppiumDriverManager.getCurrentPlatform();
        System.out.println("Current platform: " + platform);
    }
}

