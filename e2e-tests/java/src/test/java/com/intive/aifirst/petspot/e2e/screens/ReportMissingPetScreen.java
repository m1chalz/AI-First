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
 * Screen Object Model for Missing Pet Report Flow (iOS).
 * 
 * <p>This class uses dual annotations to support both Android and iOS platforms:
 * <ul>
 *   <li>@AndroidFindBy: UiAutomator2 selectors for Android (testTag attributes)</li>
 *   <li>@iOSXCUITFindBy: XCUITest selectors for iOS (accessibilityIdentifier)</li>
 * </ul>
 * 
 * <h2>Screen Structure:</h2>
 * <ul>
 *   <li>Animal List Screen: "report missing animal" button entry point</li>
 *   <li>Chip Number Screen (Step 1/4): Placeholder with continue button</li>
 *   <li>Photo Screen (Step 2/4): Placeholder with continue button</li>
 *   <li>Description Screen (Step 3/4): Placeholder with continue button</li>
 *   <li>Contact Details Screen (Step 4/4): Placeholder with continue button</li>
 *   <li>Summary Screen (Step 5): No progress indicator, submit button</li>
 * </ul>
 * 
 * <h2>Common Elements (All Screens):</h2>
 * <ul>
 *   <li>Back Button: Custom chevron-left button (exits on step 1, pops on steps 2-5)</li>
 *   <li>Progress Indicator: Circular badge showing "X/4" (hidden on summary)</li>
 * </ul>
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.mobile.ReportMissingPetSteps
 */
public class ReportMissingPetScreen {
    
    private AppiumDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // ANIMAL LIST SCREEN - Entry Point
    // ========================================
    
    /**
     * "Report Missing Animal" button on animal list screen.
     * Tapping this opens the modal report flow.
     */
    @AndroidFindBy(accessibility = "animalList.reportMissingButton")
    @iOSXCUITFindBy(id = "animalList.reportMissingButton")
    private WebElement reportMissingButton;
    
    // ========================================
    // COMMON ELEMENTS (All Report Screens)
    // ========================================
    
    /**
     * Custom back button (chevron-left icon).
     * Present on screens 2-5 of the report flow (photo, description, contact details, summary).
     * Not present on step 1 (uses dismissButton instead).
     */
    @AndroidFindBy(accessibility = "reportMissingPet.backButton")
    @iOSXCUITFindBy(id = "reportMissingPet.backButton")
    private WebElement backButton;
    
    /**
     * Progress indicator badge showing current step (e.g., "1/4", "2/4", "3/4", "4/4").
     * Hidden on summary screen.
     * Format: Circular blue badge with white text.
     */
    @AndroidFindBy(accessibility = "reportMissingPet.progressIndicator")
    @iOSXCUITFindBy(id = "reportMissingPet.progressIndicator")
    private WebElement progressIndicator;
    
    // ========================================
    // STEP 1: CHIP NUMBER SCREEN (1/4)
    // ========================================
    
    // ========================================
    // STEP 2: PHOTO SCREEN (2/4)
    // ========================================
    
    /**
     * Continue button on photo screen.
     * Navigates to description screen (step 3/4).
     */
    @AndroidFindBy(accessibility = "photo.continueButton")
    @iOSXCUITFindBy(id = "photo.continueButton")
    private WebElement photoContinueButton;
    
