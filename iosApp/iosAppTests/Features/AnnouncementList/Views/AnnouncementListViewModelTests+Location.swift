import XCTest
@testable import PetSpot

/**
 * Unit tests for AnnouncementListViewModel location permission and fetching logic.
 * Tests User Story 1: Location-Aware Content for Authorized Users.
 * Uses FakeLocationService and FakeAnnouncementRepository for isolation.
 * Follows Given-When-Then structure per constitution.
 *
 * **Note**: Permission popup tests (User Story 3) are in LandingPageViewModelTests
 * since LandingPage handles the permission popup UI.
 */
@MainActor
final class AnnouncementListViewModelLocationTests: XCTestCase {
    
    // MARK: - Test Doubles
    
    private var fakeLocationService: FakeLocationService!
    private var fakeRepository: FakeAnnouncementRepository!
    private var locationHandler: LocationPermissionHandler!
    private var viewModel: AnnouncementListViewModel!
    
    // MARK: - Setup / Teardown
    
    override func setUp() async throws {
        try await super.setUp()
        fakeLocationService = FakeLocationService()
        fakeRepository = FakeAnnouncementRepository()
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
    private func createViewModel() -> AnnouncementListViewModel {
        locationHandler = LocationPermissionHandler(
            locationService: fakeLocationService,
            notificationCenter: NotificationCenter()  // Isolated instance
        )
        
        return AnnouncementListViewModel(
            repository: fakeRepository,
            locationHandler: locationHandler,
            onAnimalSelected: { _ in }  // Not relevant for location tests
        )
    }
    
    // MARK: - T019: loadAnnouncements fetches location when authorized
    
    func test_loadAnnouncements_whenLocationPermissionGranted_shouldFetchLocation() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(Coordinate(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnnouncements = [Announcement(
            id: "test",
            name: "Test",
            photoUrl: "test",
            coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
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
        await viewModel.loadAnnouncements()
        
        // Then
        let locationCalled = await fakeLocationService.requestLocationCalled
        XCTAssertTrue(locationCalled, "Should request location when permission granted")
    }
    
    // MARK: - T020: loadAnnouncements queries with coordinates when location available
    
    func test_loadAnnouncements_whenLocationAvailable_shouldQueryWithCoordinates() async {
        // Given
        let expectedLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(expectedLocation)
        fakeRepository.stubbedAnnouncements = [Announcement(
            id: "test",
            name: "Test",
            photoUrl: "test",
            coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
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
        
        // When - loadAnnouncements triggers async load in child VM
        await viewModel.loadAnnouncements()
        // Wait for child VM's async load to complete
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms
        
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
    
    // MARK: - T021: loadAnnouncements queries without coordinates when location fetch fails
    
    func test_loadAnnouncements_whenLocationFetchFails_shouldQueryWithoutCoordinates() async {
        // Given
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(nil) // Simulate location fetch failure
        fakeRepository.stubbedAnnouncements = [Announcement(
            id: "test",
            name: "Test",
            photoUrl: "test",
            coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
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
        await viewModel.loadAnnouncements()
        
        // Then
        let passedLocation = fakeRepository.lastLocationParameter
        XCTAssertNil(passedLocation, "Should pass nil location when fetch fails")
    }
    
    func test_loadAnnouncements_whenPermissionDenied_shouldNotFetchLocation() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.denied)
        fakeRepository.stubbedAnnouncements = [Announcement(
            id: "test",
            name: "Test",
            photoUrl: "test",
            coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
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
        await viewModel.loadAnnouncements()
        
        // Then
        let locationCalled = await fakeLocationService.requestLocationCalled
        XCTAssertFalse(locationCalled, "Should not request location when permission denied")
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without location when denied")
    }
    
    func test_loadAnnouncements_whenPermissionNotDetermined_shouldQueryWithoutLocation() async {
        // Given
        await setLocationServiceStatus(.notDetermined)
        await setLocationServiceLocation(nil)
        fakeRepository.stubbedAnnouncements = [Announcement(
            id: "test",
            name: "Test",
            photoUrl: "test",
            coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
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
        await viewModel.loadAnnouncements()
        
        // Then
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without location when status notDetermined")
    }
    
    // MARK: - User Story 2 Tests: First-Time Permission Request
    
    // T033: Unit test LocationService.requestWhenInUseAuthorization with notDetermined status
    func test_loadAnnouncements_whenPermissionNotDetermined_shouldRequestAuthorization() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.notDetermined)
        await fakeLocationService.setAuthorizationAfterRequest(.authorizedWhenInUse)
        fakeRepository.stubbedAnnouncements = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnnouncements()
        
        // Then
        let requestCalled = await fakeLocationService.requestAuthorizationCalled
        XCTAssertTrue(requestCalled, "Should request authorization when status is notDetermined")
    }
    
    // T034: Unit test LocationService.requestWhenInUseAuthorization returns immediately when already authorized
    func test_loadAnnouncements_whenAlreadyAuthorized_shouldNotRequestAuthorizationAgain() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.authorizedWhenInUse)
        await setLocationServiceLocation(Coordinate(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnnouncements = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnnouncements()
        
        // Then
        let requestCalled = await fakeLocationService.requestAuthorizationCalled
        XCTAssertFalse(requestCalled, "Should not request authorization when already authorized")
    }
    
    // T035: Unit test LocationService.requestWhenInUseAuthorization returns immediately when already denied
    func test_loadAnnouncements_whenAlreadyDenied_shouldNotRequestAuthorization() async {
        // Given
        await fakeLocationService.reset()
        await setLocationServiceStatus(.denied)
        fakeRepository.stubbedAnnouncements = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnnouncements()
        
        // Then
        let requestCalled = await fakeLocationService.requestAuthorizationCalled
        XCTAssertFalse(requestCalled, "Should not request authorization when already denied")
    }
    
    // T036: Unit test AnnouncementListViewModel.loadAnnouncements requests permission when notDetermined
    func test_loadAnnouncements_whenNotDetermined_shouldUpdatePermissionStatusAfterRequest() async {
        // Given
        await setLocationServiceStatus(.notDetermined)
        await fakeLocationService.setAuthorizationAfterRequest(.authorizedWhenInUse)
        await setLocationServiceLocation(Coordinate(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnnouncements = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnnouncements()
        
        // Then
        XCTAssertEqual(viewModel.locationPermissionStatus, .authorizedWhenInUse, "Should update permission status after user grants permission")
    }
    
    // T037: Unit test AnnouncementListViewModel handles user granting permission in alert
    func test_loadAnnouncements_whenUserGrantsPermission_shouldFetchLocationAndQueryWithCoordinates() async {
        // Given
        await setLocationServiceStatus(.notDetermined)
        await fakeLocationService.setAuthorizationAfterRequest(.authorizedWhenInUse)
        let expectedLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        await setLocationServiceLocation(expectedLocation)
        fakeRepository.stubbedAnnouncements = []
        
        viewModel = createViewModel()
        
        // When - loadAnnouncements triggers async load in child VM
        await viewModel.loadAnnouncements()
        // Wait for child VM's async load to complete
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms
        
        // Then
        XCTAssertEqual(viewModel.locationPermissionStatus, .authorizedWhenInUse, "Should update status to authorized")
        XCTAssertNotNil(viewModel.currentLocation, "Should fetch location after permission granted")
        let passedLocation = fakeRepository.lastLocationParameter
        XCTAssertNotNil(passedLocation, "Should query with coordinates after permission granted")
    }
    
    // T038: Unit test AnnouncementListViewModel handles user denying permission in alert
    func test_loadAnnouncements_whenUserDeniesPermission_shouldQueryWithoutCoordinates() async {
        // Given
        await setLocationServiceStatus(.notDetermined)
        await fakeLocationService.setAuthorizationAfterRequest(.denied)
        fakeRepository.stubbedAnnouncements = []
        
        viewModel = createViewModel()
        
        // When
        await viewModel.loadAnnouncements()
        
        // Then
        XCTAssertEqual(viewModel.locationPermissionStatus, .denied, "Should update status to denied")
        XCTAssertNil(viewModel.currentLocation, "Should not have location when permission denied")
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without coordinates when permission denied")
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
    
    private func setLocationServiceLocation(_ location: Coordinate?) async {
        await fakeLocationService.setLocation(location)
    }
}

// MARK: - FakeLocationService Extensions

extension FakeLocationService {
    func setStatus(_ status: LocationPermissionStatus) {
        stubbedAuthorizationStatus = status
    }
    
    func setLocation(_ location: Coordinate?) {
        stubbedLocation = location
    }
    
    func setAuthorizationAfterRequest(_ status: LocationPermissionStatus) {
        stubbedAuthorizationAfterRequest = status
    }
}

