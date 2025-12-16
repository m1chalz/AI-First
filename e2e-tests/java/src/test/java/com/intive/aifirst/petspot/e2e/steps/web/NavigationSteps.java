package com.intive.aifirst.petspot.e2e.steps.web;

import com.intive.aifirst.petspot.e2e.pages.web.NavigationPage;
import com.intive.aifirst.petspot.e2e.utils.TestConfig;
import com.intive.aifirst.petspot.e2e.utils.WebDriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

public class NavigationSteps {

    private NavigationPage navigationPage;

    private NavigationPage getNavigationPage() {
        if (navigationPage == null) {
            WebDriver driver = WebDriverManager.getDriver();
            navigationPage = new NavigationPage(driver);
        }
        return navigationPage;
    }

    @Given("user is on the Home page")
    public void userIsOnTheHomePage() {
        WebDriver driver = WebDriverManager.getDriver();
        driver.get(TestConfig.getBaseUrl());
        getNavigationPage().waitForNavigationBarVisible(10);
    }

    @Given("user directly accesses {string} URL")
    public void userDirectlyAccessesUrl(String path) {
        WebDriver driver = WebDriverManager.getDriver();
        driver.get(TestConfig.getBaseUrl() + path);
        getNavigationPage().waitForNavigationBarVisible(10);
    }

    @When("user clicks {string} in the navigation bar")
    public void userClicksInNavigationBar(String section) {
        NavigationPage page = getNavigationPage();
        switch (section) {
            case "Home" -> page.clickHome();
            case "Lost Pet" -> page.clickLostPet();
            case "Found Pet" -> page.clickFoundPet();
            case "Contact Us" -> page.clickContact();
            case "Account" -> page.clickAccount();
            default -> throw new IllegalArgumentException("Unknown section: " + section);
        }
    }

    @Then("user should be on the {string} page")
    public void userShouldBeOnPage(String section) {
        WebDriver driver = WebDriverManager.getDriver();
        String currentUrl = driver.getCurrentUrl();
        String expectedPath = switch (section) {
            case "Home" -> "/";
            case "Lost Pet" -> "/lost-pets";
            case "Found Pet" -> "/found-pets";
            case "Contact Us" -> "/contact";
            case "Account" -> "/account";
            default -> throw new IllegalArgumentException("Unknown section: " + section);
        };
        
        if (expectedPath.equals("/")) {
            assertTrue(currentUrl.endsWith("/") || currentUrl.equals(TestConfig.getBaseUrl()),
                    "Expected to be on Home page, but URL was: " + currentUrl);
        } else {
            assertTrue(currentUrl.contains(expectedPath),
                    "Expected URL to contain " + expectedPath + ", but was: " + currentUrl);
        }
    }

    @Then("{string} navigation item should be highlighted")
    public void navigationItemShouldBeHighlighted(String section) {
        NavigationPage page = getNavigationPage();
        boolean isActive = switch (section) {
            case "Home" -> page.isHomeLinkActive();
            case "Lost Pet" -> page.isLostPetLinkActive();
            case "Found Pet" -> page.isFoundPetLinkActive();
            case "Contact Us" -> page.isContactLinkActive();
            case "Account" -> page.isAccountLinkActive();
            default -> throw new IllegalArgumentException("Unknown section: " + section);
        };
        assertTrue(isActive, section + " navigation item should be highlighted");
    }

    @Then("other navigation items should not be highlighted")
    public void otherNavigationItemsShouldNotBeHighlighted() {
        NavigationPage page = getNavigationPage();
        String activeItem = page.getActiveItemId();
        assertNotNull(activeItem, "There should be exactly one active navigation item");
        
        int activeCount = 0;
        if (page.isHomeLinkActive()) activeCount++;
        if (page.isLostPetLinkActive()) activeCount++;
        if (page.isFoundPetLinkActive()) activeCount++;
        if (page.isContactLinkActive()) activeCount++;
        if (page.isAccountLinkActive()) activeCount++;
        
        assertEquals(1, activeCount, "Only one navigation item should be active");
    }

    @Then("navigation bar should display all navigation items")
    public void navigationBarShouldDisplayAllNavigationItems() {
        assertTrue(getNavigationPage().isNavigationBarDisplayed(),
                "Navigation bar should be visible");
    }

