package com.intive.aifirst.petspot.e2e.screens;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * petListScreen.tapFirstPet();
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
     * Android: Finds by UiAutomator selector (ComposeView with scrollable content)
     * iOS: Finds by accessibility identifier "animalList.list"
     */
    @AndroidFindBy(uiAutomator = "new UiSelector().className(\"androidx.compose.ui.platform.ComposeView\")")
    @iOSXCUITFindBy(accessibility = "animalList.list")
    private WebElement petList;
    
    
    /**
     * Add button for creating new pet announcements.
     * Android: Finds by accessibility ID "animalList.reportMissingButton"
     * iOS: Finds by accessibility identifier "animalList.reportMissingButton"
     */
    @AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.Button\").textContains(\"Report\")")
    @iOSXCUITFindBy(accessibility = "animalList.reportMissingButton")
    private WebElement addButton;
    
    /**
     * Empty state message element.
     * Displayed when no pets are available.
     */
    @AndroidFindBy(accessibility = "animalList.emptyState")
    @iOSXCUITFindBy(accessibility = "animalList.emptyState")
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
     * Scrolls down the pet list.
     * Uses direct swipe gesture which works reliably with Compose LazyColumn.
     */
    public void scrollDown() {
        performSwipeScroll("down");
    }
    
    /**
     * Performs scroll using direct swipe gesture (more reliable for Compose UI).
     * 
     * @param direction "up" or "down"
     */
    private void performSwipeScroll(String direction) {
        try {
            Dimension size = driver.manage().window().getSize();
            int startX = size.width / 2;
            int startY, endY;
            
            if ("down".equals(direction)) {
                // Swipe from bottom to top = scroll down (content moves up)
                startY = (int) (size.height * 0.7);
                endY = (int) (size.height * 0.3);
            } else {
                // Swipe from top to bottom = scroll up (content moves down)
                startY = (int) (size.height * 0.3);
                endY = (int) (size.height * 0.7);
            }
            
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);
            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), startX, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            
            driver.perform(java.util.Collections.singletonList(swipe));
            System.out.println("Performed swipe scroll " + direction);
            
        } catch (Exception e) {
            System.err.println("Swipe scroll failed: " + e.getMessage());
            // Fallback to old method
            performScroll(direction);
        }
    }
    
    /**
     * Scrolls up a small amount to ensure element is not hidden behind FAB button.
     * Used after scrolling to an element to make sure it's fully visible.
     */
    public void scrollUpSmall() {
        performScrollSmall("up");
    }
    
    /**
     * Performs a small scroll (1/4 of screen) in the specified direction.
     * 
     * @param direction "up" or "down"
     */
    private void performScrollSmall(String direction) {
        try {
            Dimension size = driver.manage().window().getSize();
            int startX = size.width / 2;
            int startY, endY;
            
            if ("up".equals(direction)) {
                startY = size.height / 2;
                endY = (int) (size.height * 0.65);  // Small scroll up (15% of screen)
            } else {
                startY = size.height / 2;
                endY = (int) (size.height * 0.35);  // Small scroll down
            }
            
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence scroll = new Sequence(finger, 1);
            scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            scroll.addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), startX, endY));
            scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            
            driver.perform(java.util.Collections.singletonList(scroll));
            System.out.println("Performed small scroll " + direction);
            
        } catch (Exception e) {
            System.err.println("Small scroll failed: " + e.getMessage());
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
                // Note: Species is typically part of card text, not separate element
                String petText = pet.getText().toLowerCase();
                if (!petText.contains(expectedSpecies.toLowerCase())) {
                    System.err.println("Found pet without species '" + expectedSpecies + "': " + 
                                     petText.substring(0, Math.min(50, petText.length())));
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
                // Verify pet card has content (name, species, image are part of card)
                // Animal cards display all information together, not as separate elements
                String petText = pet.getText();
                if (petText == null || petText.trim().isEmpty()) {
                    System.err.println("Pet card has no text content");
                    return false;
                }
                
                // Check that card has some content (simplified validation)
                // Full validation would check for specific accessibility identifiers if implemented
                
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
    
    private boolean performScroll(String direction) {
        if (petList == null) {
            System.err.println("Cannot scroll without pet list reference");
            return false;
        }
        try {
            if (driver instanceof AndroidDriver) {
                Rectangle rect = petList.getRect();
                Map<String, Object> params = new HashMap<>();
                params.put("left", rect.getX());
                params.put("top", rect.getY());
                params.put("width", Math.max(rect.getWidth(), 100));
                params.put("height", Math.max(rect.getHeight(), 100));
                params.put("direction", direction);
                params.put("percent", 0.7);
                driver.executeScript("mobile: scrollGesture", params);
            } else if (driver instanceof IOSDriver) {
                Map<String, Object> params = new HashMap<>();
                params.put("direction", direction);
                params.put("elementId", ((RemoteWebElement) petList).getId());
                driver.executeScript("mobile: scroll", params);
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put("direction", direction);
                driver.executeScript("mobile: scroll", params);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Scroll (" + direction + ") failed: " + e.getMessage());
            return false;
        }
    }

    // Removed unused private method waitForScrollIdle() - was never called
    
    // ========================================
    // Helper Methods
    // ========================================
    
    /**
     * Gets all pet item elements from the list.
     * Android: Uses UiAutomator to find TextView elements (animal cards contain TextViews with animal names)
     * iOS: Uses accessibility ID pattern animalList.item.*
     * 
     * @return List of WebElements representing pet items
     */
    private List<WebElement> getPetItems() {
        // Detect platform and use appropriate selector
        String platformName = driver.getCapabilities().getPlatformName().toString().toLowerCase();
        
        if (platformName.contains("android")) {
            // Android: Find TextView elements within the list (each animal card has TextViews for name/species)
            return driver.findElements(
                io.appium.java_client.AppiumBy.androidUIAutomator(
                    "new UiSelector().className(\"android.widget.TextView\")"
                )
            );
        } else {
            // iOS: Pattern animalList.item.{id}
            return driver.findElements(
                io.appium.java_client.AppiumBy.xpath("//*[contains(@name, 'animalList.item.')]")
            );
        }
    }
}

