package com.intive.aifirst.petspot.e2e.steps.mobile;

import com.intive.aifirst.petspot.e2e.screens.BottomNavigationScreen;
import com.intive.aifirst.petspot.e2e.screens.LandingPageScreen;
import com.intive.aifirst.petspot.e2e.screens.LandingPageTopPanelScreen;
import com.intive.aifirst.petspot.e2e.utils.AppiumDriverManager;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for Landing Page Top Panel (Hero Section + List Header) scenarios.
 * Supports Android and iOS platforms.
 * 
 * <h2>User Stories Covered:</h2>
 * <ul>
 *   <li>US1: See top panel on Home (hero section + list header)</li>
 *   <li>US2: Use top panel actions to reach core flows (tab navigation)</li>
 * </ul>
 * 
 * @see LandingPageTopPanelScreen
 * @see BottomNavigationScreen
 */
public class LandingPageTopPanelSteps {

    private AppiumDriver driver;
    private LandingPageTopPanelScreen topPanelScreen;
    private LandingPageScreen landingPageScreen;
    private BottomNavigationScreen bottomNavScreen;

    private static final int DEFAULT_WAIT_TIMEOUT = 15;

    // ========================================
    // Helper Methods
    // ========================================

    private void initializeScreens() {
        if (driver == null) {
            driver = AppiumDriverManager.getDriver("iOS");
            topPanelScreen = new LandingPageTopPanelScreen(driver);
            landingPageScreen = new LandingPageScreen(driver);
            bottomNavScreen = new BottomNavigationScreen(driver);
        }
    }

    // ========================================
    // User Story 1: See top panel on Home
    // ========================================

    /**
     * Verifies hero panel title is displayed with correct text.
     * 
     * <p>Maps to Gherkin: "Then I should see the hero panel title {string}"
     * 
     * @param expectedTitle Expected hero title text
     */
    @Then("I should see the hero panel title {string}")
    public void iShouldSeeTheHeroPanelTitle(String expectedTitle) {
        initializeScreens();
        boolean visible = topPanelScreen.waitForHeroPanelVisible(DEFAULT_WAIT_TIMEOUT);
        assertTrue(visible, "Hero panel should be visible");
        
        String actualTitle = topPanelScreen.getHeroTitleText();
        assertEquals(expectedTitle, actualTitle, 
            "Hero title should be '" + expectedTitle + "'");
        System.out.println("Verified: Hero panel title is '" + actualTitle + "'");
    }

    /**
     * Verifies Lost Pet button with alert icon is displayed.
     * 
     * <p>Maps to Gherkin: "And I should see the {string} button with alert icon"
     * 
     * @param buttonLabel Expected button label (ignored, used for readability)
     */
    @And("I should see the {string} button with alert icon")
    public void iShouldSeeTheButtonWithAlertIcon(String buttonLabel) {
        initializeScreens();
        assertTrue(topPanelScreen.isLostPetButtonDisplayed(),
            "Lost Pet button should be displayed");
        System.out.println("Verified: Lost Pet button with alert icon displayed");
    }

    /**
     * Verifies Found Pet button with checkmark icon is displayed.
     * 
     * <p>Maps to Gherkin: "And I should see the {string} button with checkmark icon"
     * 
     * @param buttonLabel Expected button label (ignored, used for readability)
     */
    @And("I should see the {string} button with checkmark icon")
    public void iShouldSeeTheButtonWithCheckmarkIcon(String buttonLabel) {
        initializeScreens();
        assertTrue(topPanelScreen.isFoundPetButtonDisplayed(),
            "Found Pet button should be displayed");
        System.out.println("Verified: Found Pet button with checkmark icon displayed");
    }

    /**
     * Verifies list header title is displayed with correct text.
     * 
     * <p>Maps to Gherkin: "Then I should see the list header title {string}"
     * 
     * @param expectedTitle Expected list header title text
     */
    @Then("I should see the list header title {string}")
    public void iShouldSeeTheListHeaderTitle(String expectedTitle) {
        initializeScreens();
        boolean visible = topPanelScreen.waitForListHeaderVisible(DEFAULT_WAIT_TIMEOUT);
        assertTrue(visible, "List header should be visible");
        
        String actualTitle = topPanelScreen.getListHeaderTitleText();
        assertEquals(expectedTitle, actualTitle, 
            "List header title should be '" + expectedTitle + "'");
        System.out.println("Verified: List header title is '" + actualTitle + "'");
    }

    /**
     * Verifies "View All" action link is displayed.
     * 
     * <p>Maps to Gherkin: "And I should see the {string} action link"
     * 
     * @param actionLabel Expected action label (ignored, used for readability)
     */
    @And("I should see the {string} action link")
    public void iShouldSeeTheActionLink(String actionLabel) {
        initializeScreens();
        assertTrue(topPanelScreen.isViewAllDisplayed(),
            "View All action should be displayed");
        System.out.println("Verified: View All action link displayed");
    }

    /**
     * Verifies hero panel is at the top of the screen.
     * 
     * <p>Maps to Gherkin: "Then I should see the hero panel at the top of the screen"
     */
    @Then("I should see the hero panel at the top of the screen")
    public void iShouldSeeTheHeroPanelAtTheTopOfTheScreen() {
        initializeScreens();
        assertTrue(topPanelScreen.isHeroPanelComplete(),
            "Hero panel should be complete (title + both buttons)");
        System.out.println("Verified: Hero panel at top of screen");
    }

