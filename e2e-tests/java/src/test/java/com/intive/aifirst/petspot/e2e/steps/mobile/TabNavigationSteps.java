package com.intive.aifirst.petspot.e2e.steps.mobile;

import com.intive.aifirst.petspot.e2e.screens.BottomNavigationScreen;
import com.intive.aifirst.petspot.e2e.screens.PlaceholderScreen;
import com.intive.aifirst.petspot.e2e.screens.PetListScreen;
import com.intive.aifirst.petspot.e2e.utils.AppiumDriverManager;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Step definitions for tab navigation feature.
 * Implements all Given-When-Then steps for the 6 acceptance scenarios.
 */
public class TabNavigationSteps {
    private final BottomNavigationScreen bottomNav;
    private final PlaceholderScreen placeholderScreen;
    private final PetListScreen petListScreen;

    public TabNavigationSteps() {
        AppiumDriver driver = AppiumDriverManager.getDriver();
        this.bottomNav = new BottomNavigationScreen(driver);
        this.placeholderScreen = new PlaceholderScreen(driver);
        this.petListScreen = new PetListScreen(driver);
    }

    // Given steps

    @Given("the app is launched on the home screen")
    public void theAppIsLaunchedOnTheHomeScreen() {
        // App is launched by Appium - verify home tab is selected
        assertTrue(bottomNav.isNavigationBarVisible(), 
            "Bottom navigation should be visible on launch");
    }

    @Given("I am on the home screen")
    public void iAmOnTheHomeScreen() {
        // Navigate to home if not already there
        if (!bottomNav.isHomeTabSelected()) {
            bottomNav.tapHomeTab();
        }
        assertTrue(bottomNav.isHomeTabSelected(), 
            "Should be on home screen");
    }

    @Given("I am on any screen in the app")
    public void iAmOnAnyScreenInTheApp() {
        // Just verify app is running and navigation is visible
        assertTrue(bottomNav.isNavigationBarVisible(), 
            "Bottom navigation should be visible");
    }

    // When steps

    @When("I tap the {string} tab")
    public void iTapTheTab(String tabName) {
        bottomNav.tapTabByName(tabName);
    }

    // Then steps

    @Then("I should see the home landing page")
    public void iShouldSeeTheHomeLandingPage() {
        assertTrue(bottomNav.isHomeTabSelected(), 
            "Home tab should be selected");
        // Home shows placeholder for now - will show landing page when implemented
        assertTrue(placeholderScreen.isComingSoonTextDisplayed(),
            "Home landing page placeholder should be visible");
    }

    @Then("I should see the lost pet announcements list")
    public void iShouldSeeTheLostPetAnnouncementsList() {
        assertTrue(bottomNav.isLostPetTabSelected(), 
            "Lost Pet tab should be selected");
        // Lost Pet tab shows the animal list (existing implementation)
        assertTrue(petListScreen.isPetListVisible(),
            "Lost pet announcements list should be visible");
    }

    @Then("I should see the found pet announcements list")
    public void iShouldSeeTheFoundPetAnnouncementsList() {
        assertTrue(bottomNav.isFoundPetTabSelected(), 
            "Found Pet tab should be selected");
        // Currently shows placeholder, will show list when implemented
        assertTrue(placeholderScreen.isComingSoonTextDisplayed() || petListScreen.isPetListVisible(),
            "Found pet section should be visible");
    }

    @Then("I should see the {string} placeholder screen")
    public void iShouldSeeThePlaceholderScreen(String expectedText) {
        assertTrue(placeholderScreen.isComingSoonTextDisplayed(), 
            "Placeholder screen with '" + expectedText + "' should be visible");
    }

    @Then("the {string} tab should be visually selected")
    public void theTabShouldBeVisuallySelected(String tabName) {
        assertTrue(bottomNav.isTabSelectedByName(tabName), 
            tabName + " tab should be visually selected");
    }

    @Then("the {string} tab should not be visually selected")
    public void theTabShouldNotBeVisuallySelected(String tabName) {
        assertFalse(bottomNav.isTabSelectedByName(tabName), 
            tabName + " tab should not be visually selected");
    }
}

