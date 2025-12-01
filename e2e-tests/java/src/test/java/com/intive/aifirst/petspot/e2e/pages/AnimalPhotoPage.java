package com.intive.aifirst.petspot.e2e.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for Animal Photo Screen in the Report Missing Pet flow.
 * Handles interactions with photo upload, validation, and navigation elements.
 */
public class AnimalPhotoPage {
    
    private WebDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    @FindBy(xpath = "//*[@data-testid='animalPhoto.dropZone.area']")
    private WebElement dropZone;
    
    @FindBy(xpath = "//*[@data-testid='animalPhoto.browse.click']")
    private WebElement browseButton;
    
    @FindBy(xpath = "//*[@data-testid='animalPhoto.fileInput.field']")
    private WebElement fileInput;
    
    @FindBy(xpath = "//*[@data-testid='animalPhoto.continue.click']")
    private WebElement continueButton;
    
    @FindBy(xpath = "//*[@data-testid='animalPhoto.confirmationCard']")
    private WebElement confirmationCard;
    
    @FindBy(xpath = "//*[@data-testid='animalPhoto.filename.text']")
    private WebElement filenameText;
    
    @FindBy(xpath = "//*[@data-testid='animalPhoto.filesize.text']")
    private WebElement filesizeText;
    
    @FindBy(xpath = "//*[@data-testid='animalPhoto.remove.click']")
    private WebElement removeButton;
    
    @FindBy(xpath = "//*[@data-testid='toast.message']")
    private WebElement toastMessage;
    
    public AnimalPhotoPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Clicks the browse button to open file picker dialog.
     */
    public void clickBrowseButton() {
        waitForElementClickable(browseButton, DEFAULT_WAIT_TIMEOUT);
        browseButton.click();
    }
    
    /**
     * Uploads a file using the hidden file input element.
     * @param filePath Path to the file to upload
     */
    public void uploadFile(String filePath) {
        waitForElementVisible(fileInput, DEFAULT_WAIT_TIMEOUT);
        fileInput.sendKeys(filePath);
    }
    
    /**
     * Clicks the continue button to proceed to the next step.
     */
    public void clickContinueButton() {
        waitForElementClickable(continueButton, DEFAULT_WAIT_TIMEOUT);
        continueButton.click();
    }
    
    /**
     * Clicks the remove button to clear the uploaded photo.
     */
    public void clickRemoveButton() {
        waitForElementClickable(removeButton, DEFAULT_WAIT_TIMEOUT);
        removeButton.click();
    }
    
    /**
     * Gets the filename text from the confirmation card.
     * @return The filename displayed in the confirmation card
     */
    public String getFilename() {
        waitForElementVisible(filenameText, DEFAULT_WAIT_TIMEOUT);
        return filenameText.getText();
    }
    
    /**
     * Gets the file size text from the confirmation card.
     * @return The file size displayed in the confirmation card
     */
    public String getFileSize() {
        waitForElementVisible(filesizeText, DEFAULT_WAIT_TIMEOUT);
        return filesizeText.getText();
    }
    
    /**
     * Gets the toast notification message.
     * @return The toast message text
     */
    public String getToastMessage() {
        try {
            waitForElementVisible(toastMessage, 5);
            return toastMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Checks if the confirmation card is displayed.
     * @return true if confirmation card is visible, false otherwise
     */
    public boolean isConfirmationCardDisplayed() {
        try {
            waitForElementVisible(confirmationCard, 5);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if the upload card (drop zone) is displayed.
     * @return true if upload card is visible, false otherwise
     */
    public boolean isUploadCardDisplayed() {
        try {
            waitForElementVisible(dropZone, 5);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if the toast notification is displayed.
     * @return true if toast is visible, false otherwise
     */
    public boolean isToastDisplayed() {
        try {
            waitForElementVisible(toastMessage, 3);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if the continue button is enabled.
     * @return true if continue button is clickable, false otherwise
     */
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

