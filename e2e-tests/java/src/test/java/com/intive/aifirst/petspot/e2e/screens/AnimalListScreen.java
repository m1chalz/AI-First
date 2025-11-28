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
 * Screen Object Model for Animal List screen with location permission handling.
 * Extends the base pet list functionality with permission-related UI elements.
 * 
 * <p>This screen handles:
 * <ul>
 *   <li>Standard animal list display and interactions</li>
 *   <li>System permission dialogs (Android native)</li>
 *   <li>Custom rationale dialogs (Educational and Informational)</li>
 *   <li>Location status indicators</li>
 * </ul>
 * 
 * <h2>Test ID Naming Convention:</h2>
 * <ul>
 *   <li>{@code animalList.*} - Standard list elements</li>
 *   <li>{@code animalList.rationaleDialog.*} - Custom rationale dialog elements</li>
 *   <li>{@code animalList.educationalDialog.*} - Educational dialog elements</li>
 *   <li>{@code animalList.locationStatus} - Location indicator</li>
 *   <li>{@code animalList.loadingIndicator} - Loading state indicator</li>
 * </ul>
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.mobile.LocationPermissionSteps
 */
public class AnimalListScreen {
    
    private AppiumDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // Animal List Elements
    // ========================================
    
    /**
     * Animal list container element (scrollable list).
     */
    @AndroidFindBy(accessibility = "animalList.list")
    @iOSXCUITFindBy(accessibility = "animalList.list")
    private WebElement animalList;
    
    /**
     * Loading indicator shown during location fetch or data loading.
     */
    @AndroidFindBy(accessibility = "animalList.loadingIndicator")
    @iOSXCUITFindBy(accessibility = "animalList.loadingIndicator")
    private WebElement loadingIndicator;
    
    /**
     * Location status indicator showing current location availability.
     */
    @AndroidFindBy(accessibility = "animalList.locationStatus")
    @iOSXCUITFindBy(accessibility = "animalList.locationStatus")
    private WebElement locationStatus;
    
    // ========================================
    // Educational Rationale Dialog Elements
    // ========================================
    
    /**
     * Educational rationale dialog container.
     * Shown before system permission dialog when shouldShowRationale is true.
     */
    @AndroidFindBy(accessibility = "animalList.educationalDialog")
    @iOSXCUITFindBy(accessibility = "animalList.educationalDialog")
    private WebElement educationalDialog;
    
    /**
     * "Continue" button in educational rationale dialog.
     * Triggers system permission dialog.
     */
    @AndroidFindBy(accessibility = "animalList.educationalDialog.continueButton")
    @iOSXCUITFindBy(accessibility = "animalList.educationalDialog.continueButton")
    private WebElement educationalContinueButton;
    
    /**
     * "Not Now" button in educational rationale dialog.
     * Dismisses dialog without requesting permission.
     */
    @AndroidFindBy(accessibility = "animalList.educationalDialog.notNowButton")
    @iOSXCUITFindBy(accessibility = "animalList.educationalDialog.notNowButton")
    private WebElement educationalNotNowButton;
    
    // ========================================
    // Informational Rationale Dialog Elements (Denied State)
    // ========================================
    
    /**
     * Informational rationale dialog container.
     * Shown when permission is denied (user selected "Don't Allow" or "Don't ask again").
     */
    @AndroidFindBy(accessibility = "animalList.rationaleDialog")
    @iOSXCUITFindBy(accessibility = "animalList.rationaleDialog")
    private WebElement rationaleDialog;
    
    /**
     * "Go to Settings" button in informational rationale dialog.
     * Opens app settings for manual permission grant.
     */
    @AndroidFindBy(accessibility = "animalList.rationaleDialog.goToSettingsButton")
    @iOSXCUITFindBy(accessibility = "animalList.rationaleDialog.goToSettingsButton")
    private WebElement goToSettingsButton;
    
    /**
     * "Cancel" button in informational rationale dialog.
     * Dismisses dialog and continues without location.
     */
    @AndroidFindBy(accessibility = "animalList.rationaleDialog.cancelButton")
    @iOSXCUITFindBy(accessibility = "animalList.rationaleDialog.cancelButton")
    private WebElement cancelButton;
    
    // ========================================
    // Constructor
    // ========================================
    
    /**
     * Initializes the Screen Object with AppiumDriver instance.
     * 
     * @param driver AppiumDriver instance (AndroidDriver or IOSDriver)
     */
    public AnimalListScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    // ========================================
    // Wait Methods
    // ========================================
    
