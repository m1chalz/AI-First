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
 * Screen Object dedicated to the Microchip Number screen (step 1/4) of the missing pet flow.
 * Provides typed accessors for the microchip input field, continue button, and modal back button.
 */
public class MicrochipNumberScreen {

    private static final int DEFAULT_WAIT_TIMEOUT = 10;

    private final AppiumDriver driver;

    @AndroidFindBy(accessibility = "missingPet.microchip.input")
    @iOSXCUITFindBy(id = "missingPet.microchip.input")
    private WebElement microchipInput;

    @AndroidFindBy(accessibility = "missingPet.microchip.continueButton")
    @iOSXCUITFindBy(id = "missingPet.microchip.continueButton")
    private WebElement continueButton;

    @AndroidFindBy(accessibility = "missingPet.microchip.backButton")
    @iOSXCUITFindBy(id = "missingPet.microchip.backButton")
    private WebElement backButton;

    public MicrochipNumberScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT)), this);
    }

    public boolean isDisplayed() {
        try {
            waitForElement(microchipInput);
            return microchipInput.isDisplayed();
        } catch (Exception exception) {
            return false;
        }
    }

    public void typeMicrochipNumber(String digits) {
        waitForElement(microchipInput);
        microchipInput.clear();
        microchipInput.sendKeys(digits);
    }

    public void clearMicrochipNumber() {
        waitForElement(microchipInput);
        microchipInput.clear();
    }

    public String getMicrochipValue() {
        waitForElement(microchipInput);
        String value = microchipInput.getAttribute("value");
        if (value == null) {
            return microchipInput.getText();
        }
        return value;
    }

    public void tapContinueButton() {
        waitForElement(continueButton);
        continueButton.click();
    }

    public void tapBackButton() {
        waitForElement(backButton);
        backButton.click();
    }

    public boolean isBackButtonDisplayed() {
        try {
            return backButton.isDisplayed();
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean isContinueButtonDisplayed() {
        try {
            return continueButton.isDisplayed();
        } catch (Exception exception) {
            return false;
        }
    }

    private void waitForElement(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
        wait.until(ExpectedConditions.visibilityOf(element));
    }
}

