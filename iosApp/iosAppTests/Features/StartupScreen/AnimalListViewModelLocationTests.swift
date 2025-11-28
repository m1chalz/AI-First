import XCTest
@testable import PetSpot

/**
 * Unit tests for AnimalListViewModel location permission and fetching logic.
 * Tests User Story 1: Location-Aware Content for Authorized Users.
 * Uses FakeLocationService and FakeAnimalRepository for isolation.
 * Follows Given-When-Then structure per constitution.
 */
@MainActor
final class AnimalListViewModelLocationTests: XCTestCase {
    
    // MARK: - Test Doubles
    
    private var fakeLocationService: FakeLocationService!
    private var fakeRepository: FakeAnimalRepository!
    private var locationHandler: LocationPermissionHandler!
    private var viewModel: AnimalListViewModel!
    
    // MARK: - Setup / Teardown
    
    override func setUp() async throws {
        try await super.setUp()
        fakeLocationService = FakeLocationService()
        fakeRepository = FakeAnimalRepository()
    }
    
    override func tearDown() async throws {
        viewModel = nil
        locationHandler = nil
        fakeRepository = nil
        fakeLocationService = nil
        try await super.tearDown()
    }
    
    // MARK: - Helper Methods
    
    /// Creates AnimalListViewModel with LocationPermissionHandler wrapping FakeLocationService.
    /// Uses isolated NotificationCenter for test isolation.
    private func createViewModel() -> AnimalListViewModel {
        locationHandler = LocationPermissionHandler(
            locationService: fakeLocationService,
            notificationCenter: NotificationCenter()  // Isolated instance
        )
        
        return AnimalListViewModel(
            repository: fakeRepository,
            locationHandler: locationHandler
        )
    }
    
    // MARK: - T019: loadAnimals fetches location when authorized
    
