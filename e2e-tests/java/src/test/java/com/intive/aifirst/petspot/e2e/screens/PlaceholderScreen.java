package com.intive.aifirst.petspot.e2e.screens;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.intive.aifirst.petspot.e2e.utils.WaitUtil;

/**
 * Screen object for placeholder screen verification.
 * Used to verify unimplemented tab destinations show "Coming soon" message.
 */
public class PlaceholderScreen {
    private final AppiumDriver driver;
    private final WaitUtil waitUtil;

    private static final String COMING_SOON_TAG = "placeholder.comingSoonText";

    public PlaceholderScreen(AppiumDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    /**
     * Checks if the "Coming soon" text is displayed.
     * @return true if the placeholder text is visible
     */
    public boolean isComingSoonTextDisplayed() {
        try {
            WebElement comingSoonText = waitUtil.waitForElementToBeVisible(
                By.xpath("//*[@content-desc='" + COMING_SOON_TAG + "']")
            );
            return comingSoonText.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Waits for the placeholder screen to be displayed.
     */
    public void waitForPlaceholderScreen() {
        waitUtil.waitForElementToBeVisible(
            By.xpath("//*[@content-desc='" + COMING_SOON_TAG + "']")
        );
    }
}

