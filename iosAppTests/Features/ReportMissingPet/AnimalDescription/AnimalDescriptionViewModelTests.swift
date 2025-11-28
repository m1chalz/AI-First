import XCTest
@testable import PetSpot

/// Unit tests for AnimalDescriptionViewModel covering all 3 user stories.
/// Tests follow Given-When-Then (Arrange-Act-Assert) structure.
@MainActor
final class AnimalDescriptionViewModelTests: XCTestCase {
    
    // System under test
    var sut: AnimalDescriptionViewModel!
    
    // Dependencies
    var fakeFlowState: FakeReportMissingPetFlowState!
    var fakeLocationService: FakeLocationService!
    
    override func setUp() {
        super.setUp()
        fakeFlowState = FakeReportMissingPetFlowState()
        fakeLocationService = FakeLocationService()
        sut = AnimalDescriptionViewModel(
            flowState: fakeFlowState,
            locationService: fakeLocationService
        )
    }
    
    override func tearDown() {
        sut = nil
        fakeFlowState = nil
        fakeLocationService = nil
        super.tearDown()
    }
    
    // MARK: - User Story 1: Required Fields Tests (T021-T037)
    
    func testSelectSpecies_whenSpeciesSelected_shouldUpdateFormDataAndClearRace() {
        // Given - species selected and race entered
        sut.race = "Labrador"
        sut.raceErrorMessage = "Some error"
        
        // When - user selects different species
        sut.selectSpecies(0) // Dog
        
        // Then - species updated and race cleared
        XCTAssertEqual(sut.selectedSpeciesIndex, 0)
        XCTAssertEqual(sut.selectedSpecies, .dog)
        XCTAssertEqual(sut.race, "")
        XCTAssertNil(sut.raceErrorMessage)
    }
    
    func testSelectSpecies_whenRacePreviouslyEntered_shouldClearRaceField() {
        // Given - race entered for dog
        sut.selectedSpeciesIndex = 0 // Dog
        sut.race = "Labrador"
        sut.raceErrorMessage = nil
        
        // When - user changes species to cat
        sut.selectSpecies(1) // Cat
        
        // Then - race field cleared
        XCTAssertEqual(sut.selectedSpeciesIndex, 1)
        XCTAssertEqual(sut.selectedSpecies, .cat)
        XCTAssertEqual(sut.race, "")
        XCTAssertNil(sut.raceErrorMessage)
    }
    
    func testSelectGender_whenGenderSelected_shouldUpdateFormData() {
        // Given - no gender selected
        XCTAssertNil(sut.selectedGenderIndex)
        
        // When - user selects male
        sut.selectGender(0) // Male
        
        // Then - gender updated
        XCTAssertEqual(sut.selectedGenderIndex, 0)
        XCTAssertEqual(sut.selectedGender, .male)
    }
    
    func testOnContinueTapped_whenRequiredFieldsEmpty_shouldShowValidationErrors() {
        // Given - empty required fields
        sut.selectedSpeciesIndex = nil
        sut.race = ""
        sut.selectedGenderIndex = nil
        
        // When - user taps Continue
        sut.onContinueTapped()
        
        // Then - validation errors shown
        XCTAssertTrue(sut.showToast)
        XCTAssertFalse(sut.toastMessage.isEmpty)
        XCTAssertNotNil(sut.speciesErrorMessage)
        XCTAssertNotNil(sut.raceErrorMessage)
        XCTAssertNotNil(sut.genderErrorMessage)
    }
    
    func testOnContinueTapped_whenRequiredFieldsValid_shouldUpdateSessionAndNavigate() {
        // Given - valid required fields
        sut.disappearanceDate = Date()
        sut.selectedSpeciesIndex = 0 // Dog
        sut.race = "Golden Retriever"
        sut.selectedGenderIndex = 0 // Male
        
        var didCallContinue = false
        sut.onContinue = { didCallContinue = true }
        
        // When - user taps Continue
        sut.onContinueTapped()
        
        // Then - flow state updated and coordinator callback invoked
        XCTAssertNotNil(fakeFlowState.disappearanceDate)
        XCTAssertEqual(fakeFlowState.animalSpecies, .dog)
        XCTAssertEqual(fakeFlowState.animalRace, "Golden Retriever")
        XCTAssertEqual(fakeFlowState.animalGender, .male)
        XCTAssertTrue(didCallContinue)
        XCTAssertFalse(sut.showToast)
    }
    
