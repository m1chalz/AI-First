package com.intive.aifirst.petspot.e2e.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
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
 * Screen Object Model for Landing Page (Home Tab) (Android + iOS).
 * 
 * <p>This class uses dual annotations to support both Android and iOS platforms:
 * <ul>
 *   <li>@AndroidFindBy: UiAutomator2 selectors for Android (testTag attributes)</li>
 *   <li>@iOSXCUITFindBy: XCUITest selectors for iOS (accessibilityIdentifier)</li>
 * </ul>
 * 
 * <h2>Locator Strategy:</h2>
 * <ul>
 *   <li>Android: {@code new UiSelector().resourceId("landingPage.element.action")}</li>
 *   <li>iOS: {@code accessibilityIdentifier = "landingPage.element.action"}</li>
 *   <li>Both platforms use the same test ID pattern for consistency</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * AppiumDriver driver = AppiumDriverManager.getDriver("iOS");
 * LandingPageScreen screen = new LandingPageScreen(driver);
 * 
 * screen.waitForPageLoaded(10);
 * int count = screen.getAnnouncementCardCount();
 * screen.tapFirstAnnouncementCard();
 * }</pre>
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.mobile.LandingPageSteps
 */
public class LandingPageScreen {
    
    private final AppiumDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // Element Locators (Dual Annotations)
    // ========================================
    
    /**
     * Landing page list container element (scrollable list).
     * Contains announcement cards with recent pets.
     */
    @AndroidFindBy(accessibility = "landingPage.list")
    @iOSXCUITFindBy(accessibility = "landingPage.list")
    private WebElement announcementList;
    
    /**
     * Loading indicator shown while fetching announcements.
     */
    @AndroidFindBy(accessibility = "landingPage.loading")
    @iOSXCUITFindBy(accessibility = "landingPage.loading")
    private WebElement loadingIndicator;
    
    /**
     * Error view shown when backend is unavailable.
     */
    @AndroidFindBy(accessibility = "landingPage.error")
    @iOSXCUITFindBy(accessibility = "landingPage.error")
    private WebElement errorView;
    
    /**
     * Empty state view shown when no announcements exist.
     */
    @AndroidFindBy(accessibility = "landingPage.emptyState")
    @iOSXCUITFindBy(accessibility = "landingPage.emptyState")
    private WebElement emptyStateView;
    
    // ========================================
    // Constructor
    // ========================================
    
    /**
     * Initializes the Screen Object with AppiumDriver instance.
     * 
     * @param driver AppiumDriver instance (AndroidDriver or IOSDriver)
     */
    public LandingPageScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    // ========================================
    // Wait Methods
    // ========================================
    
