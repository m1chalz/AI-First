package com.intive.aifirst.petspot.e2e.screens;

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
 * Screen Object Model for Report Created Confirmation / Summary screen (Android + iOS).
 * 
 * <p>This class uses dual annotations to support both Android and iOS platforms:
 * <ul>
 *   <li>@AndroidFindBy: UiAutomator2 selectors for Android (testTag attributes)</li>
 *   <li>@iOSXCUITFindBy: XCUITest selectors for iOS (accessibilityIdentifier)</li>
 * </ul>
 * 
 * <h2>Locator Strategy:</h2>
 * <ul>
 *   <li>Android: {@code accessibility = "summary.element"}</li>
 *   <li>iOS: {@code accessibility = "summary.element"}</li>
 *   <li>Both platforms use the same test ID pattern per Spec 047</li>
 * </ul>
 * 
 * <h2>Test Identifiers (FR-012):</h2>
 * <ul>
 *   <li>summary.title - "Report created" title text</li>
 *   <li>summary.bodyParagraph1 - First body paragraph</li>
 *   <li>summary.bodyParagraph2 - Second body paragraph</li>
 *   <li>summary.passwordContainer - Gradient container (tappable for copy)</li>
 *   <li>summary.passwordText - Password digits display</li>
 *   <li>summary.closeButton - Close button at bottom</li>
 *   <li>summary.snackbar - Snackbar for clipboard confirmation</li>
 * </ul>
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.mobile.ReportCreatedSteps
 */
public class SummaryScreen {
    
    private AppiumDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // Element Locators (Dual Annotations)
    // ========================================
    
    /**
     * Title text element displaying "Report created".
     */
    @AndroidFindBy(accessibility = "summary.title")
    @iOSXCUITFindBy(accessibility = "summary.title")
    private WebElement title;
    
    /**
     * First body paragraph about report creation and notifications.
     */
    @AndroidFindBy(accessibility = "summary.bodyParagraph1")
    @iOSXCUITFindBy(accessibility = "summary.bodyParagraph1")
    private WebElement bodyParagraph1;
    
    /**
     * Second body paragraph about removal code and email.
     */
    @AndroidFindBy(accessibility = "summary.bodyParagraph2")
    @iOSXCUITFindBy(accessibility = "summary.bodyParagraph2")
    private WebElement bodyParagraph2;
    
    /**
     * Gradient password container (tappable to copy).
     */
    @AndroidFindBy(accessibility = "summary.passwordContainer")
    @iOSXCUITFindBy(accessibility = "summary.passwordContainer")
    private WebElement passwordContainer;
    
    /**
     * Password text element displaying the management code digits.
     */
    @AndroidFindBy(accessibility = "summary.passwordText")
    @iOSXCUITFindBy(accessibility = "summary.passwordText")
    private WebElement passwordText;
    
    /**
     * Close button to exit the flow.
     */
    @AndroidFindBy(accessibility = "summary.closeButton")
    @iOSXCUITFindBy(accessibility = "summary.closeButton")
    private WebElement closeButton;
    
    /**
     * Snackbar element for clipboard confirmation message.
     */
    @AndroidFindBy(accessibility = "summary.snackbar")
    @iOSXCUITFindBy(accessibility = "summary.snackbar")
    private WebElement snackbar;
    
    // ========================================
    // Constructor
    // ========================================
    
    /**
     * Initializes the Screen Object with AppiumDriver instance.
     * 
     * @param driver AppiumDriver instance (AndroidDriver or IOSDriver)
     */
    public SummaryScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    // ========================================
    // Screen State Verification
    // ========================================
    
