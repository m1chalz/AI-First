package com.intive.aifirst.petspot.e2e.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Screen Object Model template for mobile screens (iOS + Android).
 * 
 * Purpose: Encapsulates the structure and interactions of a specific mobile screen,
 * abstracting Appium API calls and element location logic with dual platform support.
 * 
 * Usage:
 * 1. Copy this template
 * 2. Rename class to match screen name (e.g., PetListScreen)
 * 3. Add dual annotations (@AndroidFindBy + @iOSXCUITFindBy) for all interactive elements
 * 4. Implement methods for user actions (one method per action)
 * 5. Use in Step Definitions by instantiating with AppiumDriver
 * 
 * Best Practices:
 * - ALWAYS use dual annotations (both @AndroidFindBy AND @iOSXCUITFindBy)
 * - Android: Use resourceId matching testTag values (e.g., "screen.element.action")
 * - iOS: Use id matching accessibilityIdentifier values (e.g., "screen.element.action")
 * - One method per user action (tap, swipe, type, verify)
 * - Return values for verification methods (boolean, int, String, List)
 * - Use explicit waits instead of Thread.sleep()
 * - Hide keyboard after text entry on mobile
 * - Handle platform-specific behaviors when necessary
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.mobile Step Definitions that use this Screen Object
 */
public class ScreenObjectTemplate {
    
    private AppiumDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // Element Locators (dual annotations for iOS + Android)
    // ========================================
    
    /**
     * Example button element.
     * Android pattern: new UiSelector().resourceId("screen.element.action")
     * iOS pattern: id = "screen.element.action"
     */
    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"example.button.click\")")
    @iOSXCUITFindBy(id = "example.button.click")
    private WebElement exampleButton;
    
    /**
     * Example input field element.
     */
    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"example.input.field\")")
    @iOSXCUITFindBy(id = "example.input.field")
    private WebElement exampleInput;
    
    /**
     * Example container element (e.g., list, scroll view).
     */
    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"example.container\")")
    @iOSXCUITFindBy(id = "example.container")
    private WebElement exampleContainer;
    
    // ========================================
    // Constructor
    // ========================================
    
    /**
     * Initializes the Screen Object with AppiumDriver and resolves dual annotations.
     * 
     * @param driver Appium driver instance (from AppiumDriverManager)
     */
    public ScreenObjectTemplate(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    // ========================================
    // User Action Methods
    // ========================================
    
    /**
     * Example: Tap action method.
     * Pattern: tapX (e.g., tapAddButton, tapSubmit)
     * Note: Use "tap" for mobile instead of "click"
     */
    public void tapExampleButton() {
        waitForElementClickable(exampleButton, DEFAULT_WAIT_TIMEOUT);
        exampleButton.click(); // Appium uses click() for tap
    }
    
    /**
     * Example: Type/input action method with keyboard handling.
     * Pattern: enterXInY or typeX (e.g., enterTextInSearch, typePetName)
     * 
     * @param text Text to enter in the input field
     */
    public void enterTextInExampleInput(String text) {
        waitForElementVisible(exampleInput, DEFAULT_WAIT_TIMEOUT);
        exampleInput.clear();
        exampleInput.sendKeys(text);
        hideKeyboard(); // Always hide keyboard after text entry
    }
    
    /**
     * Example: Scroll action method.
     * Pattern: scrollToX or swipeX (e.g., scrollToBottom, swipeLeft)
     */
    public void scrollDown() {
        // Platform-agnostic scroll using Appium actions
        int startX = driver.manage().window().getSize().getWidth() / 2;
        int startY = (int) (driver.manage().window().getSize().getHeight() * 0.8);
        int endY = (int) (driver.manage().window().getSize().getHeight() * 0.2);
        
        new io.appium.java_client.TouchAction<>(driver)
            .press(io.appium.java_client.touch.offset.PointOption.point(startX, startY))
            .waitAction(io.appium.java_client.touch.WaitOptions.waitOptions(Duration.ofMillis(500)))
            .moveTo(io.appium.java_client.touch.offset.PointOption.point(startX, endY))
            .release()
            .perform();
    }
    
    // ========================================
    // Verification Methods (return boolean, int, String, List)
    // ========================================
    
    /**
     * Example: Check if element is displayed.
     * Pattern: isXDisplayed or hasX (e.g., isPetListDisplayed, hasErrorMessage)
     * 
     * @return true if element is displayed, false otherwise
     */
    public boolean isExampleButtonDisplayed() {
        try {
            return exampleButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Example: Get count of elements (platform-specific logic if needed).
     * Pattern: getXCount or countX (e.g., getPetCount, countResults)
     * 
     * @return Number of matching elements
     */
    public int getExampleItemCount() {
        List<WebElement> items;
        
        if (isAndroid()) {
            items = driver.findElements(
                AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\"example\\\\.item\\\\..*\")")
            );
        } else {
            items = driver.findElements(
                AppiumBy.iOSClassChain("**/XCUIElementTypeCell[`name BEGINSWITH 'example.item.'`]")
            );
        }
        
        return items.size();
    }
    
    /**
     * Example: Get text from element.
     * Pattern: getXText or retrieveX (e.g., getTitleText, getErrorMessage)
     * 
     * @return Text content of the element
     */
    public String getExampleText() {
        waitForElementVisible(exampleContainer, DEFAULT_WAIT_TIMEOUT);
        return exampleContainer.getText();
    }
    
    /**
     * Example: Get collection of elements (platform-specific).
     * Pattern: getXList or findXElements (e.g., getPetList, getSearchResults)
     * 
     * @return List of WebElements matching the criteria
     */
    public List<WebElement> getExampleItems() {
        if (isAndroid()) {
            return driver.findElements(
                AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\"example\\\\.item\\\\..*\")")
            );
        } else {
            return driver.findElements(
                AppiumBy.iOSClassChain("**/XCUIElementTypeCell[`name BEGINSWITH 'example.item.'`]")
            );
        }
    }
    
    // ========================================
    // Platform Detection Utility Methods (private)
    // ========================================
    
    /**
     * Check if current platform is Android.
     * 
     * @return true if Android, false otherwise
     */
    private boolean isAndroid() {
        return driver.getPlatformName().equalsIgnoreCase("Android");
    }
    
    /**
     * Check if current platform is iOS.
     * 
     * @return true if iOS, false otherwise
     */
    private boolean isIOS() {
        return driver.getPlatformName().equalsIgnoreCase("iOS");
    }
    
    // ========================================
    // Wait/Helper Methods (private)
    // ========================================
    
    /**
     * Wait for element to be visible.
     * 
     * @param element WebElement to wait for
     * @param timeoutSeconds Maximum wait time in seconds
     */
    private void waitForElementVisible(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Wait for element to be clickable.
     * 
     * @param element WebElement to wait for
     * @param timeoutSeconds Maximum wait time in seconds
     */
    private void waitForElementClickable(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    /**
     * Hide mobile keyboard after text entry.
     * Works on both iOS and Android.
     */
    private void hideKeyboard() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (Exception e) {
            // Keyboard already hidden or not supported - ignore
        }
    }
    
    /**
     * Scroll element into view (if needed for off-screen elements).
     * 
     * @param element WebElement to scroll to
     */
    private void scrollToElement(WebElement element) {
        // Appium automatically scrolls to element when interacting, but can be explicit if needed
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
            element
        );
    }
    
    /**
     * Wait for screen to load (useful for screen transitions).
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     */
    public void waitForScreenLoad(int timeoutSeconds) {
        waitForElementVisible(exampleContainer, timeoutSeconds);
    }
}

