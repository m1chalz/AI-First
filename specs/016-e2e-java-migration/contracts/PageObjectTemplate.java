package com.intive.aifirst.petspot.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model template for web pages.
 * 
 * Purpose: Encapsulates the structure and interactions of a specific web page,
 * abstracting WebDriver API calls and element location logic.
 * 
 * Usage:
 * 1. Copy this template
 * 2. Rename class to match page name (e.g., PetListPage)
 * 3. Add @FindBy annotations for all interactive elements using XPath with data-testid
 * 4. Implement methods for user actions (one method per action)
 * 5. Use in Step Definitions by instantiating with WebDriver
 * 
 * Best Practices:
 * - Use XPath selectors with data-testid attributes: //*[@data-testid='screen.element.action']
 * - One method per user action (click, type, verify)
 * - Return values for verification methods (boolean, int, String, List)
 * - Use explicit waits (WebDriverWait) instead of Thread.sleep()
 * - Keep methods focused and simple
 * - Document complex methods with JavaDoc
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.web Step Definitions that use this Page Object
 */
public class PageObjectTemplate {
    
    private WebDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // Element Locators (using XPath with data-testid)
    // ========================================
    
    /**
     * Example button element.
     * XPath pattern: //*[@data-testid='pageName.elementName.action']
     */
    @FindBy(xpath = "//*[@data-testid='example.button.click']")
    private WebElement exampleButton;
    
    /**
     * Example input field element.
     */
    @FindBy(xpath = "//*[@data-testid='example.input.field']")
    private WebElement exampleInput;
    
    /**
     * Example container element (e.g., list, grid).
     */
    @FindBy(xpath = "//*[@data-testid='example.container']")
    private WebElement exampleContainer;
    
    // ========================================
    // Constructor
    // ========================================
    
    /**
     * Initializes the Page Object with WebDriver and initializes all @FindBy elements.
     * 
     * @param driver Selenium WebDriver instance (from WebDriverManager)
     */
    public PageObjectTemplate(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    // ========================================
    // User Action Methods
    // ========================================
    
    /**
     * Example: Click action method.
     * Pattern: verb + Element (e.g., clickAddButton, clickSubmit)
     */
    public void clickExampleButton() {
        waitForElementClickable(exampleButton, DEFAULT_WAIT_TIMEOUT);
        exampleButton.click();
    }
    
    /**
     * Example: Type/input action method.
     * Pattern: enterXInY or typeX (e.g., enterTextInSearch, typePetName)
     * 
     * @param text Text to enter in the input field
     */
    public void enterTextInExampleInput(String text) {
        waitForElementVisible(exampleInput, DEFAULT_WAIT_TIMEOUT);
        exampleInput.clear();
        exampleInput.sendKeys(text);
    }
    
    // ========================================
    // Verification Methods (return boolean, int, String, List)
    // ========================================
    
    /**
     * Example: Check if element is displayed.
     * Pattern: isXDisplayed or hasX (e.g., isPetListDisplayed, hasErrorMessage)
     * 
     * @return true if element is displayed, false otherwise
     */
    public boolean isExampleButtonDisplayed() {
        try {
            return exampleButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Example: Get count of elements.
     * Pattern: getXCount or countX (e.g., getPetCount, countResults)
     * 
     * @return Number of matching elements
     */
    public int getExampleItemCount() {
        List<WebElement> items = driver.findElements(
            By.xpath("//*[starts-with(@data-testid, 'example.item.')]")
        );
        return items.size();
    }
    
    /**
     * Example: Get text from element.
     * Pattern: getXText or retrieveX (e.g., getTitleText, getErrorMessage)
     * 
     * @return Text content of the element
     */
    public String getExampleText() {
        waitForElementVisible(exampleContainer, DEFAULT_WAIT_TIMEOUT);
        return exampleContainer.getText();
    }
    
    /**
     * Example: Get collection of elements.
     * Pattern: getXList or findXElements (e.g., getPetList, getSearchResults)
     * 
     * @return List of WebElements matching the criteria
     */
    public List<WebElement> getExampleItems() {
        return driver.findElements(
            By.xpath("//*[starts-with(@data-testid, 'example.item.')]")
        );
    }
    
    // ========================================
    // Utility/Helper Methods (private)
    // ========================================
    
    /**
     * Wait for element to be visible.
     * 
     * @param element WebElement to wait for
     * @param timeoutSeconds Maximum wait time in seconds
     */
    private void waitForElementVisible(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Wait for element to be clickable.
     * 
     * @param element WebElement to wait for
     * @param timeoutSeconds Maximum wait time in seconds
     */
    private void waitForElementClickable(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    /**
     * Wait for element to contain specific text.
     * 
     * @param element WebElement to check
     * @param text Expected text
     * @param timeoutSeconds Maximum wait time in seconds
     */
    private void waitForTextInElement(WebElement element, String text, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }
    
    /**
     * Scroll element into view (useful for elements below fold).
     * 
     * @param element WebElement to scroll to
     */
    private void scrollToElement(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
            element
        );
    }
}

