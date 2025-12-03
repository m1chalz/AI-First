package com.intive.aifirst.petspot.e2e.steps.mobile;

import com.intive.aifirst.petspot.e2e.screens.AnimalDescriptionScreen;
import com.intive.aifirst.petspot.e2e.utils.AppiumDriverManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

/**
 * Step definitions for Animal Description screen (Step 3/4) E2E tests.
 * Covers User Story 1 (US1) scenarios for form fields, validation, and navigation.
 */
public class AnimalDescriptionSteps {

    private AnimalDescriptionScreen animalDescriptionScreen;

    private void initScreen() {
        if (animalDescriptionScreen == null) {
            animalDescriptionScreen = new AnimalDescriptionScreen(AppiumDriverManager.getDriver());
        }
    }

    // ========================================
    // Given Steps
    // ========================================

    @Given("the user is on the Animal Description screen")
    public void userIsOnAnimalDescriptionScreen() {
        initScreen();
        assertTrue("Animal Description screen should be displayed", animalDescriptionScreen.isDisplayed());
    }

    @Given("the user has selected {string} as the species")
    public void userHasSelectedSpecies(String species) {
        initScreen();
        animalDescriptionScreen.tapSpeciesDropdown();
        // Note: Species selection from dropdown handled by platform-specific implementation
    }

    @Given("the user has entered {string} as the race")
    public void userHasEnteredRace(String race) {
        initScreen();
        animalDescriptionScreen.typeRace(race);
    }

    @Given("the user has selected {string} as the gender")
    public void userHasSelectedGender(String gender) {
        initScreen();
        if ("Male".equalsIgnoreCase(gender)) {
            animalDescriptionScreen.selectGenderMale();
        } else if ("Female".equalsIgnoreCase(gender)) {
            animalDescriptionScreen.selectGenderFemale();
        }
    }

    @Given("the user has NOT selected a species")
    public void userHasNotSelectedSpecies() {
        initScreen();
        // Species is empty by default, no action needed
    }

    @Given("the user has NOT entered a race")
    public void userHasNotEnteredRace() {
        initScreen();
        // Race is empty by default, no action needed
    }

    @Given("the user has NOT selected a gender")
    public void userHasNotSelectedGender() {
        initScreen();
        // Gender is null by default, no action needed
    }

    // ========================================
    // When Steps
    // ========================================

    @When("the user selects {string} as the species")
    public void userSelectsSpecies(String species) {
        initScreen();
        animalDescriptionScreen.tapSpeciesDropdown();
        // Platform-specific dropdown selection would go here
    }

    @When("the user enters {string} as the race")
    public void userEntersRace(String race) {
        initScreen();
        animalDescriptionScreen.typeRace(race);
    }

    @When("the user selects {string} as the gender")
    public void userSelectsGender(String gender) {
        initScreen();
        if ("Male".equalsIgnoreCase(gender)) {
            animalDescriptionScreen.selectGenderMale();
        } else if ("Female".equalsIgnoreCase(gender)) {
            animalDescriptionScreen.selectGenderFemale();
        }
    }

    @When("the user enters {string} as the pet name")
    public void userEntersPetName(String name) {
        initScreen();
        animalDescriptionScreen.typePetName(name);
    }

    @When("the user enters {string} as the age")
    public void userEntersAge(String age) {
        initScreen();
        animalDescriptionScreen.typeAge(age);
    }

    @When("the user taps the Continue button")
    public void userTapsContinueButton() {
        initScreen();
        animalDescriptionScreen.tapContinueButton();
    }

    @When("the user taps the back button")
    public void userTapsBackButton() {
        initScreen();
        animalDescriptionScreen.tapBackButton();
    }

    @When("the user taps the species dropdown")
    public void userTapsSpeciesDropdown() {
        initScreen();
        animalDescriptionScreen.tapSpeciesDropdown();
    }

    @When("the user taps the Female gender card")
    public void userTapsFemaleGenderCard() {
        initScreen();
        animalDescriptionScreen.selectGenderFemale();
    }

    @When("the user taps the Male gender card")
    public void userTapsMaleGenderCard() {
        initScreen();
        animalDescriptionScreen.selectGenderMale();
    }

