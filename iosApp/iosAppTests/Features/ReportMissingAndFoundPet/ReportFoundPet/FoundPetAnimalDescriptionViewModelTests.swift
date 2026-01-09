import XCTest
@testable import PetSpot

@MainActor
final class FoundPetAnimalDescriptionViewModelTests: XCTestCase {
    
    // MARK: - Test Doubles
    
    private var flowState: FoundPetReportFlowState!
    private var fakeLocationService: FakeLocationService!
    private var locationHandler: LocationPermissionHandler!
    private var toastSchedulerFake: ToastSchedulerFake!
    private var viewModel: FoundPetAnimalDescriptionViewModel!
    
    // MARK: - Setup / Teardown
    
    override func setUp() async throws {
        try await super.setUp()
        
        let fakeCache = PhotoAttachmentCacheFake()
        flowState = FoundPetReportFlowState(photoAttachmentCache: fakeCache)
        
        fakeLocationService = FakeLocationService()
        locationHandler = LocationPermissionHandler(
            locationService: fakeLocationService,
            notificationCenter: NotificationCenter()  // Isolated instance
        )
        
        toastSchedulerFake = ToastSchedulerFake()
        
        viewModel = FoundPetAnimalDescriptionViewModel(
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
        flowState.animalLatitude = 52.2297
        flowState.animalLongitude = 21.0122
        flowState.animalAdditionalDescription = "Brown with white paws"
        
        // When - create ViewModel
        let vm = FoundPetAnimalDescriptionViewModel(
            flowState: flowState,
            locationHandler: locationHandler,
            toastScheduler: ToastSchedulerFake()
        )
        
        // Then - loads existing data
        XCTAssertEqual(vm.disappearanceDate.timeIntervalSince1970, existingDate.timeIntervalSince1970, accuracy: 1)
        XCTAssertEqual(vm.selectedSpecies, .dog)
        XCTAssertEqual(vm.race, "Labrador")
        XCTAssertEqual(vm.selectedGender, .male)
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
    
    // MARK: - Validation Tests
    
    func testOnContinueTapped_whenAllRequiredFieldsFilled_shouldCallOnContinue() {
        // Given - all required fields filled
        viewModel.disappearanceDate = Date()
        viewModel.selectedSpecies = .dog
        viewModel.race = "Labrador"
        viewModel.selectedGender = .male
        viewModel.latitude = "52.2297"
        viewModel.longitude = "21.0122"
        
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
        viewModel.latitude = "52.2297"
        viewModel.longitude = "21.0122"
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
        viewModel.latitude = "52.2297"
        viewModel.longitude = "21.0122"
        // selectedGender = nil
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - shows validation error
        XCTAssertTrue(viewModel.showToast)
        XCTAssertNotNil(viewModel.genderErrorMessage)
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
        viewModel.latitude = "52.2297"
        viewModel.longitude = "21.0122"
        
        // When - tap continue
        viewModel.onContinueTapped()
        
        // Then - flow state updated with required fields
        XCTAssertNotNil(flowState.disappearanceDate)
        XCTAssertEqual(flowState.disappearanceDate!.timeIntervalSince1970, testDate.timeIntervalSince1970, accuracy: 1)
        XCTAssertEqual(flowState.animalSpecies, .dog)
        XCTAssertEqual(flowState.animalRace, "Labrador")
        XCTAssertEqual(flowState.animalGender, .male)
        XCTAssertEqual(flowState.animalLatitude!, 52.2297, accuracy: 0.00001)
        XCTAssertEqual(flowState.animalLongitude!, 21.0122, accuracy: 0.00001)
    }
    
}

