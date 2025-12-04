package com.intive.aifirst.petspot.e2e.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Utility class for explicit waits in both web (Selenium) and mobile (Appium) tests.
 * 
 * <p>This class provides reusable wait methods that work with both WebDriver and AppiumDriver:
 * <ul>
 *   <li>Element visibility waits</li>
 *   <li>Element clickability waits</li>
 *   <li>Text appearance waits</li>
 *   <li>Custom condition waits</li>
 * </ul>
 * 
 * <h2>Why Explicit Waits?</h2>
 * <p>Explicit waits are more reliable than implicit waits or Thread.sleep():
 * <ul>
 *   <li>Wait only as long as necessary (faster tests)</li>
 *   <li>Wait for specific conditions to be met (more stable)</li>
 *   <li>Better error messages when timeouts occur</li>
 *   <li>Configurable timeout per wait</li>
 * </ul>
 * 
 * <h2>Usage Example (Web):</h2>
 * <pre>{@code
 * WebDriver driver = WebDriverManager.getDriver();
 * By locator = By.xpath("//*[@data-testid='animalList.reportMissingButton']");
 * 
 * // Wait for element to be visible
 * WebElement button = WaitUtil.waitForElementVisible(driver, locator, 10);
 * button.click();
 * 
 * // Wait for specific text to appear
 * WaitUtil.waitForTextToAppear(driver, locator, "Report a Missing Animal", 5);
 * }</pre>
 * 
 * <h2>Usage Example (Mobile):</h2>
 * <pre>{@code
 * AppiumDriver driver = AppiumDriverManager.getDriver("Android");
 * By locator = AppiumBy.accessibilityId("animalList.reportMissingButton");
 * 
 * // Wait for element to be clickable
 * WebElement button = WaitUtil.waitForElementClickable(driver, locator, 10);
 * button.click();
 * 
 * // Wait for element to disappear
 * WaitUtil.waitForElementInvisible(driver, locator, 5);
 * }</pre>
 * 
 * @see WebDriverWait
 * @see ExpectedConditions
 */
public class WaitUtil {
    
    /** Default timeout for explicit waits (seconds) */
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    
    // ========================================
    // Element Visibility Waits
    // ========================================
    
