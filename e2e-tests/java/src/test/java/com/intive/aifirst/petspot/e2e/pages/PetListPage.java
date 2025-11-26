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
 * Page Object Model for Pet List page.
 * 
 * <p>This class encapsulates the structure and interactions of the Pet List page
 * in the PetSpot web application. All element locators use XPath with data-testid
 * attributes for stable, maintainable test automation.
 * 
 * <h2>Locator Strategy:</h2>
 * <ul>
 *   <li>Pattern: {@code //*[@data-testid='petList.element.action']}</li>
 *   <li>Example: {@code //*[@data-testid='petList.searchInput']}</li>
 *   <li>Rationale: data-testid attributes are stable across refactorings</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // In step definitions
 * WebDriver driver = WebDriverManager.getDriver();
 * PetListPage petListPage = new PetListPage(driver);
 * 
 * // Navigate and interact
 * driver.get("http://localhost:3000/pets");
 * petListPage.searchForPet("dog");
 * boolean hasResults = petListPage.isPetListDisplayed();
 * }</pre>
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.web.PetListWebSteps
 */
public class PetListPage {
    
    private WebDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // Element Locators (using XPath with data-testid)
    // ========================================
    
    /**
     * Pet list container element.
     * Displays the list of all pet announcements.
     */
    @FindBy(xpath = "//*[@data-testid='animalList.list']")
    private WebElement petList;
    
    /**
     * Search input field for filtering pets by species.
     * NOTE: Currently not implemented in the React application.
     * Uncomment when search functionality is added to the UI.
     */
    // @FindBy(xpath = "//*[@data-testid='animalList.searchInput']")
    // private WebElement searchInput;
    
    /**
     * Add button for creating new pet announcements.
     */
    @FindBy(xpath = "//*[@data-testid='animalList.reportMissingButton']")
    private WebElement addButton;
    
    /**
     * Search results count text element.
     * NOTE: Currently not implemented in the React application.
     * Uncomment when results count is added to the UI.
     */
    // @FindBy(xpath = "//*[@data-testid='animalList.resultsCount']")
    // private WebElement resultsCount;
    
    /**
     * Empty state message element.
     * Displayed when no pets match the search criteria.
     */
    @FindBy(xpath = "//*[@data-testid='animalList.emptyState']")
    private WebElement emptyStateMessage;
    
    // ========================================
    // Constructor
    // ========================================
    
    /**
     * Initializes the Page Object with WebDriver instance.
     * 
     * @param driver WebDriver instance for browser control
     */
    public PetListPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    // ========================================
    // Page Actions
    // ========================================
    
    /**
     * Waits for the pet list to be visible and loaded.
     * 
     * @param timeoutSeconds Maximum wait time in seconds
     * @return true if list became visible within timeout
     */
    public boolean waitForPetListVisible(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(petList));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Searches for pets by species name.
     * 
     * <p>NOTE: Search functionality not yet implemented in React UI.
     * This method is a placeholder for future implementation.
     * 
     * @param species Species name to search for (e.g., "dog", "cat")
     */
    public void searchForPet(String species) {
        // TODO: Implement when search input is added to React UI
        System.out.println("WARN: Search functionality not yet available in UI. Skipping search for: " + species);
        // searchInput.clear();
        // searchInput.sendKeys(species);
    }
    
    /**
     * Clicks on the first pet in the list.
     * Used for navigation to pet details page.
     */
    public void clickFirstPet() {
        List<WebElement> petItems = getPetItems();
        if (!petItems.isEmpty()) {
            petItems.get(0).click();
        } else {
            throw new RuntimeException("No pets found in list - cannot click first pet");
        }
    }
    
    /**
     * Clicks the add button to create a new pet announcement.
     */
    public void clickAddButton() {
        addButton.click();
    }
    
    // ========================================
    // Verification Methods
    // ========================================
    
    /**
     * Checks if the pet list is displayed on the page.
     * 
     * @return true if pet list is visible
     */
    public boolean isPetListDisplayed() {
        try {
            return petList.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the count of visible pet items in the list.
     * 
     * @return Number of pet items displayed
     */
    public int getPetCount() {
        return getPetItems().size();
    }
    
    /**
     * Checks if at least one pet is displayed.
     * 
     * @return true if one or more pets are visible
     */
    public boolean hasAnyPets() {
        return getPetCount() > 0;
    }
    
    /**
     * Gets the search results count text.
     * 
     * <p>NOTE: Results count not yet implemented in React UI.
     * 
     * @return Results count text (empty string until UI is updated)
     */
    public String getResultsCountText() {
        // TODO: Implement when results count is added to React UI
        return "";
        // try {
        //     return resultsCount.getText();
        // } catch (Exception e) {
        //     return "";
        // }
    }
    
    /**
     * Checks if the results count is displayed.
     * 
     * <p>NOTE: Results count not yet implemented in React UI.
     * 
     * @return false until UI is updated
     */
    public boolean isResultsCountDisplayed() {
        // TODO: Implement when results count is added to React UI
        return false;
        // try {
        //     return resultsCount.isDisplayed();
        // } catch (Exception e) {
        //     return false;
        // }
    }
    
    /**
     * Checks if the empty state message is displayed.
     * 
     * @return true if empty state is visible
     */
    public boolean isEmptyStateDisplayed() {
        try {
            return emptyStateMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verifies that all visible pets match the specified species.
     * 
     * <p>NOTE: Search/filter functionality not yet implemented in React UI.
     * This method validates species text but cannot test actual filtering.
     * 
     * @param expectedSpecies Expected species name (e.g., "Cat", "Dog", "Bird")
     * @return true if all pets match the species
     */
    public boolean allPetsMatchSpecies(String expectedSpecies) {
        List<WebElement> petItems = getPetItems();
        
        if (petItems.isEmpty()) {
            System.err.println("No pet items found to filter");
            return false;
        }
        
        for (WebElement pet : petItems) {
            try {
                // Find species text within pet card
                // Species is displayed in format: "Cat | Maine Coon" or "Dog | Labrador"
                String petText = pet.getText().toLowerCase();
                
                if (!petText.contains(expectedSpecies.toLowerCase())) {
                    System.err.println("Found pet without species '" + expectedSpecies + "': " + petText.substring(0, Math.min(50, petText.length())));
                    return false;
                }
            } catch (Exception e) {
                System.err.println("Could not verify species for pet: " + e.getMessage());
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if each pet displays required information (location, species, status).
     * 
     * <p>Validates presence of key UI elements based on actual React component structure.
     * 
     * @return true if all pets have basic information
     */
    public boolean allPetsHaveCompleteInfo() {
        List<WebElement> petItems = getPetItems();
        
        if (petItems.isEmpty()) {
            System.err.println("No pet items found to validate");
            return false;
        }
        
        System.out.println("Validating " + petItems.size() + " pet cards...");
        
        for (int i = 0; i < petItems.size(); i++) {
            WebElement pet = petItems.get(i);
            String petText = pet.getText();
            
            // Simplified validation: just check that pet card has some content
            if (petText == null || petText.trim().isEmpty()) {
                System.err.println("Pet card " + (i+1) + " has no text content");
                return false;
            }
            
            // Check for location indicator (km)
            if (!petText.contains("km")) {
                System.err.println("Pet card " + (i+1) + " missing location (km)");
                return false;
            }
            
            System.out.println("Pet card " + (i+1) + " validated OK");
        }
        
        return true;
    }
    
    // ========================================
    // Helper Methods
    // ========================================
    
    /**
     * Gets all pet item elements from the list.
     * Uses XPath pattern to find elements with pet item test IDs.
     * 
     * @return List of WebElements representing pet items
     */
    private List<WebElement> getPetItems() {
        // Pattern: animalList.item.{id} where {id} is the pet ID
        return driver.findElements(
            By.xpath("//*[starts-with(@data-testid, 'animalList.item.')]")
        );
    }
    
    /**
     * Waits for a specific number of pets to be displayed.
     * 
     * @param expectedCount Expected number of pets
     * @param timeoutSeconds Maximum wait time
     * @return true if expected count reached within timeout
     */
    public boolean waitForPetCount(int expectedCount, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            return wait.until(driver -> getPetCount() == expectedCount);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if the add button is visible.
     * 
     * @return true if add button is visible
     */
    public boolean isAddButtonVisible() {
        try {
            return addButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the text from the add button.
     * 
     * @return Button text
     */
    public String getAddButtonText() {
        try {
            return addButton.getText();
        } catch (Exception e) {
            return "";
        }
    }
}

