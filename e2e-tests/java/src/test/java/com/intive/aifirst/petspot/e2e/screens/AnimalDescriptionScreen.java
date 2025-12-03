package com.intive.aifirst.petspot.e2e.screens;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Screen Object for the Animal Description screen (step 3/4) of the missing pet flow.
 * Provides typed accessors for form fields, gender selector, and navigation buttons.
 */
public class AnimalDescriptionScreen {

    private static final int DEFAULT_WAIT_TIMEOUT = 10;

    private final AppiumDriver driver;

    // Form fields
    @AndroidFindBy(accessibility = "animalDescription.datePickerField")
    @iOSXCUITFindBy(id = "animalDescription.datePickerField")
    private WebElement datePickerField;

    @AndroidFindBy(accessibility = "animalDescription.petNameField")
    @iOSXCUITFindBy(id = "animalDescription.petNameField")
    private WebElement petNameField;

    @AndroidFindBy(accessibility = "animalDescription.speciesDropdown")
    @iOSXCUITFindBy(id = "animalDescription.speciesDropdown")
    private WebElement speciesDropdown;

    @AndroidFindBy(accessibility = "animalDescription.raceField")
    @iOSXCUITFindBy(id = "animalDescription.raceField")
    private WebElement raceField;

    @AndroidFindBy(accessibility = "animalDescription.genderFemale")
    @iOSXCUITFindBy(id = "animalDescription.genderFemale")
    private WebElement genderFemaleCard;

    @AndroidFindBy(accessibility = "animalDescription.genderMale")
    @iOSXCUITFindBy(id = "animalDescription.genderMale")
    private WebElement genderMaleCard;

    @AndroidFindBy(accessibility = "animalDescription.ageField")
    @iOSXCUITFindBy(id = "animalDescription.ageField")
    private WebElement ageField;

    // GPS Location section
    @AndroidFindBy(accessibility = "animalDescription.requestGpsButton")
    @iOSXCUITFindBy(id = "animalDescription.requestGpsButton")
    private WebElement requestGpsButton;

    @AndroidFindBy(accessibility = "animalDescription.latitudeField")
    @iOSXCUITFindBy(id = "animalDescription.latitudeField")
    private WebElement latitudeField;

    @AndroidFindBy(accessibility = "animalDescription.longitudeField")
    @iOSXCUITFindBy(id = "animalDescription.longitudeField")
    private WebElement longitudeField;

    @AndroidFindBy(accessibility = "animalDescription.continueButton")
    @iOSXCUITFindBy(id = "animalDescription.continueButton")
    private WebElement continueButton;

    @AndroidFindBy(accessibility = "animalDescription.backButton")
    @iOSXCUITFindBy(id = "animalDescription.backButton")
    private WebElement backButton;

    public AnimalDescriptionScreen(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT)), this);
    }

    public boolean isDisplayed() {
        try {
            waitForElement(speciesDropdown);
            return speciesDropdown.isDisplayed();
        } catch (Exception exception) {
            return false;
        }
    }

    // Date picker interactions
    public void tapDatePickerField() {
        waitForElement(datePickerField);
        datePickerField.click();
    }

    public String getDateValue() {
        waitForElement(datePickerField);
        return datePickerField.getText();
    }

    // Pet name interactions
    public void typePetName(String name) {
        waitForElement(petNameField);
        petNameField.clear();
        petNameField.sendKeys(name);
    }

    public String getPetName() {
        waitForElement(petNameField);
        String value = petNameField.getAttribute("value");
        return value != null ? value : petNameField.getText();
    }

    // Species dropdown interactions
    public void tapSpeciesDropdown() {
        waitForElement(speciesDropdown);
        speciesDropdown.click();
    }

    public String getSelectedSpecies() {
        waitForElement(speciesDropdown);
        return speciesDropdown.getText();
    }

    // Race field interactions
    public void typeRace(String race) {
        waitForElement(raceField);
        raceField.clear();
        raceField.sendKeys(race);
    }

    public String getRace() {
        waitForElement(raceField);
        String value = raceField.getAttribute("value");
        return value != null ? value : raceField.getText();
    }

    public boolean isRaceFieldEnabled() {
        try {
            waitForElement(raceField);
            return raceField.isEnabled();
        } catch (Exception exception) {
            return false;
        }
    }

    // Gender selector interactions
    public void selectGenderFemale() {
        waitForElement(genderFemaleCard);
        genderFemaleCard.click();
    }

    public void selectGenderMale() {
        waitForElement(genderMaleCard);
        genderMaleCard.click();
    }

    public boolean isGenderFemaleSelected() {
        waitForElement(genderFemaleCard);
        String selected = genderFemaleCard.getAttribute("selected");
        return "true".equalsIgnoreCase(selected);
    }

    public boolean isGenderMaleSelected() {
        waitForElement(genderMaleCard);
        String selected = genderMaleCard.getAttribute("selected");
        return "true".equalsIgnoreCase(selected);
    }

    // Age field interactions
    public void typeAge(String age) {
        waitForElement(ageField);
        ageField.clear();
        ageField.sendKeys(age);
    }

    public String getAge() {
        waitForElement(ageField);
        String value = ageField.getAttribute("value");
        return value != null ? value : ageField.getText();
    }

    // GPS Location interactions
    public void tapRequestGpsButton() {
        waitForElement(requestGpsButton);
        requestGpsButton.click();
    }

    public boolean isRequestGpsButtonDisplayed() {
        try {
            return requestGpsButton.isDisplayed();
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean isRequestGpsButtonEnabled() {
        try {
            waitForElement(requestGpsButton);
            return requestGpsButton.isEnabled();
        } catch (Exception exception) {
            return false;
        }
    }

    public String getRequestGpsButtonText() {
        waitForElement(requestGpsButton);
        return requestGpsButton.getText();
    }

    public void typeLatitude(String latitude) {
        waitForElement(latitudeField);
        latitudeField.clear();
        latitudeField.sendKeys(latitude);
    }

    public String getLatitude() {
        waitForElement(latitudeField);
        String value = latitudeField.getAttribute("value");
        return value != null ? value : latitudeField.getText();
    }

    public void typeLongitude(String longitude) {
        waitForElement(longitudeField);
        longitudeField.clear();
        longitudeField.sendKeys(longitude);
    }

    public String getLongitude() {
        waitForElement(longitudeField);
        String value = longitudeField.getAttribute("value");
        return value != null ? value : longitudeField.getText();
    }

    public boolean isLatitudeFieldPopulated() {
        String latitude = getLatitude();
        return latitude != null && !latitude.isEmpty() && !latitude.equals("-90 to 90");
    }

    public boolean isLongitudeFieldPopulated() {
        String longitude = getLongitude();
        return longitude != null && !longitude.isEmpty() && !longitude.equals("-180 to 180");
    }

    // Navigation interactions
    public void tapContinueButton() {
        waitForElement(continueButton);
        continueButton.click();
    }

    public void tapBackButton() {
        waitForElement(backButton);
        backButton.click();
    }

    public boolean isContinueButtonDisplayed() {
        try {
            return continueButton.isDisplayed();
        } catch (Exception exception) {
            return false;
        }
    }

    private void waitForElement(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
        wait.until(ExpectedConditions.visibilityOf(element));
    }
}

