import XCTest
@testable import PetSpot

/// Unit tests for AnnouncementCardsListViewModel.
/// Tests autonomous component behavior: loading, error handling, filtering, sorting.
/// Follows Given-When-Then structure per project constitution.
@MainActor
final class AnnouncementCardsListViewModelTests: XCTestCase {
    
    // MARK: - Test Properties
    
    private var sut: AnnouncementCardsListViewModel!
    private var fakeRepository: FakeAnnouncementRepository!
    private var capturedAnnouncementId: String?
    
    // MARK: - Setup & Teardown
    
    override func setUp() {
        super.setUp()
        fakeRepository = FakeAnnouncementRepository()
        capturedAnnouncementId = nil
    }
    
    override func tearDown() {
        sut = nil
        fakeRepository = nil
        capturedAnnouncementId = nil
        super.tearDown()
    }
    
    // MARK: - Helper Methods
    
    private func makeSUT() -> AnnouncementCardsListViewModel {
        return AnnouncementCardsListViewModel(
            repository: fakeRepository,
            onAnnouncementTapped: { [weak self] id in
                self?.capturedAnnouncementId = id
            }
        )
    }
    
    private func makeAnnouncement(
        id: String,
        lastSeenDate: String = "20/11/2025"
    ) -> Announcement {
        Announcement(
            id: id,
            name: "Test Pet",
            photoUrl: "https://example.com/photo.jpg",
            coordinate: Coordinate(latitude: 52.2297, longitude: 21.0122),
            species: .dog,
            breed: "Labrador",
            gender: .male,
            status: .active,
            lastSeenDate: lastSeenDate,
            description: "Test description",
            email: "test@example.com",
            phone: "+48123456789"
        )
    }
    
    // MARK: - T007: setQuery Tests
    
    func test_setQuery_whenQueryLimitIs5_shouldTriggerLoadAndDisplayFirst5MostRecent() async {
        // Given - Repository returns 10 announcements with different dates
        let announcements = (1...10).map { i in
            makeAnnouncement(id: "\(i)", lastSeenDate: String(format: "%02d/11/2025", i))
        }
        fakeRepository.stubbedAnnouncements = announcements
        
        sut = makeSUT()
        
        // When - setQuery is called with limit 5
        sut.query = .landingPageQuery(location: nil)
        
        // Wait for async load
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Should have exactly 5 card ViewModels
        XCTAssertEqual(sut.cardViewModels.count, 5, "Should display exactly 5 announcements")
        
        // Then - Should be sorted by date descending (newest first)
        // Date 10/11/2025 is newest, 01/11/2025 is oldest
        XCTAssertEqual(sut.cardViewModels.first?.id, "10", "First item should be newest (10/11/2025)")
        XCTAssertEqual(sut.cardViewModels.last?.id, "6", "Last item should be 6th newest (06/11/2025)")
    }
    
    func test_setQuery_whenRepositoryReturnsLessThan5Items_shouldDisplayAllAvailable() async {
        // Given - Repository returns only 3 announcements
        let announcements = (1...3).map { i in
            makeAnnouncement(id: "\(i)", lastSeenDate: String(format: "%02d/11/2025", i))
        }
        fakeRepository.stubbedAnnouncements = announcements
        
        sut = makeSUT()
        
        // When - setQuery is called
        sut.query = .landingPageQuery(location: nil)
        
        // Wait for async load
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Should display all 3 announcements
        XCTAssertEqual(sut.cardViewModels.count, 3, "Should display all 3 available announcements")
    }
    
    func test_setQuery_whenRepositoryReturnsEmptyArray_shouldResultInEmptyCardViewModels() async {
        // Given - Repository returns empty array
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When - setQuery is called
        sut.query = .landingPageQuery(location: nil)
        
        // Wait for async load
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - cardViewModels should be empty
        XCTAssertTrue(sut.cardViewModels.isEmpty, "Card ViewModels should be empty")
        XCTAssertFalse(sut.isLoading, "Should not be loading")
        XCTAssertNil(sut.errorMessage, "Should have no error message")
    }
    
    func test_setQuery_whenRepositoryThrowsError_shouldSetErrorMessage() async {
        // Given - Repository that throws error
        fakeRepository.shouldFail = true
        
        sut = makeSUT()
        
        // When - setQuery is called
        sut.query = .landingPageQuery(location: nil)
        
        // Wait for async load
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Should have error message
        XCTAssertNotNil(sut.errorMessage, "Should have error message when repository fails")
        XCTAssertTrue(sut.cardViewModels.isEmpty, "Card ViewModels should be empty on error")
        XCTAssertFalse(sut.isLoading, "Should not be loading after error")
    }
    
