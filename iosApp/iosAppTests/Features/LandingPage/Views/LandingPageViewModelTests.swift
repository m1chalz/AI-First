import XCTest
@testable import PetSpot

/// Unit tests for LandingPageViewModel.
/// Tests parent ViewModel behavior: initialization, location handling, list delegation,
/// and permission popup logic (User Story 3).
/// Follows Given-When-Then structure per project constitution.
@MainActor
final class LandingPageViewModelTests: XCTestCase {
    
    // MARK: - Test Properties
    
    private var sut: LandingPageViewModel!
    private var fakeRepository: FakeAnnouncementRepository!
    private var fakeLocationService: FakeLocationService!
    private var fakeLocationHandler: LocationPermissionHandler!
    private var capturedAnnouncementId: String?
    
    // MARK: - Setup & Teardown
    
    override func setUp() {
        super.setUp()
        fakeRepository = FakeAnnouncementRepository()
        fakeLocationService = FakeLocationService()
        fakeLocationHandler = LocationPermissionHandler(
            locationService: fakeLocationService,
            notificationCenter: NotificationCenter()
        )
        capturedAnnouncementId = nil
    }
    
    override func tearDown() {
        sut = nil
        fakeRepository = nil
        fakeLocationService = nil
        fakeLocationHandler = nil
        capturedAnnouncementId = nil
        super.tearDown()
    }
    
    // MARK: - Helper Methods
    
    private func makeSUT() -> LandingPageViewModel {
        return LandingPageViewModel(
            repository: fakeRepository,
            locationHandler: fakeLocationHandler,
            onAnnouncementTapped: { [weak self] id in
                self?.capturedAnnouncementId = id
            }
        )
    }
    
    // MARK: - T008: Initialization Tests
    
    func test_init_shouldCreateListViewModelWithLandingPageQuery() {
        // Given & When - ViewModel is created
        sut = makeSUT()
        
        // Then - listViewModel should exist
        XCTAssertNotNil(sut.listViewModel, "listViewModel should be created")
        
        // Note: Query configuration is private, but we can verify behavior through loading
    }
    
    func test_init_listViewModel_shouldBeAccessibleFromParent() {
        // Given & When - ViewModel is created
        sut = makeSUT()
        
        // Then - listViewModel is accessible and functional
        let listVM = sut.listViewModel
        XCTAssertNotNil(listVM)
        XCTAssertTrue(listVM.cardViewModels.isEmpty, "Should start with empty cards")
        XCTAssertFalse(listVM.isLoading, "Should not be loading initially")
    }
    
    // MARK: - T008: loadData Tests
    
