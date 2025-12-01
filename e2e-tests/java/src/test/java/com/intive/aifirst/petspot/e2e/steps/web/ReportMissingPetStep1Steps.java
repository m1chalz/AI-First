package com.intive.aifirst.petspot.e2e.steps.web;

import com.intive.aifirst.petspot.e2e.pages.ReportMissingPetStep1Page;
import com.intive.aifirst.petspot.e2e.utils.WebDriverManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

public class ReportMissingPetStep1Steps {
    
    private WebDriver driver;
    private ReportMissingPetStep1Page page;
    
    public ReportMissingPetStep1Steps() {
        this.driver = WebDriverManager.getDriver();
        this.page = new ReportMissingPetStep1Page(driver);
    }
    
    @When("I enter microchip number {string}")
    public void enterMicrochipNumber(String microchipNumber) {
        page.enterMicrochipNumber(microchipNumber);
        System.out.println("Entered microchip number: " + microchipNumber);
    }
    
    @When("I click the continue button on step 1")
    public void clickContinueButton() {
        page.clickContinueButton();
        System.out.println("Clicked continue button on microchip number screen");
    }
    
    @When("I click the back button on step 1")
    public void clickBackButton() {
        page.clickBackButton();
        System.out.println("Clicked back button on microchip number screen");
    }
    
    @Then("the microchip input should display {string}")
    public void microchipInputShouldDisplay(String expectedValue) {
        String actualValue = page.getMicrochipInputValue();
        assertEquals(expectedValue, actualValue,
            "Microchip input should display '" + expectedValue + "' but was: " + actualValue);
        System.out.println("Verified: Microchip input displays '" + expectedValue + "'");
    }
    
    @Then("the continue button should be enabled")
    public void continueButtonShouldBeEnabled() {
        assertTrue(page.isContinueButtonEnabled(),
            "Continue button should be enabled");
        System.out.println("Verified: Continue button is enabled");
    }
    
    @Then("the back button should be displayed")
    public void backButtonShouldBeDisplayed() {
        assertTrue(page.isBackButtonDisplayed(),
            "Back button should be displayed");
        System.out.println("Verified: Back button is displayed");
    }
}