    /**
     * Placeholder text for photo screen.
     * Currently "Photo Screen" - photo picker added in future implementation.
     */
    @AndroidFindBy(xpath = "//XCUIElementTypeStaticText[@label='Photo Screen']")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@label='Photo Screen']")
    private WebElement photoScreenTitle;
    
    // ========================================
    // STEP 3: DESCRIPTION SCREEN (3/4)
    // ========================================
    
    /**
     * Continue button on description screen.
     * Navigates to contact details screen (step 4/4).
     */
    @AndroidFindBy(accessibility = "description.continueButton")
    @iOSXCUITFindBy(id = "description.continueButton")
    private WebElement descriptionContinueButton;
    
    /**
     * Placeholder text for description screen.
     * Currently "Description Screen" - text area added in future implementation.
     */
    @AndroidFindBy(xpath = "//XCUIElementTypeStaticText[@label='Description Screen']")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@label='Description Screen']")
    private WebElement descriptionScreenTitle;
    
    // ========================================
    // STEP 4: CONTACT DETAILS SCREEN (4/4)
    // ========================================
    
    /**
     * Continue button on contact details screen.
     * Navigates to summary screen (step 5).
     */
    @AndroidFindBy(accessibility = "contactDetails.continueButton")
    @iOSXCUITFindBy(id = "contactDetails.continueButton")
    private WebElement contactDetailsContinueButton;
    
    /**
     * Placeholder text for contact details screen.
     * Currently "Contact Details Screen" - form fields added in future implementation.
     */
    @AndroidFindBy(xpath = "//XCUIElementTypeStaticText[@label='Contact Details Screen']")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@label='Contact Details Screen']")
    private WebElement contactDetailsScreenTitle;
    
    // ========================================
    // STEP 5: SUMMARY SCREEN (No Progress Indicator)
    // ========================================
    
    /**
     * Submit button on summary screen.
     * Final step - submits the report (placeholder for now).
     */
    @AndroidFindBy(accessibility = "summary.submitButton")
    @iOSXCUITFindBy(id = "summary.submitButton")
    private WebElement summarySubmitButton;
    
    /**
     * Placeholder text for summary screen.
     * Currently "Summary Screen" - actual summary data display added in future implementation.
     */
    @AndroidFindBy(xpath = "//XCUIElementTypeStaticText[@label='Summary Screen']")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@label='Summary Screen']")
    private WebElement summaryScreenTitle;
    
    // ========================================
    // INITIALIZATION
    // ========================================
    
    public ReportMissingPetScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT)), this);
    }
    
    // ========================================
    // ANIMAL LIST SCREEN ACTIONS
    // ========================================
    
    /**
     * Tap the "Report Missing Animal" button on animal list screen.
     * Opens the report missing pet modal flow.
     */
    public void tapReportMissingButton() {
        waitForElement(reportMissingButton);
        reportMissingButton.click();
    }
    
    /**
     * Verify "Report Missing Animal" button is displayed.
     */
    public boolean isReportMissingButtonDisplayed() {
        try {
            return reportMissingButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // COMMON NAVIGATION ACTIONS
    // ========================================
    
    /**
     * Tap the back button (chevron-left icon).
     * On steps 2-5: navigates to previous screen
     * Not available on step 1 (uses dismiss button instead)
     */
    public void tapBackButton() {
        waitForElement(backButton);
        backButton.click();
    }
    
    /**
     * Verify back button is displayed and tappable.
     */
    public boolean isBackButtonDisplayed() {
        try {
            return backButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get the current progress indicator text (e.g., "1/4", "2/4", "3/4", "4/4").
     * Returns empty string if progress indicator is not visible (summary screen).
     */
    public String getProgressIndicatorText() {
        try {
            waitForElement(progressIndicator);
            return progressIndicator.getText();
        } catch (Exception e) {
            return ""; // Progress indicator not visible
        }
    }
    
    /**
     * Verify progress indicator is displayed and shows expected text.
     */
    public boolean isProgressIndicatorDisplayed() {
        try {
            return progressIndicator.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // SCREEN NAVIGATION (Continue Buttons)
    // ========================================
    
    /**
     * Tap continue button on photo screen (step 2).
     * Navigates to description screen (step 3/4).
     */
    public void tapContinueOnPhotoScreen() {
        waitForElement(photoContinueButton);
        photoContinueButton.click();
    }
    
    /**
     * Tap continue button on description screen (step 3).
     * Navigates to contact details screen (step 4/4).
     */
    public void tapContinueOnDescriptionScreen() {
        waitForElement(descriptionContinueButton);
        descriptionContinueButton.click();
    }
    
    /**
     * Tap continue button on contact details screen (step 4).
     * Navigates to summary screen (step 5).
     */
    public void tapContinueOnContactDetailsScreen() {
        waitForElement(contactDetailsContinueButton);
        contactDetailsContinueButton.click();
    }
    
    /**
     * Tap submit button on summary screen (step 5).
     * Submits the report and completes the flow.
     */
    public void tapSubmitOnSummaryScreen() {
        waitForElement(summarySubmitButton);
        summarySubmitButton.click();
    }
    
    // ========================================
    // SCREEN VERIFICATION METHODS
    // ========================================
    
    /**
     * Verify photo screen is displayed.
     */
    public boolean isPhotoScreenDisplayed() {
        try {
            return photoScreenTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verify description screen is displayed.
     */
    public boolean isDescriptionScreenDisplayed() {
        try {
            return descriptionScreenTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verify contact details screen is displayed.
     */
    public boolean isContactDetailsScreenDisplayed() {
        try {
            return contactDetailsScreenTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verify summary screen is displayed.
     */
    public boolean isSummaryScreenDisplayed() {
        try {
            return summaryScreenTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========================================
    // UTILITY METHODS
    // ========================================
    
    /**
     * Wait for element to be displayed (with configurable timeout).
     */
    private void waitForElement(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Wait for element to be displayed with custom timeout.
     */
    private void waitForElement(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.visibilityOf(element));
    }
}

