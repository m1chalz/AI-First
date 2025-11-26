package com.intive.aifirst.petspot.e2e.steps.web;

import com.intive.aifirst.petspot.e2e.pages.PetListPage;
import com.intive.aifirst.petspot.e2e.utils.TestConfig;
import com.intive.aifirst.petspot.e2e.utils.WebDriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for Pet List web scenarios.
 * 
 * <p>This class implements Cucumber step definitions (Given/When/Then) for
 * pet list management features on the web platform. Steps are matched to
 * Gherkin scenarios via Cucumber expressions.
 * 
 * <h2>Architecture:</h2>
 * <ul>
 *   <li>Step Definitions (this class) → implements Given/When/Then methods</li>
 *   <li>Page Objects ({@link PetListPage}) → encapsulates page structure and actions</li>
 *   <li>WebDriverManager → provides WebDriver instance with ThreadLocal isolation</li>
 * </ul>
 * 
 * <h2>Example Gherkin Mapping:</h2>
 * <pre>
 * Gherkin:  "When I search for "dog""
 * Method:   searchForSpecies(String species)
 * 
 * Gherkin:  "Then I should see at least one pet announcement"
 * Method:   shouldSeeAtLeastOnePet()
 * </pre>
 * 
 * @see PetListPage
 * @see com.intive.aifirst.petspot.e2e.utils.WebDriverManager
 */
public class PetListWebSteps {
    
    private WebDriver driver;
    private PetListPage petListPage;
    
    /**
     * Constructor - initializes WebDriver and Page Object.
     * Called by Cucumber framework before scenario execution.
     */
    public PetListWebSteps() {
        this.driver = WebDriverManager.getDriver();
        this.petListPage = new PetListPage(driver);
    }
    
    // ========================================
    // Given Steps (Setup / Preconditions)
    // ========================================
    
    /**
     * Navigates to the pet list page.
     * 
     * <p>Maps to Gherkin: "Given I am on the pet list page"
     */
    @Given("I am on the pet list page")
    public void navigateToPetListPage() {
        String baseUrl = TestConfig.getWebBaseUrl();
        driver.get(baseUrl + "/pets");
        System.out.println("Navigated to: " + baseUrl + "/pets");
    }
    
    /**
     * Waits for the page to finish loading.
     * 
     * <p>Maps to Gherkin: "And the page has loaded completely"
     */
    @Given("the page has loaded completely")
    public void waitForPageLoad() {
        boolean loaded = petListPage.waitForPetListVisible(10);
        assertTrue(loaded, "Pet list should be visible after page load");
        System.out.println("Page loaded successfully - pet list is visible");
    }
    
    // ========================================
    // When Steps (Actions)
    // ========================================
    
    /**
     * Views the pet list (placeholder - list should already be visible after navigation).
     * 
     * <p>Maps to Gherkin: "When I view the pet list"
     */
    @When("I view the pet list")
    public void viewPetList() {
        // No action needed - list is already visible after navigation
        // This step exists for readability in Gherkin scenarios
        System.out.println("Viewing pet list (already loaded)");
    }
    
    /**
     * Searches for pets by species name.
     * 
     * <p>Maps to Gherkin: "When I search for {string}"
     * 
     * @param species Species name to search for (e.g., "dog", "cat", "bird")
     */
    @When("I search for {string}")
    public void searchForSpecies(String species) {
        petListPage.searchForPet(species);
        System.out.println("Searched for species: " + species);
        
        // Wait a moment for search results to update (implicit wait handles this)
        // In a real app, you might wait for a loading indicator to disappear
    }
    
    /**
     * Clicks on the first pet in the list.
     * 
     * <p>Maps to Gherkin: "When I click on the first pet in the list"
     */
    @When("I click on the first pet in the list")
    public void clickFirstPet() {
        petListPage.clickFirstPet();
        System.out.println("Clicked first pet in the list");
    }
    
    // ========================================
    // Then Steps (Assertions / Verification)
    // ========================================
    
    /**
     * Verifies that at least one pet is displayed.
     * 
     * <p>Maps to Gherkin: "Then I should see at least one pet announcement"
     */
    @Then("I should see at least one pet announcement")
    public void shouldSeeAtLeastOnePet() {
        assertTrue(petListPage.isPetListDisplayed(), 
            "Pet list should be displayed");
        assertTrue(petListPage.hasAnyPets(), 
            "At least one pet should be visible");
        
        int petCount = petListPage.getPetCount();
        System.out.println("Verified: Found " + petCount + " pet(s)");
    }
    
    /**
     * Verifies that each pet displays complete information.
     * 
     * <p>Maps to Gherkin: "And each pet should display name, species, and image"
     */
    @Then("each pet should display name, species, and image")
    public void eachPetShouldHaveCompleteInfo() {
        assertTrue(petListPage.allPetsHaveCompleteInfo(),
            "All pets should display name, species, and image");
        System.out.println("Verified: All pets have complete information");
    }
    
