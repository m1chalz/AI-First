/**
 * Screen Object Model for Animal Description Screen (Step 3 of Missing Pet flow)
 * Platform: iOS
 * Feature: 031-ios-animal-description-screen
 */

export class AnimalDescriptionScreen {
    // Accessibility identifiers (as defined in SwiftUI views)
    
    // Date picker
    get datePicker() {
        return $('~animalDescription.datePicker.tap');
    }
    
    // Pet name text field (optional - US1: 046-ios-pet-name-field)
    get petNameTextField() {
        return $('~animalDescription.petNameTextField.input');
    }
    
    // Species dropdown
    get speciesDropdown() {
        return $('~animalDescription.speciesDropdown.tap');
    }
    
    // Race text field
    get raceTextField() {
        return $('~animalDescription.raceTextField.input');
    }
    
    // Gender selector
    get genderMaleButton() {
        return $('~animalDescription.gender.male.tap');
    }
    
    get genderFemaleButton() {
        return $('~animalDescription.gender.female.tap');
    }
    
    // Age text field (optional)
    get ageTextField() {
        return $('~animalDescription.ageTextField.input');
    }
    
    // GPS button
    get requestGPSButton() {
        return $('~animalDescription.requestGPSButton.tap');
    }
    
    // Coordinate text fields (mandatory)
    get latitudeTextField() {
        return $('~animalDescription.latitudeTextField.input');
    }
    
    get longitudeTextField() {
        return $('~animalDescription.longitudeTextField.input');
    }
    
    // Description text area (optional)
    get descriptionTextArea() {
        return $('~animalDescription.descriptionTextArea.input');
    }
    
    // Continue button
    get continueButton() {
        return $('~animalDescription.continueButton.tap');
    }
    
    // Actions
    
    async selectDate(date: Date): Promise<void> {
        await this.datePicker.click();
        // Platform-specific date selection logic will be added in E2E test implementation
    }
    
    async enterPetName(petName: string): Promise<void> {
        await this.petNameTextField.setValue(petName);
    }
    
    async getPetNameValue(): Promise<string> {
        return await this.petNameTextField.getValue();
    }
    
    async selectSpecies(species: string): Promise<void> {
        await this.speciesDropdown.click();
        // Platform-specific picker selection logic will be added in E2E test implementation
    }
    
    async enterRace(race: string): Promise<void> {
        await this.raceTextField.setValue(race);
    }
    
    async selectGenderMale(): Promise<void> {
        await this.genderMaleButton.click();
    }
    
    async selectGenderFemale(): Promise<void> {
        await this.genderFemaleButton.click();
    }
    
    async enterAge(age: string): Promise<void> {
        await this.ageTextField.setValue(age);
    }
    
    async tapRequestGPS(): Promise<void> {
        await this.requestGPSButton.click();
    }
    
    async enterLatitude(latitude: string): Promise<void> {
        await this.latitudeTextField.setValue(latitude);
    }
    
    async enterLongitude(longitude: string): Promise<void> {
        await this.longitudeTextField.setValue(longitude);
    }
    
    async enterDescription(description: string): Promise<void> {
        await this.descriptionTextArea.setValue(description);
    }
    
    async tapContinue(): Promise<void> {
        await this.continueButton.click();
    }
}