    /**
     * Checks if the summary screen is displayed.
     * 
     * @return true if title is visible
     */
    public boolean isDisplayed() {
        try {
            return title.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for the summary screen to become visible.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if screen became visible within timeout
     */
    public boolean waitForScreenVisible(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(title));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // US1: Confirmation Messaging
    // ========================================
    
    /**
     * Gets the title text.
     * 
     * @return Title text (expected: "Report created")
     */
    public String getTitleText() {
        try {
            return title.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get title text: " + e.getMessage());
        }
    }
    
    /**
     * Checks if title is displayed.
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
     * Checks if first body paragraph is displayed.
     * 
     * @return true if paragraph is visible
     */
    public boolean isBodyParagraph1Displayed() {
        try {
            return bodyParagraph1.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if second body paragraph is displayed.
     * 
     * @return true if paragraph is visible
     */
    public boolean isBodyParagraph2Displayed() {
        try {
            return bodyParagraph2.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the first body paragraph text.
     * 
     * @return First paragraph text
     */
    public String getBodyParagraph1Text() {
        try {
            return bodyParagraph1.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get body paragraph 1 text: " + e.getMessage());
        }
    }
    
    /**
     * Gets the second body paragraph text.
     * 
     * @return Second paragraph text
     */
    public String getBodyParagraph2Text() {
        try {
            return bodyParagraph2.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get body paragraph 2 text: " + e.getMessage());
        }
    }
    
    // ========================================
    // US2: Password Display and Copy
    // ========================================
    
    /**
     * Checks if password container is displayed.
     * 
     * @return true if container is visible
     */
    public boolean isPasswordContainerDisplayed() {
        try {
            return passwordContainer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the password/code text.
     * 
     * @return Password digits (e.g., "5216577")
     */
    public String getPasswordText() {
        try {
            return passwordText.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get password text: " + e.getMessage());
        }
    }
    
    /**
     * Taps the password container to copy to clipboard.
     * Triggers ShowSnackbar effect in ViewModel.
     */
    public void tapPasswordContainer() {
        try {
            passwordContainer.click();
            System.out.println("Tapped password container to copy");
        } catch (Exception e) {
            throw new RuntimeException("Failed to tap password container: " + e.getMessage());
        }
    }
    
    /**
     * Checks if snackbar is displayed.
     * 
     * @return true if snackbar is visible
     */
    public boolean isSnackbarDisplayed() {
        try {
            return snackbar.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for snackbar to appear.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if snackbar appeared within timeout
     */
    public boolean waitForSnackbar(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(snackbar));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the snackbar message text.
     * 
     * @return Snackbar message (expected: "Code copied to clipboard")
     */
    public String getSnackbarText() {
        try {
            return snackbar.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get snackbar text: " + e.getMessage());
        }
    }
    
    // ========================================
    // US3: Close Flow
    // ========================================
    
    /**
     * Checks if close button is displayed.
     * 
     * @return true if button is visible
     */
    public boolean isCloseButtonDisplayed() {
        try {
            return closeButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the close button text.
     * 
     * @return Button text (expected: "Close")
     */
    public String getCloseButtonText() {
        try {
            return closeButton.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get close button text: " + e.getMessage());
        }
    }
    
    /**
     * Taps the close button to dismiss the flow.
     * Triggers DismissFlow effect in ViewModel.
     */
    public void tapCloseButton() {
        try {
            closeButton.click();
            System.out.println("Tapped Close button");
        } catch (Exception e) {
            throw new RuntimeException("Failed to tap Close button: " + e.getMessage());
        }
    }
    
    /**
     * Checks if close button is enabled/tappable.
     * 
     * @return true if button is enabled
     */
    public boolean isCloseButtonEnabled() {
        try {
            return closeButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Performs system back action.
     * Should behave same as Close button (FR-010).
     */
    public void performSystemBack() {
        try {
            driver.navigate().back();
            System.out.println("Performed system back");
        } catch (Exception e) {
            throw new RuntimeException("Failed to perform system back: " + e.getMessage());
        }
    }
    
    // ========================================
    // Utility Methods
    // ========================================
    
    /**
     * Gets the driver instance for platform-specific operations.
     * 
     * @return AppiumDriver instance
     */
    public AppiumDriver getDriver() {
        return driver;
    }
}

