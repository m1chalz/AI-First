import XCTest
@testable import PetSpot

@MainActor
final class AnimalDescriptionViewModelTests: XCTestCase {
    
    // MARK: - Test Doubles
    
    private var flowState: ReportMissingPetFlowState!
    private var fakeLocationService: FakeLocationService!
    private var locationHandler: LocationPermissionHandler!
    private var toastSchedulerFake: ToastSchedulerFake!
    private var viewModel: AnimalDescriptionViewModel!
    
    // MARK: - Setup / Teardown
    
    override func setUp() async throws {
        try await super.setUp()
        
        let fakeCache = PhotoAttachmentCacheFake()
        flowState = ReportMissingPetFlowState(photoAttachmentCache: fakeCache)
        
        fakeLocationService = FakeLocationService()
        locationHandler = LocationPermissionHandler(
            locationService: fakeLocationService,
            notificationCenter: NotificationCenter()  // Isolated instance
        )
        
        toastSchedulerFake = ToastSchedulerFake()
        
        viewModel = AnimalDescriptionViewModel(
            flowState: flowState,
            locationHandler: locationHandler,
            toastScheduler: toastSchedulerFake
        )
    }
    
    override func tearDown() async throws {
        viewModel = nil
        toastSchedulerFake = nil
        locationHandler = nil
        fakeLocationService = nil
        flowState = nil
        try await super.tearDown()
    }
    
    // MARK: - Initialization Tests
    
    func testInit_whenFlowStateEmpty_shouldInitializeWithDefaults() {
        // Given/When - fresh flow state in setUp
        
        // Then - default values
        XCTAssertNotNil(viewModel.disappearanceDate, "Should have default date")
        XCTAssertNil(viewModel.selectedSpecies, "Species should be nil")
        XCTAssertEqual(viewModel.race, "", "Race should be empty")
        XCTAssertNil(viewModel.selectedGender, "Gender should be nil")
        XCTAssertEqual(viewModel.age, "", "Age should be empty")
        XCTAssertEqual(viewModel.latitude, "", "Latitude should be empty")
        XCTAssertEqual(viewModel.longitude, "", "Longitude should be empty")
        XCTAssertEqual(viewModel.additionalDescription, "", "Description should be empty")
    }
    
    func testInit_whenFlowStateHasData_shouldLoadExistingData() async {
        // Given - flow state with existing data
        let existingDate = Date(timeIntervalSince1970: 1700000000)
        flowState.disappearanceDate = existingDate
        flowState.animalSpecies = .dog
        flowState.animalRace = "Labrador"
        flowState.animalGender = .male
        flowState.animalAge = 5
        flowState.animalLatitude = 52.2297
        flowState.animalLongitude = 21.0122
        flowState.animalAdditionalDescription = "Brown with white paws"
        
        // When - create ViewModel
        let vm = AnimalDescriptionViewModel(
            flowState: flowState,
            locationHandler: locationHandler,
            toastScheduler: ToastSchedulerFake()
        )
        
        // Then - loads existing data
        XCTAssertEqual(vm.disappearanceDate.timeIntervalSince1970, existingDate.timeIntervalSince1970, accuracy: 1)
        XCTAssertEqual(vm.selectedSpecies, .dog)
        XCTAssertEqual(vm.race, "Labrador")
        XCTAssertEqual(vm.selectedGender, .male)
        XCTAssertEqual(vm.age, "5")
        XCTAssertEqual(vm.latitude, "52.22970")
        XCTAssertEqual(vm.longitude, "21.01220")
        XCTAssertEqual(vm.additionalDescription, "Brown with white paws")
    }
    
    // MARK: - GPS Position Tests
    
