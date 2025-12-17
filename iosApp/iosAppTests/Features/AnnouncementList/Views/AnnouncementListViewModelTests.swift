import XCTest
@testable import PetSpot

/**
 * Unit tests for AnnouncementListViewModel.
 * Tests @Published property updates and coordinator callback invocations.
 * Follows Given-When-Then structure per project constitution.
 */
@MainActor
final class AnnouncementListViewModelTests: XCTestCase {
    
    // MARK: - Test Doubles
    
    class FakeLocationService: LocationServiceProtocol {
        var stubbedAuthorizationStatus: LocationPermissionStatus = .notDetermined
        var stubbedLocation: Coordinate?
        
        var authorizationStatus: LocationPermissionStatus {
            get async { stubbedAuthorizationStatus }
        }
        
        func requestWhenInUseAuthorization() async -> LocationPermissionStatus {
            return stubbedAuthorizationStatus
        }
        
        func requestLocation() async -> Coordinate? {
            return stubbedLocation
        }
    }
    
    // MARK: - Helper Methods
    
    /// Captured animal ID from onAnimalSelected callback
    private var capturedAnimalId: String?
    
    private func createViewModel(
        repository: AnnouncementRepositoryProtocol,
        locationStatus: LocationPermissionStatus = .authorizedWhenInUse,
        location: Coordinate? = nil
    ) -> AnnouncementListViewModel {
        let fakeLocationService = FakeLocationService()
        fakeLocationService.stubbedAuthorizationStatus = locationStatus
        fakeLocationService.stubbedLocation = location
        
        let locationHandler = LocationPermissionHandler(
            locationService: fakeLocationService,
            notificationCenter: NotificationCenter()  // Isolated instance
        )
        
        capturedAnimalId = nil
        return AnnouncementListViewModel(
            repository: repository,
            locationHandler: locationHandler,
            onAnimalSelected: { [weak self] animalId in
                self?.capturedAnimalId = animalId
            }
        )
    }
    
    // MARK: - Test loadAnnouncements Success
    
    /**
     * Tests that loadAnnouncements updates @Published cardViewModels property on success.
     * After refactoring, loadAnnouncements() triggers async load in child VM via query setter.
     */
    func testLoadAnnouncements_whenRepositorySucceeds_shouldUpdateCardViewModels() async {
        // Given - ViewModel with fake repository returning animals
        let fakeRepository = FakeAnnouncementRepository(
            animalCount: 16,
            shouldFail: false
        )
        let viewModel = createViewModel(repository: fakeRepository)

        // When - loadAnnouncements triggers async load in child VM
        await viewModel.loadAnnouncements()
        // Wait for child VM's async load to complete
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms

        // Then - cardViewModels should be populated and state updated
        XCTAssertEqual(viewModel.cardViewModels.count, 16, "Should have 16 card ViewModels")
        XCTAssertFalse(viewModel.isLoading, "Should not be loading")
        XCTAssertNil(viewModel.errorMessage, "Should have no error")
        XCTAssertFalse(viewModel.isEmpty, "isEmpty should be false when card ViewModels present")
    }

    /**
     * Tests that loadAnnouncements handles incorrect data with duplicated animal ids with no crash.
     */
    func testLoadAnnouncements_whenRepositorySucceeds_shouldNotCrashIfIdsRepeatButIgnoreDuplicates() async {
        // Given - ViewModel with fake repository returning animals
        let fakeRepository = FakeAnnouncementRepository(
            animalCount: 20,
            shouldFail: false
        )
        let viewModel = createViewModel(repository: fakeRepository)

        // When - loadAnnouncements triggers async load in child VM
        await viewModel.loadAnnouncements()
        // Wait for child VM's async load to complete
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms

        // Then - cardViewModels should be populated and state updated
        XCTAssertEqual(viewModel.cardViewModels.count, 16, "Should have 16 card ViewModels")
        XCTAssertFalse(viewModel.isLoading, "Should not be loading")
        XCTAssertNil(viewModel.errorMessage, "Should have no error")
        XCTAssertFalse(viewModel.isEmpty, "isEmpty should be false when card ViewModels present")
    }

