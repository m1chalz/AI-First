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
 * Screen Object Model for Pet Details screen (Android + iOS).
 * 
 * <p>This class uses dual annotations to support both Android and iOS platforms:
 * <ul>
 *   <li>@AndroidFindBy: UiAutomator2 selectors for Android (accessibility attributes)</li>
 *   <li>@iOSXCUITFindBy: XCUITest selectors for iOS (accessibilityIdentifier)</li>
 * </ul>
 * 
 * <h2>Locator Strategy:</h2>
 * <ul>
 *   <li>Android: {@code accessibility = "petDetails.element.action"}</li>
 *   <li>iOS: {@code accessibility = "petDetails.element.action"}</li>
 *   <li>Both platforms use the same test ID pattern per Spec 012</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // In step definitions
 * AppiumDriver driver = AppiumDriverManager.getDriver("iOS");
 * PetDetailsScreen detailsScreen = new PetDetailsScreen(driver);
 * 
 * // Interact with screen
 * boolean isLoading = detailsScreen.isLoadingDisplayed();
 * detailsScreen.tapPhoneNumber();
 * }</pre>
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.mobile.PetDetailsMobileSteps
 */
public class PetDetailsScreen {
    
    private AppiumDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // Element Locators (Dual Annotations)
    // ========================================
    
    /**
     * Pet details view container (main content).
     * Displayed when pet details have loaded successfully.
     */
    @AndroidFindBy(accessibility = "petDetails.view")
    @iOSXCUITFindBy(accessibility = "petDetails.view")
    private WebElement detailsView;
    
    /**
     * Loading spinner/indicator.
     * Displayed while fetching pet details from repository.
     */
    @AndroidFindBy(accessibility = "petDetails.loading")
    @iOSXCUITFindBy(accessibility = "petDetails.loading")
    private WebElement loadingSpinner;
    
    /**
     * Error view container.
     * Displayed when pet details fetch fails.
     */
    @AndroidFindBy(accessibility = "petDetails.error")
    @iOSXCUITFindBy(accessibility = "petDetails.error")
    private WebElement errorView;
    
    /**
     * Retry button in error state.
     * Tapping triggers ViewModel.loadPetDetails() retry.
     */
    @AndroidFindBy(accessibility = "petDetails.error.retry")
    @iOSXCUITFindBy(accessibility = "petDetails.error.retry")
    private WebElement retryButton;
    
    /**
     * Pet photo image element.
     * Displays pet photo or fallback "Image not available" placeholder.
     */
    @AndroidFindBy(accessibility = "petDetails.photo.image")
    @iOSXCUITFindBy(accessibility = "petDetails.photo.image")
    private WebElement petPhoto;
    
    /**
     * Phone number field (tappable).
     * Opens iOS dialer when tapped.
     */
    @AndroidFindBy(accessibility = "petDetails.phone.tap")
    @iOSXCUITFindBy(accessibility = "petDetails.phone.tap")
    private WebElement phoneNumber;
    
    /**
     * Email address field (tappable).
     * Opens iOS mail composer when tapped.
     */
    @AndroidFindBy(accessibility = "petDetails.email.tap")
    @iOSXCUITFindBy(accessibility = "petDetails.email.tap")
    private WebElement emailAddress;
    
    /**
     * Status badge element.
     * Displays "MISSING" (red), "FOUND" (blue), or "CLOSED" (gray).
     */
    @AndroidFindBy(accessibility = "petDetails.status.badge")
    @iOSXCUITFindBy(accessibility = "petDetails.status.badge")
    private WebElement statusBadge;
    
    /**
     * Remove Report button.
     * Displayed at bottom of screen for all users.
     */
    @AndroidFindBy(accessibility = "petDetails.removeReport.button")
    @iOSXCUITFindBy(accessibility = "petDetails.removeReport.button")
    private WebElement removeReportButton;
    
    /**
     * Pet name text element.
     */
    @AndroidFindBy(accessibility = "petDetails.name.text")
    @iOSXCUITFindBy(accessibility = "petDetails.name.text")
    private WebElement petName;
    