    /**
     * Verifies that only pets of specified species are displayed.
     * 
     * <p>Maps to Gherkin: "Then I should see only dog announcements"
     * and "Then I should see only {string} announcements"
     * 
     * @param species Expected species name
     */
    @Then("I should see only {word} announcements")
    public void shouldSeeOnlySpecificSpecies(String species) {
        assertTrue(petListPage.hasAnyPets(),
            "Search results should not be empty");
        assertTrue(petListPage.allPetsMatchSpecies(species),
            "All visible pets should be " + species + " species");
        
        int count = petListPage.getPetCount();
        System.out.println("Verified: All " + count + " pet(s) are " + species + " species");
    }
    
    /**
     * Verifies that the search results count is displayed.
     * 
     * <p>Maps to Gherkin: "And the search results count should be displayed"
     */
    @Then("the search results count should be displayed")
    public void searchResultsCountDisplayed() {
        assertTrue(petListPage.isResultsCountDisplayed(),
            "Search results count should be visible");
        
        String countText = petListPage.getResultsCountText();
        System.out.println("Verified: Results count displayed: " + countText);
    }
    
    /**
     * Verifies that the announcement count matches the current filter.
     * 
     * <p>Maps to Gherkin: "And the announcement count should match the filter"
     */
    @Then("the announcement count should match the filter")
    public void announcementCountMatchesFilter() {
        int actualCount = petListPage.getPetCount();
        String countText = petListPage.getResultsCountText();
        
        // Extract number from results text (e.g., "3 results" -> 3)
        String numberPart = countText.replaceAll("[^0-9]", "");
        
        if (!numberPart.isEmpty()) {
            int displayedCount = Integer.parseInt(numberPart);
            assertEquals(actualCount, displayedCount,
                "Displayed count should match actual number of pets");
            System.out.println("Verified: Count matches (" + actualCount + " pets)");
        } else {
            fail("Could not extract count from results text: " + countText);
        }
    }
    
    /**
     * Verifies that no pets are displayed (empty results).
     * 
     * <p>Maps to Gherkin: "Then I should see no pet announcements"
     */
    @Then("I should see no pet announcements")
    public void shouldSeeNoPets() {
        assertFalse(petListPage.hasAnyPets(),
            "No pets should be visible for empty search results");
        
        int count = petListPage.getPetCount();
        assertEquals(0, count, "Pet count should be zero");
        System.out.println("Verified: No pets displayed (count = 0)");
    }
    
    /**
     * Verifies that an empty state message is displayed.
     * 
     * <p>Maps to Gherkin: "And an empty state message should be displayed"
     */
    @Then("an empty state message should be displayed")
    public void emptyStateMessageDisplayed() {
        assertTrue(petListPage.isEmptyStateDisplayed(),
            "Empty state message should be visible when no results found");
        System.out.println("Verified: Empty state message is displayed");
    }
    
    /**
     * Verifies navigation to pet details page.
     * 
     * <p>Maps to Gherkin: "Then I should be navigated to the pet details page"
     */
    @Then("I should be navigated to the pet details page")
    public void shouldBeOnPetDetailsPage() {
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/pets/") && !currentUrl.endsWith("/pets"),
            "Should navigate to pet details page (URL should contain /pets/{id})");
        System.out.println("Verified: Navigated to pet details page: " + currentUrl);
    }
    
    /**
     * Verifies that pet details match the list entry (placeholder).
     * 
     * <p>Maps to Gherkin: "And the pet details should match the list entry"
     * 
     * <p>Note: Full implementation would require storing pet data from list
     * and comparing with details page. This is a simplified version.
     */
    @Then("the pet details should match the list entry")
    public void petDetailsMatchListEntry() {
        // Simplified: Just verify we're on a details page
        // Full implementation would compare pet name/species from list to details
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.matches(".*/pets/\\d+.*"),
            "Should be on a pet details page with numeric ID");
        System.out.println("Verified: On valid pet details page");
    }
    
    // ========================================
    // NEW: Steps for simplified feature file
    // ========================================
    
    @Then("the list should contain animal cards")
    public void listShouldContainAnimalCards() {
        System.out.println("Verifying list contains animal cards...");
        assertTrue(petListPage.getPetCount() > 0, 
            "List should contain at least one animal card");
        System.out.println("Verified: List contains " + petListPage.getPetCount() + " animal cards");
    }
    
    @Then("the add button should be visible")
    public void addButtonShouldBeVisible() {
        System.out.println("Verifying add button is visible...");
        assertTrue(petListPage.isAddButtonVisible(), 
            "Add button should be visible on the page");
        System.out.println("Verified: Add button is visible");
    }
    
    @Then("the add button should have text {string}")
    public void addButtonShouldHaveText(String expectedText) {
        System.out.println("Verifying add button text: " + expectedText);
        String actualText = petListPage.getAddButtonText();
        assertTrue(actualText.contains(expectedText), 
            "Add button should have text '" + expectedText + "' but was '" + actualText + "'");
        System.out.println("Verified: Add button text contains '" + expectedText + "'");
    }
}

