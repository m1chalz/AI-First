package com.intive.aifirst.petspot.e2e.screens;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.intive.aifirst.petspot.e2e.utils.WaitUtil;

/**
 * Screen object for bottom navigation bar interactions.
 * Provides methods to tap tabs and verify their selection state.
 */
public class BottomNavigationScreen {
    private final AppiumDriver driver;
    private final WaitUtil waitUtil;

    // Test tag locators following the convention: bottomNav.{tabId}
    private static final String HOME_TAB_TAG = "bottomNav.homeTab";
    private static final String LOST_PET_TAB_TAG = "bottomNav.lostPetTab";
    private static final String FOUND_PET_TAB_TAG = "bottomNav.foundPetTab";
    private static final String CONTACT_TAB_TAG = "bottomNav.contactTab";
    private static final String ACCOUNT_TAB_TAG = "bottomNav.accountTab";

    public BottomNavigationScreen(AppiumDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    /**
     * Taps the Home tab.
     */
    public void tapHomeTab() {
        WebElement tab = waitUtil.waitForElementToBeClickable(
            By.xpath("//*[@content-desc='" + HOME_TAB_TAG + "']")
        );
        tab.click();
    }

    /**
     * Taps the Lost Pet tab.
     */
    public void tapLostPetTab() {
        WebElement tab = waitUtil.waitForElementToBeClickable(
            By.xpath("//*[@content-desc='" + LOST_PET_TAB_TAG + "']")
        );
        tab.click();
    }

    /**
     * Taps the Found Pet tab.
     */
    public void tapFoundPetTab() {
        WebElement tab = waitUtil.waitForElementToBeClickable(
            By.xpath("//*[@content-desc='" + FOUND_PET_TAB_TAG + "']")
        );
        tab.click();
    }

    /**
     * Taps the Contact Us tab.
     */
    public void tapContactTab() {
        WebElement tab = waitUtil.waitForElementToBeClickable(
            By.xpath("//*[@content-desc='" + CONTACT_TAB_TAG + "']")
        );
        tab.click();
    }

    /**
     * Taps the Account tab.
     */
    public void tapAccountTab() {
        WebElement tab = waitUtil.waitForElementToBeClickable(
            By.xpath("//*[@content-desc='" + ACCOUNT_TAB_TAG + "']")
        );
        tab.click();
    }

    /**
     * Taps a tab by name.
     * @param tabName The display name of the tab (Home, Lost Pet, Found Pet, Contact Us, Account)
     */
    public void tapTabByName(String tabName) {
        switch (tabName) {
            case "Home" -> tapHomeTab();
            case "Lost Pet" -> tapLostPetTab();
            case "Found Pet" -> tapFoundPetTab();
            case "Contact Us" -> tapContactTab();
            case "Account" -> tapAccountTab();
            default -> throw new IllegalArgumentException("Unknown tab: " + tabName);
        }
    }

    /**
     * Checks if the Home tab is selected.
     * @return true if Home tab is selected
     */
    public boolean isHomeTabSelected() {
        return isTabSelected(HOME_TAB_TAG);
    }

    /**
     * Checks if the Lost Pet tab is selected.
     * @return true if Lost Pet tab is selected
     */
    public boolean isLostPetTabSelected() {
        return isTabSelected(LOST_PET_TAB_TAG);
    }

    /**
     * Checks if the Found Pet tab is selected.
     * @return true if Found Pet tab is selected
     */
    public boolean isFoundPetTabSelected() {
        return isTabSelected(FOUND_PET_TAB_TAG);
    }

    /**
     * Checks if the Contact Us tab is selected.
     * @return true if Contact Us tab is selected
     */
    public boolean isContactTabSelected() {
        return isTabSelected(CONTACT_TAB_TAG);
    }

    /**
     * Checks if the Account tab is selected.
     * @return true if Account tab is selected
     */
    public boolean isAccountTabSelected() {
        return isTabSelected(ACCOUNT_TAB_TAG);
    }

    /**
     * Checks if a specific tab is selected by name.
     * @param tabName The display name of the tab
     * @return true if the tab is selected
     */
    public boolean isTabSelectedByName(String tabName) {
        return switch (tabName) {
            case "Home" -> isHomeTabSelected();
            case "Lost Pet" -> isLostPetTabSelected();
            case "Found Pet" -> isFoundPetTabSelected();
            case "Contact Us" -> isContactTabSelected();
            case "Account" -> isAccountTabSelected();
            default -> throw new IllegalArgumentException("Unknown tab: " + tabName);
        };
    }

    /**
     * Checks if the bottom navigation bar is visible.
     * @return true if the navigation bar is visible
     */
    public boolean isNavigationBarVisible() {
        try {
            WebElement homeTab = driver.findElement(
                By.xpath("//*[@content-desc='" + HOME_TAB_TAG + "']")
            );
            return homeTab.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTabSelected(String tabTag) {
        try {
            WebElement tab = driver.findElement(
                By.xpath("//*[@content-desc='" + tabTag + "']")
            );
            // Check selected attribute - this may need adjustment based on actual implementation
            String selected = tab.getAttribute("selected");
            return "true".equals(selected);
        } catch (Exception e) {
            return false;
        }
    }
}

