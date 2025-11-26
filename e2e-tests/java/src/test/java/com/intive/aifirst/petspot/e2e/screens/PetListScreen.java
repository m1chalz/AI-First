package com.intive.aifirst.petspot.e2e.screens;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Screen Object Model for Pet List screen (Android + iOS).
 * 
 * <p>This class uses dual annotations to support both Android and iOS platforms:
 * <ul>
 *   <li>@AndroidFindBy: UiAutomator2 selectors for Android (testTag attributes)</li>
 *   <li>@iOSXCUITFindBy: XCUITest selectors for iOS (accessibilityIdentifier)</li>
 * </ul>
 * 
 * <h2>Locator Strategy:</h2>
 * <ul>
 *   <li>Android: {@code new UiSelector().resourceId("petList.element.action")}</li>
 *   <li>iOS: {@code accessibilityIdentifier = "petList.element.action"}</li>
 *   <li>Both platforms use the same test ID pattern for consistency</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // In step definitions
 * AppiumDriver driver = AppiumDriverManager.getDriver("Android"); // or "iOS"
 * PetListScreen petListScreen = new PetListScreen(driver);
 * 
 * // Interact with screen
 * petListScreen.tapSearchInput();
 * petListScreen.enterSearchText("dog");
 * boolean hasResults = petListScreen.isPetListDisplayed();
 * }</pre>
 * 
 * @see com.intive.aifirst.petspot.e2e.steps.mobile.PetListMobileSteps
 */
public class PetListScreen {
    
    private AppiumDriver driver;
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    // ========================================
    // Element Locators (Dual Annotations)
    // ========================================
    
    /**
     * Pet list container element (scrollable list).
     * Android: Finds by resource ID "petList.list"
     * iOS: Finds by accessibility identifier "petList.list"
     */
    @AndroidFindBy(accessibility = "petList.list")
    @iOSXCUITFindBy(accessibility = "petList.list")
    private WebElement petList;
    
    /**
     * Search input field for filtering pets by species.
     * Android: Finds by accessibility ID "petList.searchInput"
     * iOS: Finds by accessibility identifier "petList.searchInput"
     */
    @AndroidFindBy(accessibility = "petList.searchInput")
    @iOSXCUITFindBy(accessibility = "petList.searchInput")
    private WebElement searchInput;
    
    /**
     * Add button for creating new pet announcements.
     * Android: Finds by accessibility ID "petList.addButton.click"
     * iOS: Finds by accessibility identifier "petList.addButton.click"
     */
    @AndroidFindBy(accessibility = "petList.addButton.click")
    @iOSXCUITFindBy(accessibility = "petList.addButton.click")
    private WebElement addButton;
    
    /**
     * Empty state message element.
     * Displayed when no pets match the search criteria.
     */
    @AndroidFindBy(accessibility = "petList.emptyState")
    @iOSXCUITFindBy(accessibility = "petList.emptyState")
    private WebElement emptyStateMessage;
    
    // ========================================
    // Constructor
    // ========================================
    
    /**
     * Initializes the Screen Object with AppiumDriver instance.
     * 
     * @param driver AppiumDriver instance (AndroidDriver or IOSDriver)
     */
    public PetListScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    // ========================================
    // Screen Actions
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
     * Taps on the search input field to activate it.
     */
    public void tapSearchInput() {
        searchInput.click();
        System.out.println("Tapped search input");
    }
    
    /**
     * Enters text into the search field.
     * 
     * @param searchText Text to search for (e.g., "dog", "cat")
     */
    public void enterSearchText(String searchText) {
        searchInput.clear();
        searchInput.sendKeys(searchText);
        System.out.println("Entered search text: " + searchText);
    }
    
    /**
     * Taps on the first pet in the list.
     * Used for navigation to pet details screen.
     */
    public void tapFirstPet() {
        List<WebElement> petItems = getPetItems();
        if (!petItems.isEmpty()) {
            petItems.get(0).click();
            System.out.println("Tapped first pet in the list");
        } else {
            throw new RuntimeException("No pets found in list - cannot tap first pet");
        }
    }
    
