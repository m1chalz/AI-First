package com.intive.aifirst.petspot.e2e.steps.web;

import com.intive.aifirst.petspot.e2e.pages.web.LandingPage;
import com.intive.aifirst.petspot.e2e.utils.TestConfig;
import com.intive.aifirst.petspot.e2e.utils.WebDriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;

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
}