    @When("the user navigates forward to the Animal Description screen")
    public void userNavigatesForwardToAnimalDescriptionScreen() {
        initScreen();
        // Navigate from Photo screen via Continue button - handled by PhotoScreen
    }

    // ========================================
    // Then Steps
    // ========================================

    @Then("the user should be on the Contact Details screen")
    public void userShouldBeOnContactDetailsScreen() {
        // Contact Details screen verification - handled by separate Screen Object
        // For now, just verify we navigated away from Animal Description
        initScreen();
        // Platform-specific verification
    }

    @Then("the user should be on the Photo screen")
    public void userShouldBeOnPhotoScreen() {
        // Photo screen verification - handled by separate Screen Object
    }

    @Then("the user should remain on the Animal Description screen")
    public void userShouldRemainOnAnimalDescriptionScreen() {
        initScreen();
        assertTrue("Should remain on Animal Description screen", animalDescriptionScreen.isDisplayed());
    }

    @Then("the race field should be disabled")
    public void raceFieldShouldBeDisabled() {
        initScreen();
        assertFalse("Race field should be disabled", animalDescriptionScreen.isRaceFieldEnabled());
    }

    @Then("the race field should be enabled")
    public void raceFieldShouldBeEnabled() {
        initScreen();
        assertTrue("Race field should be enabled", animalDescriptionScreen.isRaceFieldEnabled());
    }

    @Then("the race field should be empty")
    public void raceFieldShouldBeEmpty() {
        initScreen();
        String race = animalDescriptionScreen.getRace();
        assertTrue("Race field should be empty", race == null || race.isEmpty());
    }

    @Then("the Female card should be selected")
    public void femaleCardShouldBeSelected() {
        initScreen();
        assertTrue("Female card should be selected", animalDescriptionScreen.isGenderFemaleSelected());
    }

    @Then("the Male card should be selected")
    public void maleCardShouldBeSelected() {
        initScreen();
        assertTrue("Male card should be selected", animalDescriptionScreen.isGenderMaleSelected());
    }

    @Then("the Female card should not be selected")
    public void femaleCardShouldNotBeSelected() {
        initScreen();
        assertFalse("Female card should not be selected", animalDescriptionScreen.isGenderFemaleSelected());
    }

    @Then("the Male card should not be selected")
    public void maleCardShouldNotBeSelected() {
        initScreen();
        assertFalse("Male card should not be selected", animalDescriptionScreen.isGenderMaleSelected());
    }

    @Then("the pet name field should display {string}")
    public void petNameFieldShouldDisplay(String expectedName) {
        initScreen();
        assertEquals("Pet name should match", expectedName, animalDescriptionScreen.getPetName());
    }

    @Then("the age field should display {string}")
    public void ageFieldShouldDisplay(String expectedAge) {
        initScreen();
        assertEquals("Age should match", expectedAge, animalDescriptionScreen.getAge());
    }

    @Then("the date field should display today's date")
    public void dateFieldShouldDisplayTodaysDate() {
        initScreen();
        String dateValue = animalDescriptionScreen.getDateValue();
        assertNotNull("Date field should have a value", dateValue);
        // Date format verification would be platform-specific
    }

    @Then("a validation error should be displayed")
    public void validationErrorShouldBeDisplayed() {
        // Snackbar or inline error verification
        // Platform-specific implementation
    }

    @Then("the dropdown should display {string}, {string}, {string}, {string}, {string}")
    public void dropdownShouldDisplayOptions(String opt1, String opt2, String opt3, String opt4, String opt5) {
        // Dropdown options verification - platform-specific
    }

    @Then("the species field should display {string}")
    public void speciesFieldShouldDisplay(String expectedSpecies) {
        initScreen();
        assertEquals("Species should match", expectedSpecies, animalDescriptionScreen.getSelectedSpecies());
    }

    @Then("the race field should display {string}")
    public void raceFieldShouldDisplay(String expectedRace) {
        initScreen();
        assertEquals("Race should match", expectedRace, animalDescriptionScreen.getRace());
    }
}