    func test_loadAnimals_whenLocationPermissionGranted_shouldFetchLocation() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(UserLocation(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnimals = [Animal(
            id: "test",
            name: "Test",
            photoUrl: "test",
            location: Location(city: "Test", radiusKm: 1),
            species: .dog,
            breed: "Test",
            gender: .male,
            status: .active,
            lastSeenDate: "2025-11-20",
            description: "Test",
            email: "test@example.com",
            phone: nil
        )]
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let locationCalled = await fakeLocationService.requestLocationCalled
        XCTAssertTrue(locationCalled, "Should request location when permission granted")
    }
    
    // MARK: - T020: loadAnimals queries with coordinates when location available
    
    func test_loadAnimals_whenLocationAvailable_shouldQueryWithCoordinates() async {
        // Given
        let expectedLocation = UserLocation(latitude: 52.2297, longitude: 21.0122)
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(expectedLocation)
        fakeRepository.stubbedAnimals = [Animal(
            id: "test",
            name: "Test",
            photoUrl: "test",
            location: Location(city: "Test", radiusKm: 1),
            species: .dog,
            breed: "Test",
            gender: .male,
            status: .active,
            lastSeenDate: "2025-11-20",
            description: "Test",
            email: "test@example.com",
            phone: nil
        )]
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let passedLocation = fakeRepository.lastLocationParameter
        XCTAssertNotNil(passedLocation, "Should pass location to repository")
        guard let location = passedLocation else {
            XCTFail("Location should not be nil")
            return
        }
        XCTAssertEqual(location.latitude, expectedLocation.latitude, accuracy: 0.0001)
        XCTAssertEqual(location.longitude, expectedLocation.longitude, accuracy: 0.0001)
    }
    
    // MARK: - T021: loadAnimals queries without coordinates when location fetch fails
    
    func test_loadAnimals_whenLocationFetchFails_shouldQueryWithoutCoordinates() async {
        // Given
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(nil) // Simulate location fetch failure
        fakeRepository.stubbedAnimals = [Animal(
            id: "test",
            name: "Test",
            photoUrl: "test",
            location: Location(city: "Test", radiusKm: 1),
            species: .dog,
            breed: "Test",
            gender: .male,
            status: .active,
            lastSeenDate: "2025-11-20",
            description: "Test",
            email: "test@example.com",
            phone: nil
        )]
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let passedLocation = fakeRepository.lastLocationParameter
        XCTAssertNil(passedLocation, "Should pass nil location when fetch fails")
    }
    
    func test_loadAnimals_whenPermissionDenied_shouldNotFetchLocation() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.denied)
        fakeRepository.stubbedAnimals = [Animal(
            id: "test",
            name: "Test",
            photoUrl: "test",
            location: Location(city: "Test", radiusKm: 1),
            species: .dog,
            breed: "Test",
            gender: .male,
            status: .active,
            lastSeenDate: "2025-11-20",
            description: "Test",
            email: "test@example.com",
            phone: nil
        )]
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let locationCalled = await fakeLocationService.requestLocationCalled
        XCTAssertFalse(locationCalled, "Should not request location when permission denied")
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without location when denied")
    }
    
    func test_loadAnimals_whenPermissionNotDetermined_shouldQueryWithoutLocation() async {
        // Given
        await setLocationServiceStatus(.notDetermined)
        await setLocationServiceLocation(nil)
        fakeRepository.stubbedAnimals = [Animal(
            id: "test",
            name: "Test",
            photoUrl: "test",
            location: Location(city: "Test", radiusKm: 1),
            species: .dog,
            breed: "Test",
            gender: .male,
            status: .active,
            lastSeenDate: "2025-11-20",
            description: "Test",
            email: "test@example.com",
            phone: nil
        )]
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without location when status notDetermined")
    }
    
    // MARK: - User Story 2 Tests: First-Time Permission Request
    
    // T033: Unit test LocationService.requestWhenInUseAuthorization with notDetermined status
    func test_loadAnimals_whenPermissionNotDetermined_shouldRequestAuthorization() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.notDetermined)
        await fakeLocationService.setAuthorizationAfterRequest(.authorizedWhenInUse)
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let requestCalled = await fakeLocationService.requestAuthorizationCalled
        XCTAssertTrue(requestCalled, "Should request authorization when status is notDetermined")
    }
    
    // T034: Unit test LocationService.requestWhenInUseAuthorization returns immediately when already authorized
    func test_loadAnimals_whenAlreadyAuthorized_shouldNotRequestAuthorizationAgain() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(UserLocation(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let requestCalled = await fakeLocationService.requestAuthorizationCalled
        XCTAssertFalse(requestCalled, "Should not request authorization when already authorized")
    }
    
    // T035: Unit test LocationService.requestWhenInUseAuthorization returns immediately when already denied
    func test_loadAnimals_whenAlreadyDenied_shouldNotRequestAuthorization() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.denied)
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        let requestCalled = await fakeLocationService.requestAuthorizationCalled
        XCTAssertFalse(requestCalled, "Should not request authorization when already denied")
    }
    
    // T036: Unit test AnimalListViewModel.loadAnimals requests permission when notDetermined
    func test_loadAnimals_whenNotDetermined_shouldUpdatePermissionStatusAfterRequest() async {
        // Given
        await setLocationServiceStatus(.notDetermined)
        await fakeLocationService.setAuthorizationAfterRequest(.authorizedWhenInUse)
        await setLocationServiceLocation(UserLocation(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        XCTAssertEqual(viewModel.locationPermissionStatus, .authorizedWhenInUse, "Should update permission status after user grants permission")
    }
    
    // T037: Unit test AnimalListViewModel handles user granting permission in alert
    func test_loadAnimals_whenUserGrantsPermission_shouldFetchLocationAndQueryWithCoordinates() async {
        // Given
        await setLocationServiceStatus(.notDetermined)
        await fakeLocationService.setAuthorizationAfterRequest(.authorizedWhenInUse)
        let expectedLocation = UserLocation(latitude: 52.2297, longitude: 21.0122)
        await setLocationServiceLocation(expectedLocation)
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        XCTAssertEqual(viewModel.locationPermissionStatus, .authorizedWhenInUse, "Should update status to authorized")
        XCTAssertNotNil(viewModel.currentLocation, "Should fetch location after permission granted")
        let passedLocation = fakeRepository.lastLocationParameter
        XCTAssertNotNil(passedLocation, "Should query with coordinates after permission granted")
    }
    
    // T038: Unit test AnimalListViewModel handles user denying permission in alert
    func test_loadAnimals_whenUserDeniesPermission_shouldQueryWithoutCoordinates() async {
        // Given
        await setLocationServiceStatus(.notDetermined)
        await fakeLocationService.setAuthorizationAfterRequest(.denied)
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        XCTAssertEqual(viewModel.locationPermissionStatus, .denied, "Should update status to denied")
        XCTAssertNil(viewModel.currentLocation, "Should not have location when permission denied")
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without coordinates when permission denied")
    }
    
    // MARK: - User Story 3 Tests: Recovery Path for Denied Permissions
    
    // T046: Unit test LocationPermissionStatus.shouldShowCustomPopup extension
    func test_shouldShowCustomPopup_whenDenied_shouldReturnTrue() {
        // Given
        let status = LocationPermissionStatus.denied
        
        // When
        let result = status.shouldShowCustomPopup
        
        // Then
        XCTAssertTrue(result, "Denied status should show custom popup")
    }
    
    func test_shouldShowCustomPopup_whenRestricted_shouldReturnTrue() {
        // Given
        let status = LocationPermissionStatus.restricted
        
        // When
        let result = status.shouldShowCustomPopup
        
        // Then
        XCTAssertTrue(result, "Restricted status should show custom popup")
    }
    
    func test_shouldShowCustomPopup_whenAuthorized_shouldReturnFalse() {
        // Given
        let status = LocationPermissionStatus.authorizedWhenInUse
        
        // When
        let result = status.shouldShowCustomPopup
        
        // Then
        XCTAssertFalse(result, "Authorized status should not show custom popup")
    }
    
    func test_shouldShowCustomPopup_whenNotDetermined_shouldReturnFalse() {
        // Given
        let status = LocationPermissionStatus.notDetermined
        
        // When
        let result = status.shouldShowCustomPopup
        
        // Then
        XCTAssertFalse(result, "NotDetermined status should not show custom popup")
    }
    
    // T047: Unit test AnimalListViewModel shows custom popup for denied status
    func test_loadAnimals_whenPermissionDenied_shouldShowCustomPopup() async {
        // Given
        await setLocationServiceStatus(.denied)
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        XCTAssertTrue(viewModel.showPermissionDeniedAlert, "Should show custom popup when permission denied")
    }
    
    // T048: Unit test AnimalListViewModel shows custom popup for restricted status
    func test_loadAnimals_whenPermissionRestricted_shouldShowCustomPopup() async {
        // Given
        await setLocationServiceStatus(.restricted)
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnimals()
        
        // Then
        XCTAssertTrue(viewModel.showPermissionDeniedAlert, "Should show custom popup when permission restricted")
    }
    
    // T049: Unit test AnimalListViewModel.hasShownPermissionAlert prevents repeated popups in session
    func test_loadAnimals_whenPopupAlreadyShown_shouldNotShowAgain() async {
        // Given
        await setLocationServiceStatus(.denied)
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When - First call
        await viewModel.loadAnimals()
        let firstCallShown = viewModel.showPermissionDeniedAlert
        
        // Reset alert state but keep session flag
        viewModel.showPermissionDeniedAlert = false
        
        // Second call
        await viewModel.loadAnimals()
        let secondCallShown = viewModel.showPermissionDeniedAlert
        
        // Then
        XCTAssertTrue(firstCallShown, "Should show popup on first call")
        XCTAssertFalse(secondCallShown, "Should not show popup again in same session")
    }
    
    // T050: Unit test AnimalListViewModel.openSettings() calls coordinator callback
    func test_openSettings_whenCalled_shouldInvokeCoordinatorCallback() async {
        // Given
        var callbackInvoked = false
        viewModel = createViewModel()
        viewModel.onOpenAppSettings = {
            callbackInvoked = true
        }
        
        // When
        viewModel.openSettings()
        
        // Then
        XCTAssertTrue(callbackInvoked, "Should invoke coordinator callback when openSettings called")
    }
    
    // T051: Unit test AnimalListViewModel.continueWithoutLocation() queries without coordinates
    func test_continueWithoutLocation_whenCalled_shouldQueryWithoutLocation() async {
        // Given
        fakeRepository.stubbedAnimals = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.continueWithoutLocation()
        
        // Then
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without location when continueWithoutLocation called")
        XCTAssertFalse(viewModel.showPermissionDeniedAlert, "Should dismiss popup")
    }
    
    // MARK: - User Story 4 Tests: Dynamic Permission Change Handling
    
    // NOTE: Tests T071-T074 for checkPermissionStatusChange() have been removed.
    // This functionality is now tested in LocationPermissionHandlerTests as it was
    // refactored into LocationPermissionHandler.
    // The foreground observer behavior is covered by:
    // - testCheckPermissionStatusChange_whenChangedFromDeniedToAuthorized_returnsTrue
    // - testCheckPermissionStatusChange_whenStatusUnchangedAuthorized_returnsFalse
    // - testStartObservingForeground_whenNotificationPosted_triggersCallback
    
    // MARK: - Helper Methods
    
    private func setLocationServiceStatus(_ status: LocationPermissionStatus) async {
        await fakeLocationService.setStatus(status)
    }
    
    private func setLocationServiceLocation(_ location: UserLocation?) async {
        await fakeLocationService.setLocation(location)
    }
}

// MARK: - FakeLocationService Extensions

extension FakeLocationService {
    func setStatus(_ status: LocationPermissionStatus) {
        stubbedAuthorizationStatus = status
    }
    
    func setLocation(_ location: UserLocation?) {
        stubbedLocation = location
    }
    
    func setAuthorizationAfterRequest(_ status: LocationPermissionStatus) {
        stubbedAuthorizationAfterRequest = status
    }
}