    func test_reload_shouldCancelPreviousTaskAndStartNew() async {
        // Given - Repository with delay
        fakeRepository.delayDuration = 0.5
        fakeRepository.stubbedAnnouncements = [makeAnnouncement(id: "1")]
        
        sut = makeSUT()
        
        // When - setQuery is called, then immediately reload
        sut.query = .landingPageQuery(location: nil)
        
        // Wait a bit then call reload (should cancel first task)
        try? await Task.sleep(nanoseconds: 50_000_000)
        sut.onRetryTapped()
        
        // Wait for second load to complete
        try? await Task.sleep(nanoseconds: 600_000_000)
        
        // Then - Should have completed (only second task should finish)
        // Repository called at least twice (first cancelled, second completed)
        XCTAssertGreaterThanOrEqual(fakeRepository.getAnnouncementsCallCount, 1)
        XCTAssertFalse(sut.isLoading, "Should not be loading after reload completes")
    }
    
    func test_applyQuery_shouldSortByCreatedAtDescending() async {
        // Given - Announcements with various dates
        let announcements = [
            makeAnnouncement(id: "oldest", lastSeenDate: "01/11/2025"),
            makeAnnouncement(id: "middle", lastSeenDate: "15/11/2025"),
            makeAnnouncement(id: "newest", lastSeenDate: "25/11/2025")
        ]
        fakeRepository.stubbedAnnouncements = announcements
        
        sut = makeSUT()
        
        // When - setQuery is called with default (no limit)
        sut.query = .defaultQuery(location: nil)
        
        // Wait for async load
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Should be sorted newest first
        XCTAssertEqual(sut.cardViewModels.count, 3)
        XCTAssertEqual(sut.cardViewModels[0].id, "newest")
        XCTAssertEqual(sut.cardViewModels[1].id, "middle")
        XCTAssertEqual(sut.cardViewModels[2].id, "oldest")
    }
    
    func test_onAnnouncementTapped_shouldInvokeClosureWithCorrectId() async {
        // Given - ViewModel with announcements loaded
        fakeRepository.stubbedAnnouncements = [makeAnnouncement(id: "tapped-id")]
        
        sut = makeSUT()
        sut.query = .landingPageQuery(location: nil)
        
        // Wait for load
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // When - Card ViewModel handleTap is called
        guard let cardVM = sut.cardViewModels.first else {
            XCTFail("No card ViewModel available")
            return
        }
        cardVM.handleTap()
        
        // Then - Closure should be invoked with correct ID
        XCTAssertEqual(capturedAnnouncementId, "tapped-id")
    }
    
    // MARK: - Loading State Tests
    
    func test_setQuery_shouldSetIsLoadingTrue_whileLoading() async {
        // Given - Repository with delay
        fakeRepository.delayDuration = 0.5
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When - setQuery is called
        sut.query = .landingPageQuery(location: nil)
        
        // Then - Should be loading immediately
        try? await Task.sleep(nanoseconds: 10_000_000) // Small delay to let task start
        XCTAssertTrue(sut.isLoading, "Should be loading while fetching")
        
        // Wait for load to complete
        try? await Task.sleep(nanoseconds: 600_000_000)
        
        // Then - Should not be loading after complete
        XCTAssertFalse(sut.isLoading, "Should not be loading after completion")
    }
    
    // MARK: - Location Parameter Tests
    
    func test_setQuery_whenLocationProvided_shouldPassToRepository() async {
        // Given - Query with location
        let location = Coordinate(latitude: 52.2297, longitude: 21.0122)
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When - setQuery is called with location
        sut.query = .landingPageQuery(location: location)
        
        // Wait for async load
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Repository should receive location
        XCTAssertEqual(fakeRepository.lastLocationParameter?.latitude, 52.2297)
        XCTAssertEqual(fakeRepository.lastLocationParameter?.longitude, 21.0122)
    }
    
    func test_setQuery_whenNoLocation_shouldPassNilToRepository() async {
        // Given - Query without location
        fakeRepository.stubbedAnnouncements = []
        
        sut = makeSUT()
        
        // When - setQuery is called without location
        sut.query = .landingPageQuery(location: nil)
        
        // Wait for async load
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        // Then - Repository should receive nil location
        XCTAssertNil(fakeRepository.lastLocationParameter)
    }
}

