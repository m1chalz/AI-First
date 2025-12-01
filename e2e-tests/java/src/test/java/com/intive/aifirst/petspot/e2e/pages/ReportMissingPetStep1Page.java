package com.intive.aifirst.petspot.e2e.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ReportMissingPetStep1Page {
    
    private WebDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    @FindBy(xpath = "//*[@data-testid='reportMissingPet.header.backButton.click']")
    private WebElement backButton;
    
    @FindBy(xpath = "//*[@data-testid='reportMissingPet.step1.microchipInput.field']")
    private WebElement microchipInput;
    
    @FindBy(xpath = "//*[@data-testid='reportMissingPet.step1.continueButton.click']")
    private WebElement continueButton;
    
    public ReportMissingPetStep1Page(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    public void clickBackButton() {
        waitForElementClickable(backButton, DEFAULT_WAIT_TIMEOUT);
        backButton.click();
    }
    
    public void enterMicrochipNumber(String microchipNumber) {
        waitForElementVisible(microchipInput, DEFAULT_WAIT_TIMEOUT);
        microchipInput.clear();
        microchipInput.sendKeys(microchipNumber);
    }
    
    public void clickContinueButton() {
        waitForElementClickable(continueButton, DEFAULT_WAIT_TIMEOUT);
        continueButton.click();
    }
    
    public String getMicrochipInputValue() {
        waitForElementVisible(microchipInput, DEFAULT_WAIT_TIMEOUT);
        return microchipInput.getAttribute("value");
    }
    
    public boolean isBackButtonDisplayed() {
        try {
            return backButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isContinueButtonEnabled() {
        try {
            waitForElementVisible(continueButton, DEFAULT_WAIT_TIMEOUT);
            return continueButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    private void waitForElementVisible(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    private void waitForElementClickable(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
}

