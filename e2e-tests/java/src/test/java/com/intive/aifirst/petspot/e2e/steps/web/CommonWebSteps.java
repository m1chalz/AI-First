package com.intive.aifirst.petspot.e2e.steps.web;

import com.intive.aifirst.petspot.e2e.utils.TestConfig;
import com.intive.aifirst.petspot.e2e.utils.WebDriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Common step definitions for web scenarios.
 * 
 * <p>This class contains reusable Cucumber step definitions that can be shared
 * across multiple web features:
 * <ul>
 *   <li>Browser navigation steps</li>
 *   <li>URL verification steps</li>
 *   <li>Page title verification steps</li>
 *   <li>Common UI interaction steps</li>
 * </ul>
 * 
 * <h2>Purpose:</h2>
 * <p>Avoid duplication by centralizing common steps that appear in multiple
 * feature files. Feature-specific steps should go in dedicated step classes
 * (e.g., {@link PetListWebSteps}).
 * 
 * <h2>Example Usage:</h2>
 * <pre>
 * Given I navigate to "/pets"
 * Then the page title should contain "PetSpot"
 * And the current URL should contain "/pets"
 * </pre>
 * 
 * @see PetListWebSteps
 * @see com.intive.aifirst.petspot.e2e.utils.WebDriverManager
 */
public class CommonWebSteps {
    
    private WebDriver driver;
    
    /**
     * Constructor - initializes WebDriver.
     * Called by Cucumber framework before scenario execution.
     */
    public CommonWebSteps() {
        this.driver = WebDriverManager.getDriver();
    }
    
    // ========================================
    // Navigation Steps
    // ========================================
    
    /**
     * Navigates to a specific path within the application.
     * 
     * <p>Maps to Gherkin: "Given I navigate to {string}"
     * 
     * @param path Relative path (e.g., "/pets", "/pets/123")
     */
    @Given("I navigate to {string}")
    public void navigateToPath(String path) {
        String baseUrl = TestConfig.getWebBaseUrl();
        String fullUrl = baseUrl + path;
        driver.get(fullUrl);
        System.out.println("Navigated to: " + fullUrl);
    }
    
    /**
     * Navigates to the application home page.
     * 
     * <p>Maps to Gherkin: "Given I am on the home page"
     */
    @Given("I am on the home page")
    public void navigateToHomePage() {
        String baseUrl = TestConfig.getWebBaseUrl();
        driver.get(baseUrl);
        System.out.println("Navigated to home page: " + baseUrl);
    }
    
    /**
     * Refreshes the current page.
     * 
     * <p>Maps to Gherkin: "When I refresh the page"
     */
    @When("I refresh the page")
    public void refreshPage() {
        driver.navigate().refresh();
        System.out.println("Page refreshed");
    }
    
    /**
     * Navigates back to the previous page.
     * 
     * <p>Maps to Gherkin: "When I go back to the previous page"
     */
    @When("I go back to the previous page")
    public void navigateBack() {
        driver.navigate().back();
        System.out.println("Navigated back to previous page");
    }
    
    // ========================================
    // URL Verification Steps
    // ========================================
    
    /**
     * Verifies that the current URL contains expected text.
     * 
     * <p>Maps to Gherkin: "Then the current URL should contain {string}"
     * 
     * @param expectedUrlPart Expected substring in URL
     */
    @Then("the current URL should contain {string}")
    public void urlShouldContain(String expectedUrlPart) {
        String actualUrl = driver.getCurrentUrl();
        assertTrue(actualUrl.contains(expectedUrlPart),
            "Current URL should contain '" + expectedUrlPart + "' but was: " + actualUrl);
        System.out.println("Verified: URL contains '" + expectedUrlPart + "'");
    }
    
    /**
     * Verifies that the current URL equals expected value.
     * 
     * <p>Maps to Gherkin: "Then the current URL should be {string}"
     * 
     * @param expectedUrl Expected exact URL
     */
    @Then("the current URL should be {string}")
    public void urlShouldEqual(String expectedUrl) {
        String baseUrl = TestConfig.getWebBaseUrl();
        String fullExpectedUrl = expectedUrl.startsWith("http") ? 
            expectedUrl : baseUrl + expectedUrl;
        
        String actualUrl = driver.getCurrentUrl();
        assertEquals(fullExpectedUrl, actualUrl,
            "Current URL should match expected URL");
        System.out.println("Verified: URL equals " + fullExpectedUrl);
    }
    
    // ========================================
    // Page Title Verification Steps
    // ========================================
    
    /**
     * Verifies that the page title contains expected text.
     * 
     * <p>Maps to Gherkin: "Then the page title should contain {string}"
     * 
     * @param expectedTitlePart Expected substring in page title
     */
    @Then("the page title should contain {string}")
    public void pageTitleShouldContain(String expectedTitlePart) {
        String actualTitle = driver.getTitle();
        assertTrue(actualTitle.contains(expectedTitlePart),
            "Page title should contain '" + expectedTitlePart + "' but was: " + actualTitle);
        System.out.println("Verified: Page title contains '" + expectedTitlePart + "'");
    }
    
    /**
     * Verifies that the page title equals expected value.
     * 
     * <p>Maps to Gherkin: "Then the page title should be {string}"
     * 
     * @param expectedTitle Expected exact page title
     */
    @Then("the page title should be {string}")
    public void pageTitleShouldEqual(String expectedTitle) {
        String actualTitle = driver.getTitle();
        assertEquals(expectedTitle, actualTitle,
            "Page title should match expected title");
        System.out.println("Verified: Page title equals '" + expectedTitle + "'");
    }
    
    // ========================================
    // Wait Steps
    // ========================================
    
    /**
     * Waits for a specified number of seconds.
     * 
     * <p>Maps to Gherkin: "When I wait {int} second(s)"
     * 
     * <p><strong>Note:</strong> Use explicit waits instead when possible.
     * This step is provided for edge cases where a fixed delay is necessary.
     * 
     * @param seconds Number of seconds to wait
     */
    @When("I wait {int} second(s)")
    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            System.out.println("Waited " + seconds + " second(s)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Wait interrupted: " + e.getMessage());
        }
    }
    
    // ========================================
    // Browser Control Steps
    // ========================================
    
    /**
     * Maximizes the browser window.
     * 
     * <p>Maps to Gherkin: "Given the browser window is maximized"
     */
    @Given("the browser window is maximized")
    public void maximizeBrowserWindow() {
        driver.manage().window().maximize();
        System.out.println("Browser window maximized");
    }
    
    /**
     * Closes the current browser tab/window.
     * 
     * <p>Maps to Gherkin: "When I close the current tab"
     * 
     * <p><strong>Warning:</strong> If this is the only window, WebDriver will be in an invalid state.
     */
    @When("I close the current tab")
    public void closeCurrentTab() {
        driver.close();
        System.out.println("Closed current browser tab");
    }
    
    // ========================================
    // Screenshot and Debugging Steps
    // ========================================
    
    /**
     * Prints the current page source to console (for debugging).
     * 
     * <p>Maps to Gherkin: "When I print the page source"
     */
    @When("I print the page source")
    public void printPageSource() {
        String pageSource = driver.getPageSource();
        System.out.println("========== PAGE SOURCE ==========");
        System.out.println(pageSource);
        System.out.println("=================================");
    }
    
    /**
     * Prints the current URL to console (for debugging).
     * 
     * <p>Maps to Gherkin: "When I print the current URL"
     */
    @When("I print the current URL")
    public void printCurrentUrl() {
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);
    }
}