    func test_loadData_shouldFetchLocationAndSetQueryOnListViewModel() async {
        // Given - Location service returns location
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(Coordinate(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When - loadData is called
        await sut.loadData()
        
        // Wait for list ViewModel to process
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Location service should be called
        let requestLocationCalled = await fakeLocationService.requestLocationCalled
        XCTAssertTrue(requestLocationCalled, "Location should be requested")
        
        // Then - Repository should receive location (via setQuery)
        XCTAssertEqual(fakeRepository.lastLocationParameter?.latitude, 52.2297)
        XCTAssertEqual(fakeRepository.lastLocationParameter?.longitude, 21.0122)
    }
    
    func test_loadData_whenLocationDenied_shouldSetQueryWithNilLocation() async {
        // Given - Location service returns denied status
        await fakeLocationService.setStubbedAuthorizationStatus(.denied)
        await fakeLocationService.setStubbedLocation(nil)
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When - loadData is called
        await sut.loadData()
        
        // Wait for list ViewModel to process
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Repository should receive nil location
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should pass nil location when denied")
    }
    
    func test_loadData_whenNotDetermined_shouldRequestPermissionsAndGetLocation() async {
        // Given - Location service returns notDetermined, then authorized after request
        await fakeLocationService.setStubbedAuthorizationStatus(.notDetermined)
        await fakeLocationService.setStubbedAuthorizationAfterRequest(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(Coordinate(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When - loadData is called
        await sut.loadData()
        
        // Wait for list ViewModel to process
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Permission should be requested
        let authCalled = await fakeLocationService.requestAuthorizationCalled
        XCTAssertTrue(authCalled, "Should request authorization when notDetermined")
    }
    
    // MARK: - T008: Callback Chain Tests
    
    func test_onAnnouncementTapped_shouldPropagateToParentClosure() async {
        // Given - ViewModel with loaded announcements
        fakeRepository.stubbedAnnouncements = [
            makeTestAnnouncement(id: "test-announcement-123")
        ]
        await fakeLocationService.setStubbedAuthorizationStatus(.denied)
        await fakeLocationService.setStubbedLocation(nil)
        
        sut = makeSUT()
        await sut.loadData()
        
        // Wait for load
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // When - Card is tapped
        if let cardVM = sut.listViewModel.cardViewModels.first {
            cardVM.handleTap()
        }
        
        // Then - Parent closure should receive the ID
        XCTAssertEqual(capturedAnnouncementId, "test-announcement-123")
    }
    
    // MARK: - User Story 3 Tests: Permission Popup (Recovery Path)
    
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
    
    // T047: Unit test LandingPageViewModel shows custom popup for denied status
    func test_loadData_whenPermissionDenied_shouldShowCustomPopup() async {
        // Given
        await fakeLocationService.setStubbedAuthorizationStatus(.denied)
        await fakeLocationService.setStubbedLocation(nil)
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Then
        XCTAssertTrue(sut.showPermissionDeniedAlert, "Should show custom popup when permission denied")
    }
    
    // T048: Unit test LandingPageViewModel shows custom popup for restricted status
    func test_loadData_whenPermissionRestricted_shouldShowCustomPopup() async {
        // Given
        await fakeLocationService.setStubbedAuthorizationStatus(.restricted)
        await fakeLocationService.setStubbedLocation(nil)
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Then
        XCTAssertTrue(sut.showPermissionDeniedAlert, "Should show custom popup when permission restricted")
    }
    
    // T049: Unit test LandingPageViewModel.hasShownPermissionAlert prevents repeated popups in session
    func test_loadData_whenPopupAlreadyShown_shouldNotShowAgain() async {
        // Given
        await fakeLocationService.setStubbedAuthorizationStatus(.denied)
        await fakeLocationService.setStubbedLocation(nil)
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When - First call
        await sut.loadData()
        let firstCallShown = sut.showPermissionDeniedAlert
        
        // Reset alert state but keep session flag
        sut.showPermissionDeniedAlert = false
        
        // Second call
        await sut.loadData()
        let secondCallShown = sut.showPermissionDeniedAlert
        
        // Then
        XCTAssertTrue(firstCallShown, "Should show popup on first call")
        XCTAssertFalse(secondCallShown, "Should not show popup again in same session")
    }
    
    // T050: Unit test LandingPageViewModel.openSettings() calls coordinator callback
    func test_openSettings_whenCalled_shouldInvokeCoordinatorCallback() async {
        // Given
        var callbackInvoked = false
        sut = makeSUT()
        sut.onOpenAppSettings = {
            callbackInvoked = true
        }
        
        // When
        sut.openSettings()
        
        // Then
        XCTAssertTrue(callbackInvoked, "Should invoke coordinator callback when openSettings called")
    }
    
    // T051: Unit test LandingPageViewModel.continueWithoutLocation() triggers query without coordinates
    func test_continueWithoutLocation_whenCalled_shouldTriggerQueryWithoutLocation() async {
        // Given
        fakeRepository.stubbedAnnouncements = []
        await fakeLocationService.setStubbedAuthorizationStatus(.denied)
        await fakeLocationService.setStubbedLocation(nil)
        
        sut = makeSUT()
        
        // When - continueWithoutLocation sets query on child VM which triggers async load
        sut.continueWithoutLocation()
        
        // Wait for async operation to complete
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then
        XCTAssertNil(fakeRepository.lastLocationParameter, "Should query without location when continueWithoutLocation called")
        XCTAssertFalse(sut.showPermissionDeniedAlert, "Should dismiss popup")
    }
    
    func test_loadData_whenPermissionAuthorized_shouldNotShowPopup() async {
        // Given
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(Coordinate(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Then
        XCTAssertFalse(sut.showPermissionDeniedAlert, "Should not show popup when authorized")
    }
    
    func test_loadData_shouldUpdateLocationPermissionStatus() async {
        // Given
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(Coordinate(latitude: 52.2297, longitude: 21.0122))
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Then
        XCTAssertEqual(sut.locationPermissionStatus, .authorizedWhenInUse)
    }
    
    func test_loadData_shouldUpdateCurrentLocation() async {
        // Given
        let expectedLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(expectedLocation)
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Then
        XCTAssertNotNil(sut.currentLocation)
        XCTAssertEqual(sut.currentLocation?.latitude, expectedLocation.latitude)
        XCTAssertEqual(sut.currentLocation?.longitude, expectedLocation.longitude)
    }
    
    // MARK: - Map Preview Tests (T013-T015a)
    
    // T013: loadData with authorized location should set .map model
    func test_loadData_whenLocationAuthorized_shouldSetMapPreviewModelWithCorrectRegion() async {
        // Given
        let expectedLocation = Coordinate(latitude: 52.23, longitude: 21.01)
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(expectedLocation)
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Then
        if case .map(let region, _, _) = sut.mapPreviewModel {
            XCTAssertEqual(region.center.latitude, 52.23, accuracy: 0.01)
            XCTAssertEqual(region.center.longitude, 21.01, accuracy: 0.01)
        } else {
            XCTFail("Expected .map state, got \(sut.mapPreviewModel)")
        }
    }
    
    // T014: loadData with denied location should set .permissionRequired model
    func test_loadData_whenLocationDenied_shouldSetPermissionRequiredModel() async {
        // Given
        await fakeLocationService.setStubbedAuthorizationStatus(.denied)
        await fakeLocationService.setStubbedLocation(nil)
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Then
        if case .permissionRequired(let message, _) = sut.mapPreviewModel {
            XCTAssertEqual(message, L10n.MapPreview.Permission.message)
        } else {
            XCTFail("Expected .permissionRequired state, got \(sut.mapPreviewModel)")
        }
    }
    
    // T015: handleMapTap should be callable (manual verification via console output)
    func test_mapTap_whenMapModelSet_shouldHaveCallableOnTapClosure() async {
        // Given
        let expectedLocation = Coordinate(latitude: 52.23, longitude: 21.01)
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(expectedLocation)
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        await sut.loadData()
        
        // When - extract and call onTap (should not crash, logs to console)
        if case .map(_, _, let onTap) = sut.mapPreviewModel {
            // Then - onTap closure should be callable without crash
            onTap()  // Should print "[LandingPage] Map preview tapped"
        } else {
            XCTFail("Expected .map state with onTap closure")
        }
    }
    
    // T015a: Map component should never call requestWhenInUseAuthorization
    func test_loadData_whenMapPreviewSetup_shouldNotRequestPermissionAgain() async {
        // Given - already authorized
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(Coordinate(latitude: 52.23, longitude: 21.01))
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // Reset the flag after initial setup (LocationPermissionHandler may have checked status)
        await fakeLocationService.reset()
        
        // When - loadData is called (should use existing permissions, not request new ones)
        await sut.loadData()
        
        // Then - requestWhenInUseAuthorization should NOT be called when already authorized
        let requestAuthCalled = await fakeLocationService.requestAuthorizationCalled
        XCTAssertFalse(requestAuthCalled, "Map preview should not trigger permission request when already authorized")
    }
    
    func test_mapPreviewModel_initialState_shouldBeLoading() {
        // Given & When
        sut = makeSUT()
        
        // Then
        if case .loading = sut.mapPreviewModel {
            // Expected
        } else {
            XCTFail("Initial mapPreviewModel should be .loading, got \(sut.mapPreviewModel)")
        }
    }
    
    func test_permissionRequired_whenGoToSettingsTapped_shouldCallOpenSettings() async {
        // Given
        await fakeLocationService.setStubbedAuthorizationStatus(.denied)
        await fakeLocationService.setStubbedLocation(nil)
        fakeRepository.stubbedAnnouncements = []
        
        var openSettingsCalled = false
        sut = makeSUT()
        sut.onOpenAppSettings = {
            openSettingsCalled = true
        }
        
        await sut.loadData()
        
        // When
        if case .permissionRequired(_, let onGoToSettings) = sut.mapPreviewModel {
            onGoToSettings()
        }
        
        // Then
        XCTAssertTrue(openSettingsCalled, "onGoToSettings should trigger openSettings callback")
    }
    
    // MARK: - T006-T008: Map Preview Pin Tests
    
    // T006: LandingPageViewModel should create pins from listViewModel.cardViewModels
    func test_loadData_whenAnnouncementsLoaded_shouldCreatePinsFromCardViewModels() async {
        // Given - Repository returns announcements with coordinates
        let announcements = [
            makeTestAnnouncement(id: "pet-1", latitude: 52.23, longitude: 21.01),
            makeTestAnnouncement(id: "pet-2", latitude: 52.24, longitude: 21.02)
        ]
        fakeRepository.stubbedAnnouncements = announcements
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(Coordinate(latitude: 52.23, longitude: 21.01))
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Wait for list ViewModel to process
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - mapPreviewModel should be .map with pins
        if case .map(_, let pins, _) = sut.mapPreviewModel {
            XCTAssertEqual(pins.count, 2, "Should have 2 pins for 2 announcements")
            XCTAssertEqual(pins[0].id, "pet-1", "First pin should have first announcement ID")
            XCTAssertEqual(pins[1].id, "pet-2", "Second pin should have second announcement ID")
        } else {
            XCTFail("Expected .map state with pins, got \(sut.mapPreviewModel)")
        }
    }
    
    // T007: mapPreviewModel should contain pins when announcements loaded
    func test_loadData_whenAnnouncementsLoaded_shouldPassPinsToMapModel() async {
        // Given - Repository returns announcements
        let announcement = makeTestAnnouncement(id: "pet-123", latitude: 52.25, longitude: 21.05)
        fakeRepository.stubbedAnnouncements = [announcement]
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(Coordinate(latitude: 52.23, longitude: 21.01))
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Wait for list ViewModel to process
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Pin should have correct coordinate from announcement
        if case .map(_, let pins, _) = sut.mapPreviewModel {
            XCTAssertEqual(pins.count, 1, "Should have 1 pin")
            XCTAssertEqual(pins[0].coordinate.latitude, 52.25, accuracy: 0.01)
            XCTAssertEqual(pins[0].coordinate.longitude, 21.05, accuracy: 0.01)
        } else {
            XCTFail("Expected .map state with pins, got \(sut.mapPreviewModel)")
        }
    }
    
    // T008: Empty pins array when no announcements exist
    func test_loadData_whenNoAnnouncements_shouldHaveEmptyPinsArray() async {
        // Given - Repository returns empty array
        fakeRepository.stubbedAnnouncements = []
        await fakeLocationService.setStubbedAuthorizationStatus(.authorizedWhenInUse)
        await fakeLocationService.setStubbedLocation(Coordinate(latitude: 52.23, longitude: 21.01))
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Wait for list ViewModel to process
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - mapPreviewModel should be .map with empty pins
        if case .map(_, let pins, _) = sut.mapPreviewModel {
            XCTAssertTrue(pins.isEmpty, "Should have empty pins array when no announcements")
        } else {
            XCTFail("Expected .map state with empty pins, got \(sut.mapPreviewModel)")
        }
    }
    
    func test_loadData_whenPermissionDenied_shouldNotHavePins() async {
        // Given - Location denied, so map shows permission required state
        fakeRepository.stubbedAnnouncements = [makeTestAnnouncement(id: "pet-1")]
        await fakeLocationService.setStubbedAuthorizationStatus(.denied)
        await fakeLocationService.setStubbedLocation(nil)
        
        sut = makeSUT()
        
        // When
        await sut.loadData()
        
        // Wait for list ViewModel to process
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - mapPreviewModel should be .permissionRequired (no pins)
        if case .permissionRequired = sut.mapPreviewModel {
            // Expected - permission required state has no pins concept
        } else {
            XCTFail("Expected .permissionRequired state when location denied, got \(sut.mapPreviewModel)")
        }
    }
    
    // MARK: - Private Helpers
    
    private func makeTestAnnouncement(id: String) -> Announcement {
        makeTestAnnouncement(id: id, latitude: 52.2297, longitude: 21.0122)
    }
    
    private func makeTestAnnouncement(
        id: String,
        latitude: Double,
        longitude: Double
    ) -> Announcement {
        Announcement(
            id: id,
            name: "Test Pet",
            photoUrl: "https://example.com/photo.jpg",
            coordinate: Coordinate(latitude: latitude, longitude: longitude),
            species: .dog,
            breed: "Labrador",
            gender: .male,
            status: .active,
            lastSeenDate: "20/11/2025",
            description: "Test description",
            email: "test@example.com",
            phone: "+48123456789"
        )
    }
}

// MARK: - FakeLocationService Extensions

extension FakeLocationService {
    /// Sets stubbed authorization status (actor-isolated)
    func setStubbedAuthorizationStatus(_ status: LocationPermissionStatus) {
        self.stubbedAuthorizationStatus = status
    }
    
    /// Sets stubbed location (actor-isolated)
    func setStubbedLocation(_ location: Coordinate?) {
        self.stubbedLocation = location
    }
    
    /// Sets stubbed authorization status after request (actor-isolated)
    func setStubbedAuthorizationAfterRequest(_ status: LocationPermissionStatus) {
        self.stubbedAuthorizationAfterRequest = status
    }
}