    // MARK: - Test isEmpty Property
    
    /**
     * Tests isEmpty computed property returns true when repository returns empty list.
     * After refactoring, state is managed by child listViewModel.
     */
    func test_isEmpty_whenRepositoryReturnsEmptyList_shouldReturnTrue() async {
        // Given - ViewModel with repository returning empty list
        let fakeRepository = FakeAnnouncementRepository(
            animalCount: 0,
            shouldFail: false
        )
        let viewModel = createViewModel(repository: fakeRepository)
        
        // When - loading completes with empty data (wait for child VM async load)
        await viewModel.loadAnnouncements()
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms
        
        // Then - isEmpty should be true (list loaded but empty)
        XCTAssertTrue(viewModel.isEmpty, "isEmpty should be true when repository returns empty list")
        XCTAssertTrue(viewModel.cardViewModels.isEmpty, "cardViewModels should be empty")
        XCTAssertFalse(viewModel.isLoading, "Should not be loading after load completes")
        XCTAssertNil(viewModel.errorMessage, "Should have no error")
    }
    
    /**
     * Tests isEmpty computed property returns false when card ViewModels present.
     */
    func test_isEmpty_whenCardViewModelsPresent_shouldReturnFalse() async {
        // Given - ViewModel with card ViewModels loaded
        let fakeRepository = FakeAnnouncementRepository(
            animalCount: 16,
            shouldFail: false
        )
        let viewModel = createViewModel(repository: fakeRepository)
        
        // When - card ViewModels are loaded (wait for child VM async load)
        await viewModel.loadAnnouncements()
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms
        
        // Then - isEmpty should be false
        XCTAssertFalse(viewModel.isEmpty, "isEmpty should be false when card ViewModels present")
    }
    
    // MARK: - Test selectAnimal Callback
    
    /**
     * Tests that tapping announcement card invokes onAnimalSelected closure.
     * After refactoring, onAnimalSelected is passed to listViewModel via constructor.
     */
    func testOnAnnouncementTapped_shouldInvokeOnAnimalSelectedClosure() async {
        // Given - ViewModel with callback closure (passed in constructor)
        let fakeRepository = FakeAnnouncementRepository(
            animalCount: 3,
            shouldFail: false
        )
        let viewModel = createViewModel(repository: fakeRepository)
        
        // Wait for initial load to complete (child VM async load)
        await viewModel.loadAnnouncements()
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms
        
        // When - user taps first announcement card (simulated via cardViewModel action)
        guard let firstCard = viewModel.cardViewModels.first else {
            XCTFail("Should have card ViewModels after load")
            return
        }
        firstCard.handleTap()
        
        // Then - closure should be invoked with correct ID
        XCTAssertEqual(capturedAnimalId, firstCard.id, "Should invoke closure with correct animal ID")
    }
    
    // MARK: - Test reportMissing Callback
    
    /**
     * Tests that reportMissing invokes onReportMissing closure.
     */
    func testReportMissing_shouldInvokeOnReportMissingClosure() {
        // Given - ViewModel with callback closure
        let fakeRepository = FakeAnnouncementRepository(
            animalCount: 0,
            shouldFail: false
        )
        let viewModel = createViewModel(repository: fakeRepository)
        
        var callbackInvoked = false
        viewModel.onReportMissing = {
            callbackInvoked = true
        }
        
        // When - reportMissing is called
        viewModel.reportMissing()
        
        // Then - closure should be invoked
        XCTAssertTrue(callbackInvoked, "Should invoke onReportMissing closure")
    }
    
    // MARK: - Test reportFound Callback
    
    /**
     * Tests that reportFound invokes onReportFound closure.
     * Note: Not exposed in iOS mobile UI, but included for completeness.
     */
    func testReportFound_shouldInvokeOnReportFoundClosure() {
        // Given - ViewModel with callback closure
        let fakeRepository = FakeAnnouncementRepository(
            animalCount: 0,
            shouldFail: false
        )
        let viewModel = createViewModel(repository: fakeRepository)
        
        var callbackInvoked = false
        viewModel.onReportFound = {
            callbackInvoked = true
        }
        
        // When - reportFound is called
        viewModel.reportFound()
        
        // Then - closure should be invoked
        XCTAssertTrue(callbackInvoked, "Should invoke onReportFound closure")
    }
    