    func testRequestGPSPosition_whenAuthorized_shouldPopulateCoordinates() async {
        // Given - location service authorized with sample location
        await fakeLocationService.setStatus(.authorizedWhenInUse)
        let sampleLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        await fakeLocationService.setLocation(sampleLocation)
        
        // When - request GPS position
        await viewModel.requestGPSPosition()
        
        // Then - coordinates populated
        XCTAssertEqual(viewModel.latitude, "52.22970")
        XCTAssertEqual(viewModel.longitude, "21.01220")
        XCTAssertNotNil(viewModel.gpsHelperText)
        XCTAssertFalse(viewModel.showPermissionDeniedAlert)
    }
    
    func testRequestGPSPosition_whenDenied_shouldShowAlert() async {
        // Given - location service denied
        await fakeLocationService.setStatus(.denied)
        
        // When - request GPS position
        await viewModel.requestGPSPosition()
        
        // Then - shows permission alert, coordinates not populated
        XCTAssertTrue(viewModel.showPermissionDeniedAlert)
        XCTAssertEqual(viewModel.latitude, "")
        XCTAssertEqual(viewModel.longitude, "")
    }
    
    func testRequestGPSPosition_whenNotDetermined_shouldRequestPermission() async {
        // Given - location service not determined, will grant permission
        await fakeLocationService.setStatus(.notDetermined)
        let sampleLocation = Coordinate(latitude: 40.7128, longitude: -74.0060)
        await fakeLocationService.setLocation(sampleLocation)
        
        // Simulate user granting permission
        await fakeLocationService.setStatus(.authorizedWhenInUse)
        
        // When - request GPS position
        await viewModel.requestGPSPosition()
        
        // Then - coordinates populated after permission granted
        XCTAssertEqual(viewModel.latitude, "40.71280")
        XCTAssertEqual(viewModel.longitude, "-74.00600")
        XCTAssertFalse(viewModel.showPermissionDeniedAlert)
    }
    
    // MARK: - Validation Tests
    
    func testOnContinueTapped_whenAllRequiredFieldsFilled_shouldCallOnContinue() {
        // Given - all required fields filled
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        
        var continueCallbackInvoked = false
        viewModel.onContinue = {
            continueCallbackInvoked = true
        }
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - callback invoked, no toast
        XCTAssertTrue(continueCallbackInvoked)
        XCTAssertFalse(viewModel.showToast)
    }
    