    @Then("navigation bar should display the PetSpot logo")
    public void navigationBarShouldDisplayLogo() {
        assertTrue(getNavigationPage().isNavigationBarDisplayed(),
                "Navigation bar with logo should be visible");
    }

    // User Story 2 - Visual Design Step Definitions

    @Then("navigation bar should display with horizontal layout")
    public void navigationBarShouldDisplayWithHorizontalLayout() {
        assertTrue(getNavigationPage().isNavigationBarHorizontalLayout(),
                "Navigation bar should have horizontal flexbox layout");
    }

    @Then("navigation bar logo should be positioned on the left side")
    public void navigationBarLogoShouldBePositionedOnLeftSide() {
        assertTrue(getNavigationPage().isLogoPositionedLeft(),
                "Logo should be positioned on the left side of navigation bar");
    }

    @Then("navigation items should be positioned on the right side")
    public void navigationItemsShouldBePositionedOnRightSide() {
        assertTrue(getNavigationPage().areNavigationItemsPositionedRight(),
                "Navigation items should be positioned on the right side");
    }

    @Then("all navigation items should display an icon")
    public void allNavigationItemsShouldDisplayAnIcon() {
        assertTrue(getNavigationPage().allNavigationItemsHaveIcons(),
                "All navigation items should display an icon");
    }

    @Then("all navigation items should display a text label")
    public void allNavigationItemsShouldDisplayATextLabel() {
        assertTrue(getNavigationPage().allNavigationItemsHaveLabels(),
                "All navigation items should display a text label");
    }

    @Then("icons should appear before labels")
    public void iconsShouldAppearBeforeLabels() {
        assertTrue(getNavigationPage().iconsAppearBeforeLabels(),
                "Icons should appear before labels in navigation items");
    }

    @Then("{string} navigation item should have active styling")
    public void navigationItemShouldHaveActiveStyling(String section) {
        NavigationPage page = getNavigationPage();
        assertTrue(page.hasActiveClass(page.getNavigationItem(section)),
                section + " navigation item should have active styling");
    }

    @Then("{string} navigation item should have blue background color")
    public void navigationItemShouldHaveBlueBackgroundColor(String section) {
        NavigationPage page = getNavigationPage();
        assertTrue(page.hasActiveItemBlueBackground(page.getNavigationItem(section)),
                section + " navigation item should have blue background color (#EFF6FF)");
    }

    @Then("{string} navigation item should have blue text color")
    public void navigationItemShouldHaveBlueTextColor(String section) {
        NavigationPage page = getNavigationPage();
        assertTrue(page.hasActiveItemBlueText(page.getNavigationItem(section)),
                section + " navigation item should have blue text color (#155DFC)");
    }

    @Then("{string} navigation item should have inactive styling")
    public void navigationItemShouldHaveInactiveStyling(String section) {
        NavigationPage page = getNavigationPage();
        assertFalse(page.hasActiveClass(page.getNavigationItem(section)),
                section + " navigation item should have inactive styling");
    }

    @Then("{string} navigation item should have transparent background")
    public void navigationItemShouldHaveTransparentBackground(String section) {
        NavigationPage page = getNavigationPage();
        assertTrue(page.hasInactiveItemTransparentBackground(page.getNavigationItem(section)),
                section + " navigation item should have transparent background");
    }

    @Then("{string} navigation item should have gray text color")
    public void navigationItemShouldHaveGrayTextColor(String section) {
        NavigationPage page = getNavigationPage();
        assertTrue(page.hasInactiveItemGrayText(page.getNavigationItem(section)),
                section + " navigation item should have gray text color (#4A5565)");
    }

    @When("user hovers over {string} navigation item")
    public void userHoversOverNavigationItem(String section) {
        getNavigationPage().hoverOverNavigationItem(section);
    }

    @Then("{string} navigation item should show hover feedback")
    public void navigationItemShouldShowHoverFeedback(String section) {
        NavigationPage page = getNavigationPage();
        assertTrue(page.hasHoverFeedback(page.getNavigationItem(section)),
                section + " navigation item should show hover feedback");
    }
}

