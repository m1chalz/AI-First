import XCTest
import MapKit
@testable import PetSpot

/// Unit tests for FullscreenMapViewModel.
/// Tests map region centering, legend configuration, and pin loading.
/// Follows Given-When-Then structure per project constitution.
@MainActor
final class FullscreenMapViewModelTests: XCTestCase {
    
    // MARK: - Test Properties
    
    private var sut: FullscreenMapViewModel!
    private var fakeRepository: FakeAnnouncementRepository!
    
    // MARK: - Setup & Teardown
    
    override func setUp() {
        super.setUp()
        fakeRepository = FakeAnnouncementRepository()
    }
    
    override func tearDown() {
        sut = nil
        fakeRepository = nil
        super.tearDown()
    }
    
    // MARK: - Map Region Centering Tests
    
    func testInit_withUserLocation_shouldSetMapRegionCenterFromLocation() {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122) // Warsaw
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // Then
        XCTAssertEqual(sut.mapRegion.center.latitude, 52.2297, accuracy: 0.001)
        XCTAssertEqual(sut.mapRegion.center.longitude, 21.0122, accuracy: 0.001)
    }
    
    func testInit_withUserLocation_shouldUseCityLevelZoom() {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // Then - ~20km span (10km radius * 2 = ~0.18 degrees latitude)
        XCTAssertEqual(sut.mapRegion.span.latitudeDelta, 0.18, accuracy: 0.05)
    }
    
    func testInit_withDifferentLocation_shouldCenterOnThatLocation() {
        // Given
        let krakowLocation = Coordinate(latitude: 50.0647, longitude: 19.9450) // Krakow
        
        // When
        sut = FullscreenMapViewModel(userLocation: krakowLocation, repository: fakeRepository)
        
        // Then
        XCTAssertEqual(sut.mapRegion.center.latitude, 50.0647, accuracy: 0.001)
        XCTAssertEqual(sut.mapRegion.center.longitude, 19.9450, accuracy: 0.001)
    }
    
    // MARK: - Legend Model Configuration Tests
    
    func testLegendModel_shouldHaveTwoLegendItems() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // Then
        XCTAssertEqual(sut.legendModel.legendItems.count, 2)
    }
    
    func testLegendModel_shouldHaveMissingAndFoundItems() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // Then
        XCTAssertEqual(sut.legendModel.legendItems[0].id, "missing")
        XCTAssertEqual(sut.legendModel.legendItems[1].id, "found")
    }
    
    func testLegendModel_shouldHaveCorrectColors() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // Then
        XCTAssertEqual(sut.legendModel.legendItems[0].colorHex, "#FF0000") // Red for missing
        XCTAssertEqual(sut.legendModel.legendItems[1].colorHex, "#0074FF") // Blue for found
    }
    
    func testLegendModel_shouldHaveNilTitle() {
        // Given - fullscreen map uses navigation bar for title
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // Then
        XCTAssertNil(sut.legendModel.title)
    }
    
    func testLegendModel_shouldHaveCorrectAccessibilityIdPrefix() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // Then
        XCTAssertEqual(sut.legendModel.legendAccessibilityIdPrefix, "fullscreenMap.legend")
    }
    
    // MARK: - T008: Pin Loading - isLoading State (US1)
    
    func testLoadPins_whenViewAppears_shouldSetIsLoadingTrue() async {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        fakeRepository.delayDuration = 0.5 // Slow response to observe loading state
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // When
        let loadTask = Task {
            await sut.loadPins()
        }
        
        // Allow task to start
        try? await Task.sleep(for: .milliseconds(50))
        
        // Then
        XCTAssertTrue(sut.isLoading)
        
        // Cleanup
        loadTask.cancel()
    }
    
    // MARK: - T009: Pin Loading - Mapping All Announcements (US1)
    
    func testLoadPins_whenRepositoryReturnsAnnouncements_shouldMapAllToMapPins() async {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let announcements = [
            Announcement(
                id: "1",
                name: "Buddy",
                photoUrl: "photo1.jpg",
                coordinate: Coordinate(latitude: 52.23, longitude: 21.01),
                species: .dog,
                breed: "Labrador",
                gender: .male,
                status: .active,
                lastSeenDate: "01/01/2025",
                description: nil,
                email: nil,
                phone: nil
            ),
            Announcement(
                id: "2",
                name: "Luna",
                photoUrl: "photo2.jpg",
                coordinate: Coordinate(latitude: 52.24, longitude: 21.02),
                species: .cat,
                breed: "Siamese",
                gender: .female,
                status: .found,
                lastSeenDate: "02/01/2025",
                description: nil,
                email: nil,
                phone: nil
            ),
            Announcement(
                id: "3",
                name: "Max",
                photoUrl: "photo3.jpg",
                coordinate: Coordinate(latitude: 52.25, longitude: 21.03),
                species: .dog,
                breed: "German Shepherd",
                gender: .male,
                status: .closed,
                lastSeenDate: "03/01/2025",
                description: nil,
                email: nil,
                phone: nil
            )
        ]
        fakeRepository.stubbedAnnouncements = announcements
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // When
        await sut.loadPins()
        
        // Then - ALL announcements mapped to pins (no status filtering)
        XCTAssertEqual(sut.pins.count, 3)
    }
    
    // MARK: - T010: Pin Loading - Correct Mapping (US1)
    
    func testLoadPins_whenRepositoryReturnsAnnouncements_shouldMapToMapPins() async {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let announcement = Announcement(
            id: "test-id-123",
            name: "Buddy",
            photoUrl: "photo.jpg",
            coordinate: Coordinate(latitude: 52.2350, longitude: 21.0200),
            species: .dog,
            breed: "Labrador",
            gender: .male,
            status: .active,
            lastSeenDate: "01/01/2025",
            description: nil,
            email: nil,
            phone: nil
        )
        fakeRepository.stubbedAnnouncements = [announcement]
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // When
        await sut.loadPins()
        
        // Then
        XCTAssertEqual(sut.pins.count, 1)
        let pin = sut.pins[0]
        XCTAssertEqual(pin.id, "test-id-123")
        XCTAssertEqual(pin.coordinate.latitude, 52.2350, accuracy: 0.0001)
        XCTAssertEqual(pin.coordinate.longitude, 21.0200, accuracy: 0.0001)
        XCTAssertEqual(pin.species, .dog)
    }
    
    // MARK: - T011: Pin Loading - Empty Response (US1)
    
    func testLoadPins_whenRepositoryReturnsEmptyArray_shouldSetEmptyPins() async {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        fakeRepository.stubbedAnnouncements = []
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // When
        await sut.loadPins()
        
        // Then
        XCTAssertTrue(sut.pins.isEmpty)
    }
    
    // MARK: - T012: Pin Loading - Error Handling (US1)
    
    func testLoadPins_whenRepositoryThrowsError_shouldKeepExistingPins() async {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let initialAnnouncements = [
            Announcement(
                id: "existing-1",
                name: "Existing Pet",
                photoUrl: "photo.jpg",
                coordinate: Coordinate(latitude: 52.23, longitude: 21.01),
                species: .cat,
                breed: nil,
                gender: .female,
                status: .active,
                lastSeenDate: "01/01/2025",
                description: nil,
                email: nil,
                phone: nil
            )
        ]
        fakeRepository.stubbedAnnouncements = initialAnnouncements
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // Load initial pins
        await sut.loadPins()
        XCTAssertEqual(sut.pins.count, 1)
        
        // When - repository now fails
        fakeRepository.shouldFail = true
        await sut.loadPins()
        
        // Then - existing pins should remain
        XCTAssertEqual(sut.pins.count, 1)
        XCTAssertEqual(sut.pins[0].id, "existing-1")
    }
    
    // MARK: - T013: Pin Loading - isLoading Reset (US1)
    
    func testLoadPins_whenComplete_shouldSetIsLoadingFalse() async {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        fakeRepository.stubbedAnnouncements = []
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // When
        await sut.loadPins()
        
        // Then
        XCTAssertFalse(sut.isLoading)
    }
    
    // MARK: - T025: Region Change - Fetch New Pins (US2)
    
    func testHandleRegionChange_whenCalled_shouldFetchPinsForNewRegion() async {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let announcement = Announcement(
            id: "new-region-pin",
            name: "New Pet",
            photoUrl: "photo.jpg",
            coordinate: Coordinate(latitude: 50.0647, longitude: 19.9450),
            species: .cat,
            breed: nil,
            gender: .female,
            status: .active,
            lastSeenDate: "01/01/2025",
            description: nil,
            email: nil,
            phone: nil
        )
        fakeRepository.stubbedAnnouncements = [announcement]
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // When - simulate panning to Krakow
        let newRegion = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 50.0647, longitude: 19.9450),
            span: MKCoordinateSpan(latitudeDelta: 0.18, longitudeDelta: 0.18)
        )
        await sut.handleRegionChange(newRegion)
        
        // Then
        XCTAssertEqual(sut.pins.count, 1)
        XCTAssertEqual(sut.pins[0].id, "new-region-pin")
    }
    
    // MARK: - T026: Region Change - Task Cancellation (US2)
    
    func testHandleRegionChange_whenCalledRapidly_shouldCancelPreviousTask() async {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        fakeRepository.delayDuration = 0.5 // Slow response
        fakeRepository.stubbedAnnouncements = [
            Announcement(
                id: "slow-response",
                name: "Slow",
                photoUrl: "photo.jpg",
                coordinate: Coordinate(latitude: 52.23, longitude: 21.01),
                species: .dog,
                breed: nil,
                gender: .male,
                status: .active,
                lastSeenDate: "01/01/2025",
                description: nil,
                email: nil,
                phone: nil
            )
        ]
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // When - rapid region changes
        let region1 = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 50.0, longitude: 19.0),
            span: MKCoordinateSpan(latitudeDelta: 0.18, longitudeDelta: 0.18)
        )
        let region2 = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 51.0, longitude: 20.0),
            span: MKCoordinateSpan(latitudeDelta: 0.18, longitudeDelta: 0.18)
        )
        
        // Start first request (will be slow)
        let firstTask = Task {
            await sut.handleRegionChange(region1)
        }
        
        // Wait a bit then start second request
        try? await Task.sleep(for: .milliseconds(50))
        
        // Change to fast response for second request
        fakeRepository.delayDuration = 0
        fakeRepository.stubbedAnnouncements = [
            Announcement(
                id: "fast-response",
                name: "Fast",
                photoUrl: "photo.jpg",
                coordinate: Coordinate(latitude: 51.0, longitude: 20.0),
                species: .cat,
                breed: nil,
                gender: .female,
                status: .active,
                lastSeenDate: "01/01/2025",
                description: nil,
                email: nil,
                phone: nil
            )
        ]
        
        await sut.handleRegionChange(region2)
        firstTask.cancel()
        
        // Then - only second request's result should be present
        XCTAssertEqual(sut.pins.count, 1)
        XCTAssertEqual(sut.pins[0].id, "fast-response")
    }
    
    // MARK: - T051: Pin Selection - Toggle Behavior (KAN-32)
    
    func testSelectPin_whenSamePinAlreadySelected_shouldDeselectIt() {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        sut.selectPin("pin-A")
        XCTAssertEqual(sut.selectedPinId, "pin-A")
        
        // When - tap same pin again (FR-011)
        sut.selectPin("pin-A")
        
        // Then - should toggle off
        XCTAssertNil(sut.selectedPinId)
    }
    
    // MARK: - T052: Pin Selection - Replace Behavior (KAN-32)
    
    func testSelectPin_whenDifferentPinSelected_shouldReplaceSelection() {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        sut.selectPin("pin-A")
        XCTAssertEqual(sut.selectedPinId, "pin-A")
        
        // When - tap different pin (FR-012)
        sut.selectPin("pin-B")
        
        // Then - should replace with new pin
        XCTAssertEqual(sut.selectedPinId, "pin-B")
    }
    
    func testDeselectPin_whenPinSelected_shouldClearSelection() {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        sut.selectPin("pin-A")
        XCTAssertEqual(sut.selectedPinId, "pin-A")
        
        // When - tap on map background (FR-010)
        sut.deselectPin()
        
        // Then - should clear selection
        XCTAssertNil(sut.selectedPinId)
    }
    
    func testSelectPin_whenNoSelectionExists_shouldSelectNewPin() {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        XCTAssertNil(sut.selectedPinId)
        
        // When - tap pin (FR-001)
        sut.selectPin("pin-A")
        
        // Then - should select the pin
        XCTAssertEqual(sut.selectedPinId, "pin-A")
    }
    
    // MARK: - T027: Region Change - Error Handling (US2)
    
    func testHandleRegionChange_whenRepositoryFails_shouldKeepExistingPins() async {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let initialAnnouncement = Announcement(
            id: "existing-pin",
            name: "Existing",
            photoUrl: "photo.jpg",
            coordinate: Coordinate(latitude: 52.23, longitude: 21.01),
            species: .dog,
            breed: nil,
            gender: .male,
            status: .active,
            lastSeenDate: "01/01/2025",
            description: nil,
            email: nil,
            phone: nil
        )
        fakeRepository.stubbedAnnouncements = [initialAnnouncement]
        sut = FullscreenMapViewModel(userLocation: userLocation, repository: fakeRepository)
        
        // Load initial pins
        await sut.loadPins()
        XCTAssertEqual(sut.pins.count, 1)
        
        // When - repository fails on region change
        fakeRepository.shouldFail = true
        let newRegion = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 50.0, longitude: 19.0),
            span: MKCoordinateSpan(latitudeDelta: 0.18, longitudeDelta: 0.18)
        )
        await sut.handleRegionChange(newRegion)
        
        // Then - existing pins remain
        XCTAssertEqual(sut.pins.count, 1)
        XCTAssertEqual(sut.pins[0].id, "existing-pin")
    }
}
