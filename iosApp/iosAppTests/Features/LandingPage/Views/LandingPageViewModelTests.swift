import XCTest
@testable import PetSpot

/// Unit tests for LandingPageViewModel.
/// Tests parent ViewModel behavior: initialization, location handling, list delegation.
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
    
    // MARK: - Private Helpers
    
    private func makeTestAnnouncement(id: String) -> Announcement {
        Announcement(
            id: id,
            name: "Test Pet",
            photoUrl: "https://example.com/photo.jpg",
            coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
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