    /**
     * Taps the add button to create a new pet announcement.
     */
    public void tapAddButton() {
        addButton.click();
        System.out.println("Tapped add button");
    }
    
    /**
     * Scrolls down the pet list.
     * Uses platform-agnostic scrolling (works on both Android and iOS).
     */
    public void scrollDown() {
        // Get screen dimensions
        int startX = driver.manage().window().getSize().getWidth() / 2;
        int startY = (int) (driver.manage().window().getSize().getHeight() * 0.8);
        int endY = (int) (driver.manage().window().getSize().getHeight() * 0.2);
        
        // Perform swipe gesture (platform-agnostic)
        // Note: In Appium 9.x, use W3C Actions API instead of TouchAction
        try {
            // For now, using sendKeys as a placeholder - actual scroll implementation
            // would use W3C Actions or platform-specific scroll methods
            System.out.println("Scrolling down (platform-agnostic implementation)");
            // TODO: Implement W3C Actions scroll in Phase 5
        } catch (Exception e) {
            System.err.println("Scroll failed: " + e.getMessage());
        }
    }
    
    /**
     * Hides the keyboard (Android) or dismisses it (iOS).
     * Platform-agnostic method that works on both platforms.
     */
    public void hideKeyboard() {
        try {
            // Appium 9.x: Use platform-specific methods
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
            } else if (driver instanceof io.appium.java_client.ios.IOSDriver) {
                ((io.appium.java_client.ios.IOSDriver) driver).hideKeyboard();
            }
            System.out.println("Keyboard hidden");
        } catch (Exception e) {
            System.out.println("Keyboard already hidden or not present");
        }
    }
    
    // ========================================
    // Verification Methods
    // ========================================
    
    /**
     * Checks if the pet list is displayed on the screen.
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
     * @param expectedSpecies Expected species name
     * @return true if all pets match the species
     */
    public boolean allPetsMatchSpecies(String expectedSpecies) {
        List<WebElement> petItems = getPetItems();
        
        for (WebElement pet : petItems) {
            try {
                // Find species text within each pet item (platform-agnostic)
                String speciesIdentifier = "petList.item.species";
                WebElement speciesElement = pet.findElement(
                    io.appium.java_client.AppiumBy.accessibilityId(speciesIdentifier)
                );
                String actualSpecies = speciesElement.getText().toLowerCase();
                
                if (!actualSpecies.contains(expectedSpecies.toLowerCase())) {
                    System.err.println("Found pet with species: " + actualSpecies + 
                                     " (expected: " + expectedSpecies + ")");
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
     * Checks if each pet displays required information (name, species, image).
     * 
     * @return true if all pets have complete information
     */
    public boolean allPetsHaveCompleteInfo() {
        List<WebElement> petItems = getPetItems();
        
        for (WebElement pet : petItems) {
            try {
                // Check for name element
                pet.findElement(io.appium.java_client.AppiumBy.accessibilityId("petList.item.name"));
                
                // Check for species element
                pet.findElement(io.appium.java_client.AppiumBy.accessibilityId("petList.item.species"));
                
                // Check for image element
                pet.findElement(io.appium.java_client.AppiumBy.accessibilityId("petList.item.image"));
                
            } catch (Exception e) {
                System.err.println("Pet missing required information: " + e.getMessage());
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if a pet at the specified position is displayed.
     * 
     * @param position Pet position (1-indexed)
     * @return true if pet at position is visible
     */
    public boolean isPetAtPositionDisplayed(int position) {
        List<WebElement> petItems = getPetItems();
        return petItems.size() >= position && petItems.get(position - 1).isDisplayed();
    }
    
    // ========================================
    // Helper Methods
    // ========================================
    
    /**
     * Gets all pet item elements from the list.
     * Uses accessibility ID pattern to find elements with pet item identifiers.
     * 
     * @return List of WebElements representing pet items
     */
    private List<WebElement> getPetItems() {
        // Pattern: petList.item.{id} where {id} is the pet ID
        // Using partial match for accessibility ID
        return driver.findElements(
            io.appium.java_client.AppiumBy.xpath("//*[contains(@content-desc, 'petList.item.') or contains(@name, 'petList.item.')]")
        );
    }
}