    /**
     * Waits for the landing page to be fully loaded (list visible).
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if page loaded within timeout
     */
    public boolean waitForPageLoaded(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(announcementList));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for loading indicator to appear.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if loading indicator appeared
     */
    public boolean waitForLoadingIndicator(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(loadingIndicator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for error view to appear.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if error view appeared
     */
    public boolean waitForErrorView(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(errorView));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for empty state view to appear.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if empty state view appeared
     */
    public boolean waitForEmptyState(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(emptyStateView));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // Screen Actions
    // ========================================
    
    /**
     * Taps on the first announcement card in the list.
     * Used for navigation to pet details screen.
     * 
     * @throws RuntimeException if no announcement cards are found
     */
    public void tapFirstAnnouncementCard() {
        List<WebElement> cards = getAnnouncementCards();
        if (!cards.isEmpty()) {
            cards.get(0).click();
            System.out.println("Tapped first announcement card on landing page");
        } else {
            throw new RuntimeException("No announcement cards found on landing page");
        }
    }
    
    /**
     * Taps on an announcement card at specific index (0-based).
     * 
     * @param index Index of the card to tap
     * @throws RuntimeException if index is out of bounds
     */
    public void tapAnnouncementCardAtIndex(int index) {
        List<WebElement> cards = getAnnouncementCards();
        if (index < cards.size()) {
            cards.get(index).click();
            System.out.println("Tapped announcement card at index " + index);
        } else {
            throw new RuntimeException("Card index " + index + " out of bounds. Total cards: " + cards.size());
        }
    }
    
    /**
     * Taps retry button in error view.
     * 
     * @throws RuntimeException if retry button is not found
     */
    public void tapRetryButton() {
        try {
            // Find retry button within error view
            WebElement retryButton;
            String platformName = driver.getCapabilities().getPlatformName().toString().toLowerCase();
            
            if (platformName.contains("android")) {
                retryButton = driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiSelector().text(\"Retry\").enabled(true)"
                ));
            } else {
                // iOS: Look for retry button by label
                retryButton = driver.findElement(AppiumBy.accessibilityId("retry.button"));
            }
            
            retryButton.click();
            System.out.println("Tapped retry button");
        } catch (Exception e) {
            throw new RuntimeException("Retry button not found: " + e.getMessage());
        }
    }
    
    // ========================================
    // Verification Methods
    // ========================================
    
    /**
     * Checks if the announcement list is displayed.
     * 
     * @return true if list is visible
     */
    public boolean isAnnouncementListDisplayed() {
        try {
            return announcementList.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if loading indicator is displayed.
     * 
     * @return true if loading indicator is visible
     */
    public boolean isLoadingIndicatorDisplayed() {
        try {
            return loadingIndicator.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if error view is displayed.
     * 
     * @return true if error view is visible
     */
    public boolean isErrorViewDisplayed() {
        try {
            return errorView.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if empty state view is displayed.
     * 
     * @return true if empty state view is visible
     */
    public boolean isEmptyStateDisplayed() {
        try {
            return emptyStateView.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the count of visible announcement cards.
     * 
     * @return Number of announcement cards displayed
     */
    public int getAnnouncementCardCount() {
        return getAnnouncementCards().size();
    }
    
    /**
     * Checks if any announcement cards are displayed.
     * 
     * @return true if one or more cards are visible
     */
    public boolean hasAnyAnnouncementCards() {
        return getAnnouncementCardCount() > 0;
    }
    
    /**
     * Verifies that exactly N announcement cards are displayed.
     * 
     * @param expectedCount Expected number of cards
     * @return true if count matches
     */
    public boolean hasExactlyNCards(int expectedCount) {
        int actualCount = getAnnouncementCardCount();
        if (actualCount != expectedCount) {
            System.out.println("Expected " + expectedCount + " cards, but found " + actualCount);
        }
        return actualCount == expectedCount;
    }
    
    /**
     * Checks if element with specific accessibility ID is displayed.
     * 
     * @param accessibilityId Accessibility identifier to find
     * @return true if element is visible
     */
    public boolean isElementWithAccessibilityIdDisplayed(String accessibilityId) {
        try {
            WebElement element = driver.findElement(AppiumBy.accessibilityId(accessibilityId));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // Helper Methods
    // ========================================
    
    /**
     * Gets all announcement card elements from the landing page.
     * Uses platform-specific selectors based on accessibility ID pattern.
     * 
     * @return List of WebElements representing announcement cards
     */
    private List<WebElement> getAnnouncementCards() {
        String platformName = driver.getCapabilities().getPlatformName().toString().toLowerCase();
        
        if (platformName.contains("android")) {
            // Android: Find by accessibility ID pattern
            return driver.findElements(
                AppiumBy.xpath("//*[contains(@content-desc, 'animalList.item.') or contains(@content-desc, 'landingPage.item.')]")
            );
        } else {
            // iOS: Pattern landingPage.item.{id} or animalList.item.{id}
            // Announcement cards reuse the animalList.item pattern from AnnouncementCardView
            return driver.findElements(
                AppiumBy.xpath("//*[contains(@name, 'animalList.item.')]")
            );
        }
    }
}