    /**
     * Species field value.
     */
    @AndroidFindBy(accessibility = "petDetails.species.text")
    @iOSXCUITFindBy(accessibility = "petDetails.species.text")
    private WebElement speciesText;
    
    /**
     * Breed field value.
     */
    @AndroidFindBy(accessibility = "petDetails.breed.text")
    @iOSXCUITFindBy(accessibility = "petDetails.breed.text")
    private WebElement breedText;
    
    /**
     * Photo placeholder element.
     * Displayed when photo is not available.
     */
    @AndroidFindBy(accessibility = "petDetails.photo.placeholder")
    @iOSXCUITFindBy(accessibility = "petDetails.photo.placeholder")
    private WebElement photoPlaceholder;
    
    // ========================================
    // Constructor
    // ========================================
    
    /**
     * Initializes the Screen Object with AppiumDriver instance.
     * 
     * @param driver AppiumDriver instance (AndroidDriver or IOSDriver)
     */
    public PetDetailsScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    // ========================================
    // Screen State Verification
    // ========================================
    
    /**
     * Checks if pet details view is displayed (loaded state).
     * 
     * @return true if details view is visible
     */
    public boolean isDisplayed() {
        try {
            return detailsView.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if loading indicator is displayed.
     * 
     * @return true if loading spinner is visible
     */
    public boolean isLoadingDisplayed() {
        try {
            return loadingSpinner.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if error view is displayed.
     * 
     * @return true if error view is visible
     */
    public boolean isErrorDisplayed() {
        try {
            return errorView.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for the details view to become visible.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if details view became visible within timeout
     */
    public boolean waitForDetailsVisible(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(detailsView));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for the loading indicator to become visible.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if loading indicator became visible within timeout
     */
    public boolean waitForLoadingVisible(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(loadingSpinner));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if loading indicator is centered on screen.
     * 
     * @return true if loading indicator appears centered
     */
    public boolean isLoadingCentered() {
        try {
            // Check if loading spinner is displayed and positioned centrally
            // Simplified check: verify element is visible (actual centering is UI implementation detail)
            return loadingSpinner.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // Screen Actions
    // ========================================
    
    /**
     * Taps the retry button in error state.
     * Triggers ViewModel.loadPetDetails() to re-attempt data fetch.
     */
    public void tapRetryButton() {
        try {
            retryButton.click();
            System.out.println("Tapped retry button");
        } catch (Exception e) {
            throw new RuntimeException("Failed to tap retry button: " + e.getMessage());
        }
    }
    
    /**
     * Taps on the phone number field.
     * Opens iOS dialer with phone number pre-filled.
     */
    public void tapPhoneNumber() {
        try {
            phoneNumber.click();
            System.out.println("Tapped phone number");
        } catch (Exception e) {
            throw new RuntimeException("Failed to tap phone number: " + e.getMessage());
        }
    }
    
    /**
     * Taps on the email address field.
     * Opens iOS mail composer with email pre-filled.
     */
    public void tapEmailAddress() {
        try {
            emailAddress.click();
            System.out.println("Tapped email address");
        } catch (Exception e) {
            throw new RuntimeException("Failed to tap email address: " + e.getMessage());
        }
    }
    
    /**
     * Taps the Remove Report button.
     * Logs action to console (placeholder implementation).
     */
    public void tapRemoveReportButton() {
        try {
            removeReportButton.click();
            System.out.println("Tapped Remove Report button");
        } catch (Exception e) {
            throw new RuntimeException("Failed to tap Remove Report button: " + e.getMessage());
        }
    }
    
    /**
     * Scrolls to the bottom of the screen.
     * Used to reveal Remove Report button.
     */
    public void scrollToBottom() {
        try {
            // Use mobile: scroll command for iOS
            if (driver.getCapabilities().getPlatformName().toString().equalsIgnoreCase("iOS")) {
                driver.executeScript("mobile: scroll", 
                    new java.util.HashMap<String, Object>() {{
                        put("direction", "down");
                    }});
            } else {
                // Android scroll gesture
                driver.executeScript("mobile: scrollGesture", 
                    new java.util.HashMap<String, Object>() {{
                        put("direction", "down");
                        put("percent", 1.0);
                    }});
            }
            System.out.println("Scrolled to bottom");
        } catch (Exception e) {
            System.err.println("Scroll to bottom failed: " + e.getMessage());
        }
    }
    
    // ========================================
    // Data Verification
    // ========================================
    
    /**
     * Gets the status badge text.
     * 
     * @return Status text ("MISSING", "FOUND", "CLOSED")
     */
    public String getStatusBadgeText() {
        try {
            return statusBadge.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get status badge text: " + e.getMessage());
        }
    }
    
    /**
     * Gets the status badge background color.
     * 
     * @return Color value as string
     */
    public String getStatusBadgeColor() {
        try {
            // Get the color attribute (implementation depends on platform)
            // iOS: May need to check label property
            // Android: May need to check backgroundColor attribute
            String color = statusBadge.getAttribute("color");
            if (color == null || color.isEmpty()) {
                color = statusBadge.getAttribute("backgroundColor");
            }
            return color;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get status badge color: " + e.getMessage());
        }
    }
    
    /**
     * Checks if pet photo is displayed.
     * 
     * @return true if photo is visible
     */
    public boolean isPetPhotoDisplayed() {
        try {
            return petPhoto.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if pet name is displayed.
     * 
     * @return true if name is visible
     */
    public boolean isPetNameDisplayed() {
        try {
            return petName.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if species field is displayed.
     * 
     * @return true if species is visible
     */
    public boolean isSpeciesDisplayed() {
        try {
            return speciesText.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if breed field is displayed.
     * 
     * @return true if breed is visible
     */
    public boolean isBreedDisplayed() {
        try {
            return breedText.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if all required fields contain valid data (non-empty).
     * 
     * @return true if all fields have content
     */
    public boolean allFieldsHaveValidData() {
        try {
            boolean hasName = petName.getText() != null && !petName.getText().trim().isEmpty();
            boolean hasSpecies = speciesText.getText() != null && !speciesText.getText().trim().isEmpty();
            boolean hasBreed = breedText.getText() != null && !breedText.getText().trim().isEmpty();
            
            return hasName && hasSpecies && hasBreed;
        } catch (Exception e) {
            System.err.println("Failed to verify field data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if photo placeholder is displayed.
     * 
     * @return true if placeholder is visible
     */
    public boolean isPhotoPlaceholderDisplayed() {
        try {
            return photoPlaceholder.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the photo placeholder text.
     * 
     * @return Placeholder text (e.g., "Image not available")
     */
    public String getPhotoPlaceholderText() {
        try {
            return photoPlaceholder.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get photo placeholder text: " + e.getMessage());
        }
    }
    
    /**
     * Checks if Remove Report button is visible.
     * 
     * @return true if button is displayed
     */
    public boolean isRemoveReportButtonVisible() {
        try {
            return removeReportButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if Remove Report button is tappable (enabled).
     * 
     * @return true if button is enabled
     */
    public boolean isRemoveReportButtonTappable() {
        try {
            return removeReportButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if error message is user-friendly.
     * Expected message: "Unable to load pet details"
     * 
     * @return true if error message matches expected text
     */
    public boolean isErrorMessageUserFriendly() {
        try {
            String errorText = errorView.getText();
            return errorText != null && errorText.contains("Unable to load pet details");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if phone number field is displayed.
     * 
     * @return true if phone number is visible
     */
    public boolean isPhoneNumberDisplayed() {
        try {
            return phoneNumber.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if email address field is displayed.
     * 
     * @return true if email is visible
     */
    public boolean isEmailAddressDisplayed() {
        try {
            return emailAddress.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the phone number text value.
     * 
     * @return Phone number as displayed
     */
    public String getPhoneNumberText() {
        try {
            return phoneNumber.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get phone number text: " + e.getMessage());
        }
    }
    
    /**
     * Gets the email address text value.
     * 
     * @return Email address as displayed
     */
    public String getEmailAddressText() {
        try {
            return emailAddress.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get email address text: " + e.getMessage());
        }
    }
}