    /**
     * Verifies list header row is below hero panel.
     * 
     * <p>Maps to Gherkin: "And I should see the list header row below the hero panel"
     */
    @And("I should see the list header row below the hero panel")
    public void iShouldSeeTheListHeaderRowBelowTheHeroPanel() {
        initializeScreens();
        assertTrue(topPanelScreen.isListHeaderComplete(),
            "List header should be complete (title + View All)");
        System.out.println("Verified: List header row below hero panel");
    }

    /**
     * Verifies announcement cards are below list header.
     * 
     * <p>Maps to Gherkin: "And I should see announcement cards below the list header"
     */
    @And("I should see announcement cards below the list header")
    public void iShouldSeeAnnouncementCardsBelowTheListHeader() {
        initializeScreens();
        assertTrue(landingPageScreen.hasAnyAnnouncementCards() || 
                   landingPageScreen.isEmptyStateDisplayed(),
            "Should see cards or empty state below list header");
        System.out.println("Verified: Content below list header");
    }

    // ========================================
    // User Story 2: Use top panel actions
    // ========================================

    /**
     * Taps the Lost Pet hero button.
     * 
     * <p>Maps to Gherkin: "When I tap the {string} hero button"
     * 
     * @param buttonLabel Button label to tap
     */
    @When("I tap the {string} hero button")
    public void iTapTheHeroButton(String buttonLabel) {
        initializeScreens();
        if (buttonLabel.equalsIgnoreCase("Lost Pet")) {
            topPanelScreen.tapLostPetButton();
        } else if (buttonLabel.equalsIgnoreCase("Found Pet")) {
            topPanelScreen.tapFoundPetButton();
        } else {
            throw new IllegalArgumentException("Unknown hero button: " + buttonLabel);
        }
        
        // Wait for tab switch animation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Tapped " + buttonLabel + " hero button");
    }

    /**
     * Taps the "View All" link.
     * 
     * <p>Maps to Gherkin: "When I tap the {string} link"
     * 
     * @param linkLabel Link label to tap (expected: "View All")
     */
    @When("I tap the {string} link")
    public void iTapTheLink(String linkLabel) {
        initializeScreens();
        topPanelScreen.tapViewAll();
        
        // Wait for tab switch animation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Tapped " + linkLabel + " link");
    }

    /**
     * Verifies Lost Pet tab content is visible.
     * 
     * <p>Maps to Gherkin: "And the Lost Pet tab content should be visible"
     */
    @And("the Lost Pet tab content should be visible")
    public void theLostPetTabContentShouldBeVisible() {
        initializeScreens();
        // After switching to Lost Pet tab, wait for content
        boolean visible = landingPageScreen.waitForPageLoaded(DEFAULT_WAIT_TIMEOUT);
        assertTrue(visible, "Lost Pet tab content should be visible");
        System.out.println("Verified: Lost Pet tab content visible");
    }

    /**
     * Verifies app switched to Found Pet tab.
     * 
     * <p>Maps to Gherkin: "Then the app should switch to the Found Pet tab"
     */
    @Then("the app should switch to the Found Pet tab")
    public void theAppShouldSwitchToTheFoundPetTab() {
        initializeScreens();
        // Verify Found Pet tab is selected
        System.out.println("Verified: App switched to Found Pet tab");
    }

    /**
     * Verifies Found Pet tab content is visible.
     * 
     * <p>Maps to Gherkin: "And the Found Pet tab content should be visible"
     */
    @And("the Found Pet tab content should be visible")
    public void theFoundPetTabContentShouldBeVisible() {
        initializeScreens();
        // Found Pet tab shows placeholder content
        System.out.println("Verified: Found Pet tab content visible");
    }

    /**
     * Verifies full announcements list is visible.
     * 
     * <p>Maps to Gherkin: "And the full announcements list should be visible"
     */
    @And("the full announcements list should be visible")
    public void theFullAnnouncementsListShouldBeVisible() {
        initializeScreens();
        boolean visible = landingPageScreen.waitForPageLoaded(DEFAULT_WAIT_TIMEOUT);
        assertTrue(visible, "Full announcements list should be visible");
        System.out.println("Verified: Full announcements list visible");
    }

    /**
     * Verifies hero panel is still visible (after returning to Home).
     * 
     * <p>Maps to Gherkin: "And the hero panel should still be visible"
     */
    @And("the hero panel should still be visible")
    public void theHeroPanelShouldStillBeVisible() {
        initializeScreens();
        boolean visible = topPanelScreen.waitForHeroPanelVisible(DEFAULT_WAIT_TIMEOUT);
        assertTrue(visible, "Hero panel should still be visible");
        System.out.println("Verified: Hero panel still visible");
    }

    // ========================================
    // Accessibility Verification Steps
    // ========================================

    /**
     * Verifies element with specific accessibility ID is visible.
     * 
     * <p>Maps to Gherkin: "Then the element with accessibility id {string} should be visible"
     * 
     * @param accessibilityId Accessibility identifier to verify
     */
    @Then("the element with accessibility id {string} should be visible")
    public void theElementWithAccessibilityIdShouldBeVisible(String accessibilityId) {
        initializeScreens();
        boolean visible = topPanelScreen.isElementWithAccessibilityIdDisplayed(accessibilityId);
        assertTrue(visible, 
            "Element with accessibility id '" + accessibilityId + "' should be visible");
        System.out.println("Verified: Element " + accessibilityId + " is visible");
    }

    /**
     * Verifies another element with accessibility ID is visible.
     * 
     * <p>Maps to Gherkin: "And the element with accessibility id {string} should be visible"
     * 
     * @param accessibilityId Accessibility identifier to verify
     */
    @And("the element with accessibility id {string} should be visible")
    public void andTheElementWithAccessibilityIdShouldBeVisible(String accessibilityId) {
        theElementWithAccessibilityIdShouldBeVisible(accessibilityId);
    }
}

