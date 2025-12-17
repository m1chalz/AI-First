package com.intive.aifirst.petspot.e2e.steps.web;

import com.intive.aifirst.petspot.e2e.pages.web.LandingPage;
import com.intive.aifirst.petspot.e2e.utils.TestConfig;
import com.intive.aifirst.petspot.e2e.utils.WebDriverManager;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LandingPageSteps {

    private LandingPage landingPage;

    private LandingPage getLandingPage() {
        if (landingPage == null) {
            WebDriver driver = WebDriverManager.getDriver();
            landingPage = new LandingPage(driver);
        }
        return landingPage;
    }

    // Given steps
    @Given("user navigates to the landing page")
    public void userNavigatesToTheLandingPage() {
        WebDriver driver = WebDriverManager.getDriver();
        driver.get(TestConfig.getBaseUrl());
        assertTrue(getLandingPage().waitForPageLoad(10), "Landing page should load within timeout");
    }

    // Then steps - Hero Section
    @Then("landing page should display the hero section")
    public void landingPageShouldDisplayTheHeroSection() {
        assertTrue(getLandingPage().isHeroSectionDisplayed(), "Hero section should be displayed");
    }

    @Then("hero section should display the main heading")
    public void heroSectionShouldDisplayTheMainHeading() {
        assertTrue(getLandingPage().isHeroHeadingDisplayed(), "Hero heading should be displayed");
    }

    @Then("hero section should display the description text")
    public void heroSectionShouldDisplayTheDescriptionText() {
        assertTrue(getLandingPage().isHeroDescriptionDisplayed(), "Hero description should be displayed");
    }

    @Then("hero section should display {int} feature cards")
    public void heroSectionShouldDisplayFeatureCards(int expectedCount) {
        int actualCount = getLandingPage().getFeatureCardCount();
        assertEquals(expectedCount, actualCount,
            "Expected " + expectedCount + " feature cards, but found " + actualCount);
    }

    // Then steps - Footer
    @Then("landing page should display the footer")
    public void landingPageShouldDisplayTheFooter() {
        assertTrue(getLandingPage().isFooterDisplayed(), "Footer should be displayed");
    }

    @Then("footer should display the branding column")
    public void footerShouldDisplayTheBrandingColumn() {
        assertTrue(getLandingPage().isFooterBrandingDisplayed(), "Footer branding should be displayed");
    }

    @Then("footer should display the quick links column")
    public void footerShouldDisplayTheQuickLinksColumn() {
        assertTrue(getLandingPage().isFooterQuickLinksDisplayed(), "Footer quick links should be displayed");
    }

    @Then("footer should display the contact information column")
    public void footerShouldDisplayTheContactInformationColumn() {
        assertTrue(getLandingPage().isFooterContactDisplayed(), "Footer contact information should be displayed");
    }

    // US2 - Feature Cards steps
    @Then("feature cards should be displayed in the following order:")
    public void featureCardsShouldBeDisplayedInTheFollowingOrder(DataTable dataTable) {
        List<String> expectedOrder = dataTable.asList();
        List<String> actualOrder = getLandingPage().getFeatureCardTitles();
        assertEquals(expectedOrder, actualOrder, "Feature cards should be in correct order");
    }

    @Then("feature card {string} should display title {string}")
    public void featureCardShouldDisplayTitle(String id, String expectedTitle) {
        String actualTitle = getLandingPage().getFeatureCardTitle(id);
        assertEquals(expectedTitle, actualTitle,
            "Feature card '" + id + "' should display title '" + expectedTitle + "'");
    }

    @Then("feature card {string} should display a description")
    public void featureCardShouldDisplayADescription(String id) {
        assertTrue(getLandingPage().hasFeatureCardDescription(id),
            "Feature card '" + id + "' should have a description");
    }

    @Then("feature card {string} should have blue icon color")
    public void featureCardShouldHaveBlueIconColor(String id) {
        String color = getLandingPage().getFeatureCardIconColor(id);
        assertTrue(color.contains("59, 130, 246") || color.contains("#3B82F6") || color.contains("#3b82f6"),
            "Feature card '" + id + "' should have blue icon color, but was: " + color);
    }

    @Then("feature card {string} should have red icon color")
    public void featureCardShouldHaveRedIconColor(String id) {
        String color = getLandingPage().getFeatureCardIconColor(id);
        assertTrue(color.contains("239, 68, 68") || color.contains("#EF4444") || color.contains("#ef4444"),
            "Feature card '" + id + "' should have red icon color, but was: " + color);
    }

    @Then("feature card {string} should have green icon color")
    public void featureCardShouldHaveGreenIconColor(String id) {
        String color = getLandingPage().getFeatureCardIconColor(id);
        assertTrue(color.contains("16, 185, 129") || color.contains("#10B981") || color.contains("#10b981"),
            "Feature card '" + id + "' should have green icon color, but was: " + color);
    }

    @Then("feature card {string} should have purple icon color")
    public void featureCardShouldHavePurpleIconColor(String id) {
        String color = getLandingPage().getFeatureCardIconColor(id);
        assertTrue(color.contains("139, 92, 246") || color.contains("#8B5CF6") || color.contains("#8b5cf6"),
            "Feature card '" + id + "' should have purple icon color, but was: " + color);
    }

    @Then("feature cards should not be clickable")
    public void featureCardsShouldNotBeClickable() {
        assertFalse(getLandingPage().areFeatureCardsClickable(),
            "Feature cards should not be clickable");
    }
}