    func testRaceTextFieldModel_whenSpeciesNotSelected_shouldBeDisabled() {
        // Given - no species selected
        sut.selectedSpeciesIndex = nil
        
        // When - accessing race text field model
        let model = sut.raceTextFieldModel
        
        // Then - field is disabled
        XCTAssertTrue(model.isDisabled)
    }
    
    func testRaceTextFieldModel_whenSpeciesSelected_shouldBeEnabled() {
        // Given - species selected
        sut.selectedSpeciesIndex = 0 // Dog
        
        // When - accessing race text field model
        let model = sut.raceTextFieldModel
        
        // Then - field is enabled
        XCTAssertFalse(model.isDisabled)
    }
    
    func testValidateAllFields_whenDateMissing_shouldReturnMissingDateError() {
        // Note: Date is never missing (DatePicker defaults to today)
        // This test documents that date is always valid
        
        // Given - all other fields valid
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        
        // When - validating all fields
        sut.onContinueTapped()
        
        // Then - no date error (date is always present)
        XCTAssertFalse(sut.showToast)
    }
    
    func testValidateAllFields_whenSpeciesMissing_shouldReturnMissingSpeciesError() {
        // Given - species not selected
        sut.selectedSpeciesIndex = nil
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        
        // When - validating all fields
        sut.onContinueTapped()
        
        // Then - species error shown
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.speciesErrorMessage)
    }
    
    func testValidateAllFields_whenRaceEmpty_shouldReturnMissingRaceError() {
        // Given - race empty
        sut.selectedSpeciesIndex = 0
        sut.race = ""
        sut.selectedGenderIndex = 0
        
        // When - validating all fields
        sut.onContinueTapped()
        
        // Then - race error shown
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.raceErrorMessage)
    }
    
    func testValidateAllFields_whenGenderMissing_shouldReturnMissingGenderError() {
        // Given - gender not selected
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = nil
        
        // When - validating all fields
        sut.onContinueTapped()
        
        // Then - gender error shown
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.genderErrorMessage)
    }
    
    func testValidateAllFields_whenAllRequiredFieldsValid_shouldReturnEmptyErrors() {
        // Given - all required fields valid
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        
        var didCallContinue = false
        sut.onContinue = { didCallContinue = true }
        
        // When - validating all fields
        sut.onContinueTapped()
        
        // Then - no errors, navigation proceeds
        XCTAssertFalse(sut.showToast)
        XCTAssertNil(sut.speciesErrorMessage)
        XCTAssertNil(sut.raceErrorMessage)
        XCTAssertNil(sut.genderErrorMessage)
        XCTAssertTrue(didCallContinue)
    }
    
    // MARK: - User Story 2: GPS Capture Tests (T074-T083)
    
    func testRequestGPSPosition_whenPermissionNotDetermined_shouldRequestPermissionAndFetchLocation() async {
        // Given - permission not determined, will be granted
        await fakeLocationService.setStubbedAuthorizationStatus(.notDetermined)
        await fakeLocationService.setStubbedLocation(UserLocation(
            latitude: 52.2297,
            longitude: 21.0122,
            timestamp: Date()
        ))
        
        // When - user taps GPS button
        await sut.requestGPSPosition()
        
        // Wait for async completion
        await Task.yield()
        
        // Then - permission requested and location fetched
        let callCount = await fakeLocationService.requestAuthorizationCallCount
        XCTAssertEqual(callCount, 1)
    }
    
    func testRequestGPSPosition_whenPermissionAuthorized_shouldFetchLocation() async {
        // Given - permission already authorized
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(UserLocation(
            latitude: 52.2297,
            longitude: 21.0122,
            timestamp: Date()
        ))
        
        // When - user taps GPS button
        await sut.requestGPSPosition()
        
        // Wait for async completion
        await Task.yield()
        
        // Then - location fetched and fields populated
        let callCount = await fakeLocationService.requestLocationCallCount
        XCTAssertEqual(callCount, 1)
        XCTAssertEqual(sut.latitude, "52.22970")
        XCTAssertEqual(sut.longitude, "21.01220")
    }
    
    func testRequestGPSPosition_whenPermissionDenied_shouldShowAlert() async {
        // Given - permission denied
        await fakeLocationService.setStubbedAuthorizationStatus(.denied)
        
        // When - user taps GPS button
        await sut.requestGPSPosition()
        
        // Wait for async completion
        await Task.yield()
        
        // Then - alert shown
        XCTAssertTrue(sut.showPermissionDeniedAlert)
    }
    
    func testValidateCoordinates_whenBothEmpty_shouldReturnValid() {
        // Given - empty coordinates
        sut.latitude = ""
        sut.longitude = ""
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        
        var didCallContinue = false
        sut.onContinue = { didCallContinue = true }
        
        // When - validating with Continue tap
        sut.onContinueTapped()
        
        // Then - validation passes (coordinates optional)
        XCTAssertFalse(sut.showToast)
        XCTAssertNil(sut.latitudeErrorMessage)
        XCTAssertNil(sut.longitudeErrorMessage)
        XCTAssertTrue(didCallContinue)
    }
    
    func testValidateCoordinates_whenLatitudeOutOfRange_shouldReturnInvalidLatError() {
        // Given - latitude out of range
        sut.latitude = "100"
        sut.longitude = "21.0122"
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        
        // When - validating
        sut.onContinueTapped()
        
        // Then - latitude error shown
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.latitudeErrorMessage)
    }
    
    func testValidateCoordinates_whenLongitudeOutOfRange_shouldReturnInvalidLongError() {
        // Given - longitude out of range
        sut.latitude = "52.2297"
        sut.longitude = "200"
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        
        // When - validating
        sut.onContinueTapped()
        
        // Then - longitude error shown
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.longitudeErrorMessage)
    }
    
    func testValidateCoordinates_whenBothInRange_shouldReturnValid() {
        // Given - valid coordinates
        sut.latitude = "52.2297"
        sut.longitude = "21.0122"
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        
        var didCallContinue = false
        sut.onContinue = { didCallContinue = true }
        
        // When - validating
        sut.onContinueTapped()
        
        // Then - validation passes
        XCTAssertFalse(sut.showToast)
        XCTAssertNil(sut.latitudeErrorMessage)
        XCTAssertNil(sut.longitudeErrorMessage)
        XCTAssertTrue(didCallContinue)
    }
    
    func testValidateCoordinates_whenInvalidFormat_shouldReturnFormatError() {
        // Given - non-numeric coordinates
        sut.latitude = "abc"
        sut.longitude = "def"
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        
        // When - validating
        sut.onContinueTapped()
        
        // Then - format error shown
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.latitudeErrorMessage)
        XCTAssertNotNil(sut.longitudeErrorMessage)
    }
    
    func testOnContinueTapped_withInvalidCoordinates_shouldShowInlineErrors() {
        // Given - invalid coordinates but valid required fields
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        sut.latitude = "100"
        sut.longitude = "200"
        
        // When - tapping Continue
        sut.onContinueTapped()
        
        // Then - coordinate errors shown inline
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.latitudeErrorMessage)
        XCTAssertNotNil(sut.longitudeErrorMessage)
    }
    
    // MARK: - User Story 3: Validation & Persistence Tests (T107-T116)
    
    func testInit_whenFlowStateHasExistingData_shouldPopulateFormData() {
        // Given - flow state with existing animal description data
        let existingDate = Date()
        fakeFlowState.disappearanceDate = existingDate
        fakeFlowState.animalSpecies = .dog
        fakeFlowState.animalRace = "Golden Retriever"
        fakeFlowState.animalGender = .male
        fakeFlowState.animalAge = 5
        fakeFlowState.animalLatitude = 52.2297
        fakeFlowState.animalLongitude = 21.0122
        fakeFlowState.animalAdditionalDescription = "Friendly dog"
        
        // When - creating new ViewModel with existing flow state
        let newSut = AnimalDescriptionViewModel(
            flowState: fakeFlowState,
            locationService: fakeLocationService
        )
        
        // Then - form data populated from flow state
        XCTAssertEqual(newSut.disappearanceDate, existingDate)
        XCTAssertEqual(newSut.selectedSpecies, .dog)
        XCTAssertEqual(newSut.race, "Golden Retriever")
        XCTAssertEqual(newSut.selectedGender, .male)
        XCTAssertEqual(newSut.age, "5")
        XCTAssertEqual(newSut.latitude, "52.22970")
        XCTAssertEqual(newSut.longitude, "21.01220")
        XCTAssertEqual(newSut.additionalDescription, "Friendly dog")
    }
    
    func testInit_whenFlowStateEmpty_shouldUseDefaultFormData() {
        // Given - empty flow state (default state)
        // Already set up in setUp()
        
        // When - ViewModel initialized
        // Already initialized in setUp()
        
        // Then - form data uses defaults
        XCTAssertNotNil(sut.disappearanceDate) // Today's date
        XCTAssertNil(sut.selectedSpeciesIndex)
        XCTAssertEqual(sut.race, "")
        XCTAssertNil(sut.selectedGenderIndex)
        XCTAssertEqual(sut.age, "")
        XCTAssertEqual(sut.latitude, "")
        XCTAssertEqual(sut.longitude, "")
        XCTAssertEqual(sut.additionalDescription, "")
    }
    
    func testUpdateFlowState_whenCalled_shouldSaveFormDataToFlowState() {
        // Given - valid form data
        let testDate = Date()
        sut.disappearanceDate = testDate
        sut.selectedSpeciesIndex = 0 // Dog
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0 // Male
        sut.age = "3"
        sut.latitude = "52.2297"
        sut.longitude = "21.0122"
        sut.additionalDescription = "Very friendly"
        
        sut.onContinue = {}
        
        // When - tapping Continue (which calls updateFlowState)
        sut.onContinueTapped()
        
        // Then - flow state updated with all fields
        XCTAssertEqual(fakeFlowState.disappearanceDate, testDate)
        XCTAssertEqual(fakeFlowState.animalSpecies, .dog)
        XCTAssertEqual(fakeFlowState.animalRace, "Labrador")
        XCTAssertEqual(fakeFlowState.animalGender, .male)
        XCTAssertEqual(fakeFlowState.animalAge, 3)
        XCTAssertEqual(fakeFlowState.animalLatitude, 52.2297)
        XCTAssertEqual(fakeFlowState.animalLongitude, 21.0122)
        XCTAssertEqual(fakeFlowState.animalAdditionalDescription, "Very friendly")
    }
    
    func testOnBackTapped_whenCalled_shouldNotUpdateFlowState() {
        // Given - modified form data
        let originalSpecies = fakeFlowState.animalSpecies
        sut.selectedSpeciesIndex = 1 // Cat (different from original)
        sut.race = "Persian"
        
        var didCallBack = false
        sut.onBack = { didCallBack = true }
        
        // When - tapping Back
        sut.onBackTapped()
        
        // Then - flow state unchanged and back callback invoked
        XCTAssertEqual(fakeFlowState.animalSpecies, originalSpecies)
        XCTAssertTrue(didCallBack)
    }
    
    func testValidateAllFields_whenAgeBelowZero_shouldReturnInvalidAgeError() {
        // Given - negative age
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        sut.age = "-5"
        
        // When - validating
        sut.onContinueTapped()
        
        // Then - age error shown
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.ageErrorMessage)
    }
    
    func testValidateAllFields_whenAgeAbove40_shouldReturnInvalidAgeError() {
        // Given - age above limit
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        sut.age = "50"
        
        // When - validating
        sut.onContinueTapped()
        
        // Then - age error shown
        XCTAssertTrue(sut.showToast)
        XCTAssertNotNil(sut.ageErrorMessage)
    }
    
    func testValidateAllFields_whenAgeEmpty_shouldReturnValid() {
        // Given - empty age (optional field)
        sut.selectedSpeciesIndex = 0
        sut.race = "Labrador"
        sut.selectedGenderIndex = 0
        sut.age = ""
        
        var didCallContinue = false
        sut.onContinue = { didCallContinue = true }
        
        // When - validating
        sut.onContinueTapped()
        
        // Then - no age error
        XCTAssertFalse(sut.showToast)
        XCTAssertNil(sut.ageErrorMessage)
        XCTAssertTrue(didCallContinue)
    }
    
    func testCharacterCountText_whenDescriptionEmpty_shouldReturn0Slash500() {
        // Given - empty description
        sut.additionalDescription = ""
        
        // When - accessing character count text
        let countText = sut.characterCountText
        
        // Then - returns "0/500"
        XCTAssertEqual(countText, "0/500")
    }
    
    func testCharacterCountText_whenDescription123Chars_shouldReturn123Slash500() {
        // Given - description with 123 characters
        sut.additionalDescription = String(repeating: "a", count: 123)
        
        // When - accessing character count text
        let countText = sut.characterCountText
        
        // Then - returns "123/500"
        XCTAssertEqual(countText, "123/500")
    }
    
    func testCharacterCountColor_whenNearLimit_shouldReturnWarningColor() {
        // Given - description near limit (490+ chars)
        sut.additionalDescription = String(repeating: "a", count: 490)
        
        // When - accessing character count color
        let color = sut.characterCountColor
        
        // Then - returns red color
        XCTAssertEqual(color, .red)
    }
}

// MARK: - Test Helpers

extension FakeLocationService {
    func setStubbedAuthorizationStatus(_ status: LocationPermissionStatus) async {
        self.stubbedAuthorizationStatus = status
    }
    
    func setStubbedLocation(_ location: UserLocation?) async {
        self.stubbedLocation = location
    }
}