    /**
     * Waits for the animal list to be visible and loaded.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if list became visible within timeout
     */
    public boolean waitForAnimalListVisible(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(animalList));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for the loading indicator to disappear.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if loading completed within timeout
     */
    public boolean waitForLoadingComplete(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.invisibilityOf(loadingIndicator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for the educational rationale dialog to appear.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if dialog appeared within timeout
     */
    public boolean waitForEducationalDialog(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(educationalDialog));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Waits for the informational rationale dialog to appear.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if dialog appeared within timeout
     */
    public boolean waitForRationaleDialog(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(rationaleDialog));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // System Permission Dialog Interactions (Android)
    // ========================================
    
    /**
     * Taps "Allow" on the Android system permission dialog.
     * Uses Android native UI element locator.
     */
    public void tapAllowOnSystemDialog() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            // Android permission dialog "Allow" button
            WebElement allowButton = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button")
            ));
            allowButton.click();
            System.out.println("Tapped 'Allow' on system permission dialog");
        } catch (Exception e) {
            // Try alternative locator for different Android versions
            try {
                WebElement allowButton = driver.findElement(
                    AppiumBy.xpath("//*[@text='Allow' or @text='ALLOW' or @text='While using the app']")
                );
                allowButton.click();
                System.out.println("Tapped 'Allow' on system permission dialog (alternative locator)");
            } catch (Exception ex) {
                System.err.println("Could not find 'Allow' button: " + ex.getMessage());
                throw new RuntimeException("System permission dialog 'Allow' button not found", ex);
            }
        }
    }
    
    /**
     * Taps "Deny" on the Android system permission dialog.
     */
    public void tapDenyOnSystemDialog() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            // Android permission dialog "Don't allow" button
            WebElement denyButton = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.id("com.android.permissioncontroller:id/permission_deny_button")
            ));
            denyButton.click();
            System.out.println("Tapped 'Deny' on system permission dialog");
        } catch (Exception e) {
            // Try alternative locator
            try {
                WebElement denyButton = driver.findElement(
                    AppiumBy.xpath("//*[@text=\"Don't allow\" or @text='DENY' or @text='Deny']")
                );
                denyButton.click();
                System.out.println("Tapped 'Deny' on system permission dialog (alternative locator)");
            } catch (Exception ex) {
                System.err.println("Could not find 'Deny' button: " + ex.getMessage());
                throw new RuntimeException("System permission dialog 'Deny' button not found", ex);
            }
        }
    }
    
    /**
     * Checks if the system permission dialog is displayed.
     * 
     * @return true if system permission dialog is visible
     */
    public boolean isSystemPermissionDialogDisplayed() {
        try {
            // Check for Android permission dialog
            return driver.findElement(
                AppiumBy.id("com.android.permissioncontroller:id/grant_dialog")
            ).isDisplayed();
        } catch (Exception e) {
            try {
                // Alternative: check for any permission dialog text
                return driver.findElement(
                    AppiumBy.xpath("//*[contains(@text, 'location') or contains(@text, 'Location')]")
                ).isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        }
    }
    
    // ========================================
    // Educational Dialog Actions
    // ========================================
    
    /**
     * Taps "Continue" on the educational rationale dialog.
     */
    public void tapContinueOnEducationalDialog() {
        educationalContinueButton.click();
        System.out.println("Tapped 'Continue' on educational dialog");
    }
    
    /**
     * Taps "Not Now" on the educational rationale dialog.
     */
    public void tapNotNowOnEducationalDialog() {
        educationalNotNowButton.click();
        System.out.println("Tapped 'Not Now' on educational dialog");
    }
    
    /**
     * Checks if the educational rationale dialog is displayed.
     * 
     * @return true if educational dialog is visible
     */
    public boolean isEducationalDialogDisplayed() {
        try {
            return educationalDialog.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // Informational Rationale Dialog Actions
    // ========================================
    
    /**
     * Taps "Go to Settings" on the informational rationale dialog.
     */
    public void tapGoToSettings() {
        goToSettingsButton.click();
        System.out.println("Tapped 'Go to Settings' on rationale dialog");
    }
    
    /**
     * Taps "Cancel" on the informational rationale dialog.
     */
    public void tapCancelOnRationaleDialog() {
        cancelButton.click();
        System.out.println("Tapped 'Cancel' on rationale dialog");
    }
    
    /**
     * Checks if the informational rationale dialog is displayed.
     * 
     * @return true if rationale dialog is visible
     */
    public boolean isRationaleDialogDisplayed() {
        try {
            return rationaleDialog.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // Location Status Verification
    // ========================================
    
    /**
     * Checks if the loading indicator is displayed.
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
     * Checks if the location status indicator is displayed.
     * 
     * @return true if location status is visible
     */
    public boolean isLocationStatusDisplayed() {
        try {
            return locationStatus.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if the animal list is displayed.
     * 
     * @return true if animal list is visible
     */
    public boolean isAnimalListDisplayed() {
        try {
            return animalList.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the count of visible animal items in the list.
     * 
     * @return Number of animal items displayed
     */
    public int getAnimalCount() {
        return getAnimalItems().size();
    }
    
    /**
     * Checks if at least one animal is displayed.
     * 
     * @return true if one or more animals are visible
     */
    public boolean hasAnyAnimals() {
        return getAnimalCount() > 0;
    }
    
    // ========================================
    // Helper Methods
    // ========================================
    
    /**
     * Gets all animal item elements from the list.
     * 
     * @return List of WebElements representing animal items
     */
    private List<WebElement> getAnimalItems() {
        return driver.findElements(
            AppiumBy.xpath("//*[contains(@content-desc, 'animalList.item.') or contains(@name, 'animalList.item.')]")
        );
    }
}