    func testOnContinueTapped_whenMissingSpecies_shouldShowToastAndError() {
        // Given - missing species
        viewModel.disappearanceDate = Date()
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        // selectedSpecies = nil
        
        var continueCallbackInvoked = false
        viewModel.onContinue = {
            continueCallbackInvoked = true
        }
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows toast, doesn't call continue, shows error
        XCTAssertFalse(continueCallbackInvoked)
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.speciesErrorMessage)
        // Verify ToastScheduler was called with 3 second duration
        XCTAssertEqual(toastSchedulerFake.scheduledDurations, [3.0])
    }
    
    func testOnContinueTapped_whenMissingGender_shouldShowToastAndError() {
        // Given - missing gender
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        // selectedGender = nil
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows validation error
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.genderErrorMessage)
    }
    
    func testOnContinueTapped_whenInvalidAge_shouldShowError() {
        // Given - all required fields + invalid age (out of range)
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.age = "50"  // Max is 40
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows age validation error
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.ageErrorMessage)
    }
    
    func testOnContinueTapped_whenInvalidLatitude_shouldShowError() {
        // Given - all required fields + invalid latitude
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.latitude = "100"  // Max is 90
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows latitude validation error
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.latitudeErrorMessage)
    }
    
    func testOnContinueTapped_whenInvalidLongitude_shouldShowError() {
        // Given - all required fields + invalid longitude
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.longitude = "200"  // Max is 180
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows longitude validation error
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.longitudeErrorMessage)
    }
    
    // MARK: - Species Change Tests
    
    func testHandleSpeciesChange_shouldClearRaceField() {
        // Given - race already filled
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.raceErrorMessage = "Some error"
        
        // When - change species
        viewModel.selectedSpecies = .cat
        viewModel.handleSpeciesChange()
        
        // Then - race cleared
        XCTAssertEqual(viewModel.race, "")
        XCTAssertNil(viewModel.raceErrorMessage)
    }
    
    // MARK: - Coordinator Callback Tests
    
    func testOnBackTapped_shouldInvokeOnBackCallback() {
        // Given - callback set
        var backCallbackInvoked = false
        viewModel.onBack = {
            backCallbackInvoked = true
        }
        
        // When - tap back
        viewModel.onBackTapped()
        
        // Then - callback invoked
        XCTAssertTrue(backCallbackInvoked)
    }
    
    // MARK: - Character Count Tests
    
    func testCharacterCountText_shouldShowCorrectCount() {
        // Given - description with text
        viewModel.additionalDescription = "Test"
        
        // When - read character count
        let countText = viewModel.characterCountText
        
        // Then - shows correct count
        XCTAssertEqual(countText, "4/500")
    }
    
    // MARK: - Flow State Update Tests
    
    func testOnContinueTapped_whenValid_shouldUpdateFlowStateWithAllRequiredFields() {
        // Given - all required fields filled
        let testDate = Date(timeIntervalSince1970: 1700000000)
        viewModel.disappearanceDate = testDate
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - flow state updated with required fields
        XCTAssertNotNil(flowState.disappearanceDate)
        XCTAssertEqual(flowState.disappearanceDate!.timeIntervalSince1970, testDate.timeIntervalSince1970, accuracy: 1)
        XCTAssertEqual(flowState.animalSpecies, .dog)
        XCTAssertEqual(flowState.animalRace, "Labrador")
        XCTAssertEqual(flowState.animalGender, .male)
    }
    
    func testOnContinueTapped_whenValidWithOptionalFields_shouldUpdateFlowStateWithAllData() {
        // Given - all fields filled (required + optional)
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .cat
        viewModel.race = "Persian"
        viewModel.selectedGender = .female
        viewModel.age = "3"
        viewModel.latitude = "52.22970"
        viewModel.longitude = "21.01220"
        viewModel.additionalDescription = "White fur with blue eyes"
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - flow state updated with all data
        XCTAssertEqual(flowState.animalSpecies, .cat)
        XCTAssertEqual(flowState.animalRace, "Persian")
        XCTAssertEqual(flowState.animalGender, .female)
        XCTAssertEqual(flowState.animalAge!, 3)
        XCTAssertEqual(flowState.animalLatitude!, 52.22970, accuracy: 0.00001)
        XCTAssertEqual(flowState.animalLongitude!, 21.01220, accuracy: 0.00001)
        XCTAssertEqual(flowState.animalAdditionalDescription, "White fur with blue eyes")
    }
    
    func testOnContinueTapped_whenOptionalFieldsEmpty_shouldSaveNilInFlowState() {
        // Given - only required fields filled
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.age = ""
        viewModel.latitude = ""
        viewModel.longitude = ""
        viewModel.additionalDescription = ""
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - optional fields are nil in flow state
        XCTAssertNil(flowState.animalAge)
        XCTAssertNil(flowState.animalLatitude)
        XCTAssertNil(flowState.animalLongitude)
        XCTAssertNil(flowState.animalAdditionalDescription)
    }
    
    func testOnContinueTapped_whenRaceHasWhitespace_shouldTrimInFlowState() {
        // Given - race with leading/trailing whitespace
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "  Labrador  "
        viewModel.selectedGender = .male
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - race trimmed in flow state
        XCTAssertEqual(flowState.animalRace, "Labrador")
    }
    
    // MARK: - Field Change Handler Tests
    
    func testHandleRaceChange_whenTextNotEmpty_shouldClearError() {
        // Given - race error set
        viewModel.raceErrorMessage = "Race is required"
        
        // When - user types in race field
        viewModel.race = "L"
        viewModel.handleRaceChange("L")
        
        // Then - error cleared
        XCTAssertNil(viewModel.raceErrorMessage)
    }
    
    func testHandleRaceChange_whenTextEmpty_shouldNotClearError() {
        // Given - race error set
        viewModel.raceErrorMessage = "Race is required"
        
        // When - user clears race field
        viewModel.handleRaceChange("")
        
        // Then - error still present
        XCTAssertNotNil(viewModel.raceErrorMessage)
    }
    
    func testHandleGenderChange_shouldClearError() {
        // Given - gender error set
        viewModel.genderErrorMessage = "Gender is required"
        
        // When - user selects gender
        viewModel.selectedGender = .male
        viewModel.handleGenderChange()
        
        // Then - error cleared
        XCTAssertNil(viewModel.genderErrorMessage)
    }
    
    func testHandleSpeciesChange_whenSpeciesErrorSet_shouldClearError() {
        // Given - species error set
        viewModel.speciesErrorMessage = "Species is required"
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        
        // When - change species
        viewModel.handleSpeciesChange()
        
        // Then - both species and race errors cleared
        XCTAssertNil(viewModel.speciesErrorMessage)
        XCTAssertNil(viewModel.raceErrorMessage)
    }
    
    // MARK: - Validation Edge Cases
    
    func testOnContinueTapped_whenAgeIsNotNumeric_shouldShowError() {
        // Given - all required fields + non-numeric age
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.age = "abc"
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows age validation error
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.ageErrorMessage)
    }
    
    func testOnContinueTapped_whenOnlyLatitudeFilled_shouldShowError() {
        // Given - all required fields + only latitude
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.latitude = "52.2297"
        viewModel.longitude = ""
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows coordinate format error
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.latitudeErrorMessage)
        XCTAssertNotNil(viewModel.longitudeErrorMessage)
    }
    
    func testOnContinueTapped_whenOnlyLongitudeFilled_shouldShowError() {
        // Given - all required fields + only longitude
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.latitude = ""
        viewModel.longitude = "21.0122"
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows coordinate format error
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.latitudeErrorMessage)
        XCTAssertNotNil(viewModel.longitudeErrorMessage)
    }
    
    func testOnContinueTapped_whenCoordinatesNonNumeric_shouldShowError() {
        // Given - all required fields + non-numeric coordinates
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.latitude = "abc"
        viewModel.longitude = "xyz"
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows coordinate format error
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.latitudeErrorMessage)
        XCTAssertNotNil(viewModel.longitudeErrorMessage)
    }
    
    func testOnContinueTapped_whenLatitudeAtBoundary_shouldPass() {
        // Given - all required fields + boundary latitude values
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.latitude = "90"
        viewModel.longitude = "0"
        
        var continueCallbackInvoked = false
        viewModel.onContinue = {
            continueCallbackInvoked = true
        }
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - validation passes
        XCTAssertTrue(continueCallbackInvoked)
        XCTAssertFalse(viewModel.showToast)
        XCTAssertNil(viewModel.latitudeErrorMessage)
    }
    
    func testOnContinueTapped_whenLongitudeAtBoundary_shouldPass() {
        // Given - all required fields + boundary longitude values
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.latitude = "0"
        viewModel.longitude = "180"
        
        var continueCallbackInvoked = false
        viewModel.onContinue = {
            continueCallbackInvoked = true
        }
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - validation passes
        XCTAssertTrue(continueCallbackInvoked)
        XCTAssertFalse(viewModel.showToast)
        XCTAssertNil(viewModel.longitudeErrorMessage)
    }
    
    func testOnContinueTapped_whenNegativeCoordinatesValid_shouldPass() {
        // Given - all required fields + negative coordinates (valid range)
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.latitude = "-45.5"
        viewModel.longitude = "-120.3"
        
        var continueCallbackInvoked = false
        viewModel.onContinue = {
            continueCallbackInvoked = true
        }
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - validation passes
        XCTAssertTrue(continueCallbackInvoked)
        XCTAssertFalse(viewModel.showToast)
    }
    
    func testOnContinueTapped_whenCoordinatesWithWhitespace_shouldValidateCorrectly() {
        // Given - all required fields + coordinates with whitespace
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.latitude = "  52.2297  "
        viewModel.longitude = "  21.0122  "
        
        var continueCallbackInvoked = false
        viewModel.onContinue = {
            continueCallbackInvoked = true
        }
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - validation passes (trimmed automatically)
        XCTAssertTrue(continueCallbackInvoked)
        XCTAssertFalse(viewModel.showToast)
    }
    
    // MARK: - GPS Helper Text Tests
    
    func testRequestGPSPosition_whenAuthorizedButLocationFailed_shouldShowFailureMessage() async {
        // Given - location service authorized but returns nil location
        await fakeLocationService.setStatus(.authorizedWhenInUse)
        await fakeLocationService.setLocation(nil)
        
        // When - request GPS position
        await viewModel.requestGPSPosition()
        
        // Then - shows failure message
        XCTAssertEqual(viewModel.gpsHelperText, "Failed to get location")
        XCTAssertEqual(viewModel.latitude, "")
        XCTAssertEqual(viewModel.longitude, "")
    }
    
    // MARK: - Computed Properties Tests
    
    func testRaceTextFieldModel_whenSpeciesNotSelected_shouldBeDisabled() {
        // Given - no species selected
        viewModel.selectedSpecies = nil
        
        // When - get race text field model
        let model = viewModel.raceTextFieldModel
        
        // Then - field is disabled
        XCTAssertTrue(model.isDisabled)
    }
    
    func testRaceTextFieldModel_whenSpeciesSelected_shouldBeEnabled() {
        // Given - species selected
        viewModel.selectedSpecies = .dog
        
        // When - get race text field model
        let model = viewModel.raceTextFieldModel
        
        // Then - field is enabled
        XCTAssertFalse(model.isDisabled)
    }
    
    func testToastMessage_whenValidationFails_shouldShowCorrectMessage() {
        // Given - missing required field
        viewModel.disappearanceDate = Date()
        // selectedSpecies = nil
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - toast message set
        XCTAssertTrue(viewModel.showToast)
        XCTAssertFalse(viewModel.toastMessage.isEmpty)
    }
    
    // MARK: - Pet Name Tests
    
    func test_petName_whenUserEntersText_shouldUpdatePublishedProperty() {
        // Given - fresh ViewModel
        
        // When - user types in pet name field
        viewModel.petName = "Max"
        
        // Then - property updates
        XCTAssertEqual(viewModel.petName, "Max")
    }
    
    func test_petName_whenFlowStateHasPetName_shouldInitializeProperty() {
        // Given - flow state with existing pet name
        flowState.petName = "Buddy"
        
        // When - create new ViewModel
        let vm = AnimalDescriptionViewModel(
            flowState: flowState,
            locationHandler: locationHandler,
            toastScheduler: ToastSchedulerFake()
        )
        
        // Then - pet name loaded from flow state
        XCTAssertEqual(vm.petName, "Buddy")
    }
    
    func test_petName_whenFlowStateHasNoPetName_shouldInitializeToEmptyString() {
        // Given - flow state without pet name (nil)
        XCTAssertNil(flowState.petName)
        
        // When - create new ViewModel (already created in setUp)
        
        // Then - pet name initializes to empty string
        XCTAssertEqual(viewModel.petName, "")
    }
    
    func test_onContinueTapped_whenPetNameHasText_shouldStoreTrimmedValue() {
        // Given - all required fields + pet name with whitespace
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.petName = "  Max  "
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - flow state updated with trimmed pet name
        XCTAssertEqual(flowState.petName, "Max")
    }
    
    func test_onContinueTapped_whenPetNameIsEmpty_shouldStoreNil() {
        // Given - all required fields + empty pet name
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.petName = ""
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - flow state pet name is nil
        XCTAssertNil(flowState.petName)
    }
    
    func test_onContinueTapped_whenPetNameIsWhitespaceOnly_shouldStoreNil() {
        // Given - all required fields + whitespace-only pet name
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.petName = "   "
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - flow state pet name is nil (whitespace trimmed away)
        XCTAssertNil(flowState.petName)
    }
}