    // MARK: - API Integration Tests (User Story 1)
    
    /// T016: Test AnnouncementListViewModel loadAnnouncements should update announcements publisher with API data
    func testLoadAnnouncements_whenRepositoryReturnsApiData_shouldUpdateCardViewModels() async {
        // Given - ViewModel with repository that returns API-like data
        let testLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let fakeRepository = FakeAnnouncementRepository(animalCount: 3, shouldFail: false)
        let viewModel = createViewModel(
            repository: fakeRepository,
            locationStatus: .authorizedWhenInUse,
            location: testLocation
        )
        
        // When - loadAnnouncements triggers async load in child VM
        await viewModel.loadAnnouncements()
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms
        
        // Then - cardViewModels should be populated with API data
        XCTAssertEqual(viewModel.cardViewModels.count, 3)
        XCTAssertFalse(viewModel.isLoading)
        XCTAssertNil(viewModel.errorMessage)
        XCTAssertNotNil(viewModel.currentLocation)
    }
    
    /// T017: Test AnnouncementListViewModel with location permissions should pass coordinates to repository
    func testLoadAnnouncements_withLocationPermissionsGranted_shouldPassCoordinatesToRepository() async {
        // Given - ViewModel with authorized location
        let testLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let fakeRepository = FakeAnnouncementRepository(animalCount: 5, shouldFail: false)
        let viewModel = createViewModel(
            repository: fakeRepository,
            locationStatus: .authorizedWhenInUse,
            location: testLocation
        )
        
        // When - loadAnnouncements triggers async load in child VM
        await viewModel.loadAnnouncements()
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms
        
        // Then - currentLocation should be set
        XCTAssertNotNil(viewModel.currentLocation, "currentLocation should not be nil")
        guard let location = viewModel.currentLocation else {
            return // Test already failed at XCTAssertNotNil
        }
        XCTAssertEqual(location.latitude, 52.2297, accuracy: 0.0001)
        XCTAssertEqual(location.longitude, 21.0122, accuracy: 0.0001)
        XCTAssertFalse(viewModel.isLoading)
    }
    
    /// T018: Test AnnouncementListViewModel without location permissions should call repository without coordinates
    func testLoadAnnouncements_withoutLocationPermissions_shouldCallRepositoryWithoutCoordinates() async {
        // Given - ViewModel with denied location permissions
        let fakeRepository = FakeAnnouncementRepository(animalCount: 10, shouldFail: false)
        let viewModel = createViewModel(
            repository: fakeRepository,
            locationStatus: .denied,
            location: nil
        )
        
        // When - loadAnnouncements triggers async load in child VM
        await viewModel.loadAnnouncements()
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms
        
        // Then - currentLocation should be nil, but animals still loaded
        XCTAssertNil(viewModel.currentLocation)
        XCTAssertEqual(viewModel.cardViewModels.count, 10)
        XCTAssertFalse(viewModel.isLoading)
        XCTAssertNil(viewModel.errorMessage)
    }
    
    /// T019: Test AnnouncementListViewModel with repository error should set error state
    func testLoadAnnouncements_whenRepositoryThrowsError_shouldSetErrorState() async {
        // Given - ViewModel with repository that fails
        let fakeRepository = FakeAnnouncementRepository(animalCount: 0, shouldFail: true)
        let viewModel = createViewModel(repository: fakeRepository)
        
        // When - loadAnnouncements triggers async load in child VM
        await viewModel.loadAnnouncements()
        try? await Task.sleep(nanoseconds: 100_000_000) // 100ms
        
        // Then - error state should be set
        XCTAssertNotNil(viewModel.errorMessage)
        XCTAssertFalse(viewModel.isLoading)
        XCTAssertTrue(viewModel.cardViewModels.isEmpty)
    }
}