    /**
     * Waits for an element to be visible on the page/screen.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator (By.xpath, By.id, AppiumBy.accessibilityId, etc.)
     * @param timeoutSeconds Maximum wait time in seconds
     * @return WebElement once it becomes visible
     * @throws org.openqa.selenium.TimeoutException if element not visible within timeout
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * Waits for an element to be visible (using default timeout).
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @return WebElement once it becomes visible
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator) {
        return waitForElementVisible(driver, locator, DEFAULT_TIMEOUT_SECONDS);
    }
    
    /**
     * Waits for an already-found element to be visible.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param element WebElement to wait for
     * @param timeoutSeconds Maximum wait time in seconds
     * @return WebElement once it becomes visible
     */
    public static WebElement waitForElementVisible(WebDriver driver, WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Waits for an element to become invisible (hidden or removed from DOM).
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if element became invisible, false if still visible
     */
    public static boolean waitForElementInvisible(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    // ========================================
    // Element Interactivity Waits
    // ========================================
    
    /**
     * Waits for an element to be clickable (visible AND enabled).
     * 
     * <p>This is the recommended wait before clicking elements, as it ensures
     * the element is both visible and enabled (not disabled or obscured).
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @param timeoutSeconds Maximum wait time in seconds
     * @return WebElement once it becomes clickable
     */
    public static WebElement waitForElementClickable(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    /**
     * Waits for an element to be clickable (using default timeout).
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @return WebElement once it becomes clickable
     */
    public static WebElement waitForElementClickable(WebDriver driver, By locator) {
        return waitForElementClickable(driver, locator, DEFAULT_TIMEOUT_SECONDS);
    }
    
    /**
     * Waits for an already-found element to be clickable.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param element WebElement to wait for
     * @param timeoutSeconds Maximum wait time in seconds
     * @return WebElement once it becomes clickable
     */
    public static WebElement waitForElementClickable(WebDriver driver, WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    // ========================================
    // Text and Attribute Waits
    // ========================================
    
    /**
     * Waits for specific text to appear in an element.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @param expectedText Text to wait for
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if text appeared, false otherwise
     */
    public static boolean waitForTextToAppear(WebDriver driver, By locator, String expectedText, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }
    
    /**
     * Waits for an element's text to match expected value.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @param expectedText Exact text to match
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if text matches, false otherwise
     */
    public static boolean waitForTextEquals(WebDriver driver, By locator, String expectedText, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.textToBe(locator, expectedText));
    }
    
    /**
     * Waits for an element to have a specific attribute value.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @param attribute Attribute name (e.g., "class", "value", "disabled")
     * @param expectedValue Expected attribute value
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if attribute matches, false otherwise
     */
    public static boolean waitForAttributeContains(WebDriver driver, By locator, String attribute, 
                                                    String expectedValue, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.attributeContains(locator, attribute, expectedValue));
    }
    
    // ========================================
    // Presence and Count Waits
    // ========================================
    
    /**
     * Waits for an element to be present in the DOM (may not be visible).
     * 
     * <p>Use this when you need to verify an element exists in the DOM,
     * regardless of visibility (e.g., hidden elements, elements off-screen).
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @param timeoutSeconds Maximum wait time in seconds
     * @return WebElement once it's present in DOM
     */
    public static WebElement waitForElementPresent(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    /**
     * Waits for a specific number of elements to be present.
     * 
     * <p>Useful for waiting for lists to load (e.g., "wait for at least 5 pets to appear").
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @param expectedCount Expected number of elements
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if expected count reached
     */
    public static boolean waitForElementCount(WebDriver driver, By locator, int expectedCount, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> d.findElements(locator).size() == expectedCount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for at least a minimum number of elements to be present.
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param locator Element locator
     * @param minCount Minimum number of elements expected
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if minimum count reached
     */
    public static boolean waitForElementCountAtLeast(WebDriver driver, By locator, int minCount, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            wait.until(d -> d.findElements(locator).size() >= minCount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // Custom Condition Waits
    // ========================================
    
    /**
     * Waits for a custom condition to be true.
     * 
     * <p>Use this for complex wait conditions that aren't covered by standard methods.
     * 
     * <h2>Example:</h2>
     * <pre>{@code
     * // Wait for page title to contain specific text
     * WaitUtil.waitForCondition(driver, 10, 
     *     d -> d.getTitle().contains("Pet List")
     * );
     * 
     * // Wait for element count to be greater than 5
     * WaitUtil.waitForCondition(driver, 10,
     *     d -> d.findElements(By.xpath("//div[@class='pet']")).size() > 5
     * );
     * }</pre>
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param timeoutSeconds Maximum wait time in seconds
     * @param condition Custom condition function that returns boolean
     * @return Result of the condition once it becomes true
     * @param <T> Return type of the condition function
     */
    public static <T> T waitForCondition(WebDriver driver, int timeoutSeconds, 
                                         Function<WebDriver, T> condition) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(condition);
    }
    
    /**
     * Waits for a custom condition (using default timeout).
     * 
     * @param driver WebDriver or AppiumDriver instance
     * @param condition Custom condition function
     * @return Result of the condition once it becomes true
     * @param <T> Return type of the condition function
     */
    public static <T> T waitForCondition(WebDriver driver, Function<WebDriver, T> condition) {
        return waitForCondition(driver, DEFAULT_TIMEOUT_SECONDS, condition);
    }
    
    // ========================================
    // Mobile-Specific Waits (Appium)
    // ========================================
    
    /**
     * Waits for an element with accessibility ID to be visible (mobile-specific).
     * 
     * <p>Convenience method for mobile tests using accessibility identifiers.
     * 
     * @param driver AppiumDriver instance
     * @param accessibilityId Accessibility identifier
     * @param timeoutSeconds Maximum wait time in seconds
     * @return WebElement once it becomes visible
     */
    public static WebElement waitForAccessibilityIdVisible(AppiumDriver driver, String accessibilityId, 
                                                           int timeoutSeconds) {
        By locator = io.appium.java_client.AppiumBy.accessibilityId(accessibilityId);
        return waitForElementVisible(driver, locator, timeoutSeconds);
    }
    
    /**
     * Waits for keyboard to be hidden (mobile-specific).
     * 
     * @param driver AppiumDriver instance (AndroidDriver or IOSDriver)
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if keyboard hidden, false if still visible
     */
    public static boolean waitForKeyboardHidden(AppiumDriver driver, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(d -> {
            try {
                if (d instanceof io.appium.java_client.android.AndroidDriver) {
                    return !((io.appium.java_client.android.AndroidDriver) d).isKeyboardShown();
                } else if (d instanceof io.appium.java_client.ios.IOSDriver) {
                    return !((io.appium.java_client.ios.IOSDriver) d).isKeyboardShown();
                }
                return true; // Unknown platform, assume keyboard hidden
            } catch (Exception e) {
                return true; // If check fails, assume keyboard hidden
            }
        });
    }
}

