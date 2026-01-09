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

/**
 * Screen Object Model for Fullscreen Interactive Map (Android + iOS).
 * 
 * <p>This class uses dual annotations to support both Android and iOS platforms:
 * <ul>
 *   <li>@AndroidFindBy: UiAutomator2 selectors for Android (testTag attributes)</li>
 *   <li>@iOSXCUITFindBy: XCUITest selectors for iOS (accessibilityIdentifier)</li>
 * </ul>
 * 
 * <h2>Locator Strategy:</h2>
 * <ul>
 *   <li>Android: Uses testTag via accessibility/content-desc</li>
 *   <li>iOS: Uses accessibilityIdentifier</li>
 *   <li>Both platforms use the same test ID pattern: fullscreenMap.element.action</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * AppiumDriver driver = AppiumDriverManager.getDriver("Android");
 * FullscreenMapScreen screen = new FullscreenMapScreen(driver);
 * 
 * screen.waitForMapLoaded(10);
 * screen.tapBackButton();
 * }</pre>
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.mobile.FullscreenMapSteps
 */
public class FullscreenMapScreen {
    
    private final AppiumDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // Element Locators (Dual Annotations)
    // ========================================
    
    /**
     * Map container element (main map view).
     */
    @AndroidFindBy(accessibility = "fullscreenMap.container")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.container")
    private WebElement mapContainer;
    
    /**
     * Header bar with title and back button.
     */
    @AndroidFindBy(accessibility = "fullscreenMap.header")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.header")
    private WebElement header;
    
    /**
     * Back button in top-left corner.
     */
    @AndroidFindBy(accessibility = "fullscreenMap.backButton")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.backButton")
    private WebElement backButton;
    
    /**
     * Title text "Pet Locations".
     */
    @AndroidFindBy(accessibility = "fullscreenMap.title")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.title")
    private WebElement title;
    
    /**
     * Legend showing "Missing" and "Found" colors.
     */
    @AndroidFindBy(accessibility = "fullscreenMap.legend")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.legend")
    private WebElement legend;
    
    /**
     * Loading indicator shown while fetching pins.
     */
    @AndroidFindBy(accessibility = "fullscreenMap.loading")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.loading")
    private WebElement loadingIndicator;
    
    /**
     * Error state view with message.
     */
    @AndroidFindBy(accessibility = "fullscreenMap.error")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.error")
    private WebElement errorView;
    
    /**
     * Retry button shown in error state.
     */
    @AndroidFindBy(accessibility = "fullscreenMap.retryButton")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.retryButton")
    private WebElement retryButton;
    
    /**
     * Pet details popup (bottom sheet).
     */
    @AndroidFindBy(accessibility = "fullscreenMap.petPopup")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.petPopup")
    private WebElement petPopup;
    
    /**
     * Close button on pet popup.
     */
    @AndroidFindBy(accessibility = "fullscreenMap.petPopup.close")
    @iOSXCUITFindBy(accessibility = "fullscreenMap.petPopup.close")
    private WebElement popupCloseButton;
    
    // ========================================
    // Constructor
    // ========================================
    
    /**
     * Initializes the Screen Object with AppiumDriver instance.
     * 
     * @param driver AppiumDriver instance (AndroidDriver or IOSDriver)
     */
    public FullscreenMapScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    // ========================================
    // Wait Methods
    // ========================================
    
    /**
     * Waits for the fullscreen map to be fully loaded (container visible).
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if map loaded within timeout
     */
    public boolean waitForMapLoaded(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(mapContainer));
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
     * Waits for pet popup to appear.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if popup appeared within timeout
     */
    public boolean waitForPetPopup(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(petPopup));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for pet popup to disappear.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if popup disappeared within timeout
     */
    public boolean waitForPetPopupDismissed(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.invisibilityOf(petPopup));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // Screen Actions
    // ========================================
    
    /**
     * Taps the back button to navigate to previous screen.
     */
    public void tapBackButton() {
        backButton.click();
        System.out.println("Tapped back button on fullscreen map");
    }
    
    /**
     * Taps the retry button in error state.
     */
    public void tapRetryButton() {
        retryButton.click();
        System.out.println("Tapped retry button on fullscreen map");
    }
    
    /**
     * Taps the close button on pet popup.
     */
    public void tapPopupCloseButton() {
        popupCloseButton.click();
        System.out.println("Tapped close button on pet popup");
    }
    
    /**
     * Taps a pin by animal ID.
     * 
     * @param animalId ID of the animal whose pin to tap
     * @return true if pin was found and tapped
     */
    public boolean tapPinByAnimalId(String animalId) {
        try {
            String accessibilityId = "fullscreenMap.pin." + animalId;
            WebElement pin = driver.findElement(AppiumBy.accessibilityId(accessibilityId));
            pin.click();
            System.out.println("Tapped pin for animal: " + animalId);
            return true;
        } catch (Exception e) {
            System.out.println("Could not find pin for animal: " + animalId);
            return false;
        }
    }
    
    /**
     * Taps anywhere on the map (to dismiss popup by tapping outside).
     */
    public void tapOnMap() {
        mapContainer.click();
        System.out.println("Tapped on map area");
    }
    
    // ========================================
    // Verification Methods
    // ========================================
    
    /**
     * Checks if the map container is displayed.
     * 
     * @return true if map is visible
     */
    public boolean isMapDisplayed() {
        try {
            return mapContainer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if the header is displayed.
     * 
     * @return true if header is visible
     */
    public boolean isHeaderDisplayed() {
        try {
            return header.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if the back button is displayed.
     * 
     * @return true if back button is visible
     */
    public boolean isBackButtonDisplayed() {
        try {
            return backButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if the title is displayed.
     * 
     * @return true if title is visible
     */
    public boolean isTitleDisplayed() {
        try {
            return title.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the title text.
     * 
     * @return title text or empty string if not found
     */
    public String getTitleText() {
        try {
            return title.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Checks if the legend is displayed.
     * 
     * @return true if legend is visible
     */
    public boolean isLegendDisplayed() {
        try {
            return legend.isDisplayed();
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
     * Checks if retry button is displayed.
     * 
     * @return true if retry button is visible
     */
    public boolean isRetryButtonDisplayed() {
        try {
            return retryButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if pet popup is displayed.
     * 
     * @return true if popup is visible
     */
    public boolean isPetPopupDisplayed() {
        try {
            return petPopup.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if a pin for specific animal is displayed.
     * 
     * @param animalId ID of the animal
     * @return true if pin is visible
     */
    public boolean isPinDisplayed(String animalId) {
        try {
            String accessibilityId = "fullscreenMap.pin." + animalId;
            WebElement pin = driver.findElement(AppiumBy.accessibilityId(accessibilityId));
            return pin.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
