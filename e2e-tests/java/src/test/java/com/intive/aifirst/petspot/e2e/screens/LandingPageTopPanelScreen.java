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
 * Screen Object Model for Landing Page Top Panel (Hero Section + List Header).
 * Supports both Android and iOS platforms via dual annotations.
 * 
 * <h2>Elements:</h2>
 * <ul>
 *   <li>Hero Panel: Title "Find Your Pet" + "Lost Pet" / "Found Pet" buttons</li>
 *   <li>List Header: "Recent Reports" title + "View All" action</li>
 * </ul>
 * 
 * <h2>Accessibility IDs (per FR-010):</h2>
 * <ul>
 *   <li>home.hero.title - Hero section title</li>
 *   <li>home.hero.lostPetButton - Lost Pet button</li>
 *   <li>home.hero.foundPetButton - Found Pet button</li>
 *   <li>home.recentReports.title - List header title</li>
 *   <li>home.recentReports.viewAll - View All action</li>
 * </ul>
 * 
 * @see LandingPageScreen
 */
public class LandingPageTopPanelScreen {

    private final AppiumDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;

    // ========================================
    // Hero Panel Elements
    // ========================================

    /**
     * Hero panel title element ("Find Your Pet").
     */
    @AndroidFindBy(accessibility = "home.hero.title")
    @iOSXCUITFindBy(accessibility = "home.hero.title")
    private WebElement heroTitle;

    /**
     * "Lost Pet" button in hero panel.
     * Red/orange gradient with alert triangle icon.
     */
    @AndroidFindBy(accessibility = "home.hero.lostPetButton")
    @iOSXCUITFindBy(accessibility = "home.hero.lostPetButton")
    private WebElement lostPetButton;

    /**
     * "Found Pet" button in hero panel.
     * Blue gradient with checkmark icon.
     */
    @AndroidFindBy(accessibility = "home.hero.foundPetButton")
    @iOSXCUITFindBy(accessibility = "home.hero.foundPetButton")
    private WebElement foundPetButton;

    // ========================================
    // List Header Elements
    // ========================================

    /**
     * List header title element ("Recent Reports").
     */
    @AndroidFindBy(accessibility = "home.recentReports.title")
    @iOSXCUITFindBy(accessibility = "home.recentReports.title")
    private WebElement listHeaderTitle;

    /**
     * "View All" action in list header.
     */
    @AndroidFindBy(accessibility = "home.recentReports.viewAll")
    @iOSXCUITFindBy(accessibility = "home.recentReports.viewAll")
    private WebElement viewAllAction;

    // ========================================
    // Constructor
    // ========================================

    /**
     * Initializes the Screen Object with AppiumDriver instance.
     *
     * @param driver AppiumDriver instance (AndroidDriver or IOSDriver)
     */
    public LandingPageTopPanelScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    // ========================================
    // Wait Methods
    // ========================================

    /**
     * Waits for the hero panel to be visible.
     *
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if hero panel appeared within timeout
     */
    public boolean waitForHeroPanelVisible(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(heroTitle));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Waits for the list header to be visible.
     *
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if list header appeared within timeout
     */
    public boolean waitForListHeaderVisible(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(listHeaderTitle));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ========================================
    // Hero Panel Actions
    // ========================================

    /**
     * Taps the "Lost Pet" button in the hero panel.
     * Expected result: App switches to Lost Pet tab.
     */
    public void tapLostPetButton() {
        lostPetButton.click();
        System.out.println("Tapped Lost Pet button");
    }

    /**
     * Taps the "Found Pet" button in the hero panel.
     * Expected result: App switches to Found Pet tab.
     */
    public void tapFoundPetButton() {
        foundPetButton.click();
        System.out.println("Tapped Found Pet button");
    }

    // ========================================
    // List Header Actions
    // ========================================

    /**
     * Taps the "View All" action in the list header.
     * Expected result: App switches to Lost Pet tab (full list).
     */
    public void tapViewAll() {
        viewAllAction.click();
        System.out.println("Tapped View All");
    }

    // ========================================
    // Verification Methods
    // ========================================

    /**
     * Checks if the hero panel title is displayed.
     *
     * @return true if hero title is visible
     */
    public boolean isHeroTitleDisplayed() {
        try {
            return heroTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the text of the hero panel title.
     *
     * @return Hero title text
     */
    public String getHeroTitleText() {
        try {
            return heroTitle.getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Checks if the "Lost Pet" button is displayed.
     *
     * @return true if Lost Pet button is visible
     */
    public boolean isLostPetButtonDisplayed() {
        try {
            return lostPetButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the "Found Pet" button is displayed.
     *
     * @return true if Found Pet button is visible
     */
    public boolean isFoundPetButtonDisplayed() {
        try {
            return foundPetButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the list header title is displayed.
     *
     * @return true if list header title is visible
     */
    public boolean isListHeaderTitleDisplayed() {
        try {
            return listHeaderTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the text of the list header title.
     *
     * @return List header title text
     */
    public String getListHeaderTitleText() {
        try {
            return listHeaderTitle.getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Checks if the "View All" action is displayed.
     *
     * @return true if View All is visible
     */
    public boolean isViewAllDisplayed() {
        try {
            return viewAllAction.isDisplayed();
        } catch (Exception e) {
            return false;
        }
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

    /**
     * Verifies all hero panel elements are visible.
     *
     * @return true if title and both buttons are visible
     */
    public boolean isHeroPanelComplete() {
        return isHeroTitleDisplayed() &&
               isLostPetButtonDisplayed() &&
               isFoundPetButtonDisplayed();
    }

    /**
     * Verifies all list header elements are visible.
     *
     * @return true if title and View All are visible
     */
    public boolean isListHeaderComplete() {
        return isListHeaderTitleDisplayed() &&
               isViewAllDisplayed();
    }
}

