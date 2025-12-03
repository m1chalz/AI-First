import XCTest
@testable import PetSpot

/**
 * Unit tests for AnimalCardViewModel.
 * Tests presentation layer properties and action callbacks.
 * Follows Given-When-Then structure per project constitution.
 */
@MainActor
final class AnimalCardViewModelTests: XCTestCase {
    
    // MARK: - Helper Methods
    
    /**
     * Creates a test Announcement with default values.
     */
    private func makeTestAnnouncement(
        id: String = "test-id-123",
        name: String = "Buddy",
        breed: String? = "Golden Retriever",
        latitude: Double = 52.2297,
        longitude: Double = 21.0122,
        species: AnimalSpecies = .dog,
        status: AnimalStatus = .active,
        lastSeenDate: String = "20/11/2024"
    ) -> Announcement {
        return Announcement(
            id: id,
            name: name,
            photoUrl: "https://example.com/photo.jpg",
            coordinate: Coordinate(latitude: latitude, longitude: longitude),
            species: species,
            breed: breed,
            gender: .male,
            status: status,
            lastSeenDate: lastSeenDate,
            description: "Test description",
            email: "test@example.com",
            phone: "+48123456789"
        )
    }
    /**
     * Tests that speciesName returns display name.
     */
    func test_speciesName_shouldReturnDisplayName() {
        // Given - ViewModel with dog species
        let announcement = makeTestAnnouncement(species: .dog)
        let viewModel = AnimalCardViewModel(announcement: announcement, onAction: { _ in })
        
        // When - accessing speciesName
        let speciesName = viewModel.speciesName
        
        // Then - should return species display name
        XCTAssertEqual(speciesName, AnimalSpecies.dog.displayName, "speciesName should return species displayName")
    }
    
    /**
     * Tests that breedName returns breed string.
     */
    func test_breedName_shouldReturnBreed() {
        // Given - ViewModel with Golden Retriever breed
        let announcement = makeTestAnnouncement(breed: "Golden Retriever")
        let viewModel = AnimalCardViewModel(announcement: announcement, onAction: { _ in })
        
        // When - accessing breedName
        let breedName = viewModel.breedName
        
        // Then - should return breed string
        XCTAssertEqual(breedName, "Golden Retriever", "breedName should return breed")
    }
    
    /**
     * Tests that statusText returns status display name.
     */
    func test_statusText_shouldReturnStatusDisplayName() {
        // Given - ViewModel with active status
        let announcement = makeTestAnnouncement(status: .active)
        let viewModel = AnimalCardViewModel(announcement: announcement, onAction: { _ in })
        
        // When - accessing statusText
        let statusText = viewModel.statusText
        
        // Then - should return status display name
        XCTAssertEqual(statusText, AnimalStatus.active.displayName, "statusText should return status displayName")
    }
    
    /**
     * Tests that statusColorHex returns badge color hex string.
     */
    func test_statusColorHex_shouldReturnBadgeColorHex() {
        // Given - ViewModel with active status
        let announcement = makeTestAnnouncement(status: .active)
        let viewModel = AnimalCardViewModel(announcement: announcement, onAction: { _ in })
        
        // When - accessing statusColorHex
        let colorHex = viewModel.statusColorHex
        
        // Then - should return hex color string
        XCTAssertEqual(colorHex, AnimalStatus.active.badgeColorHex, "statusColorHex should return badge color hex")
        XCTAssertTrue(colorHex.hasPrefix("#"), "statusColorHex should be hex format starting with #")
    }
    
    /**
     * Tests that dateText returns last seen date.
     */
    func test_dateText_shouldReturnLastSeenDate() {
        // Given - ViewModel with specific date
        let announcement = makeTestAnnouncement(lastSeenDate: "20/11/2024")
        let viewModel = AnimalCardViewModel(announcement: announcement, onAction: { _ in })
        
        // When - accessing dateText
        let dateText = viewModel.dateText
        
        // Then - should return date string
        XCTAssertEqual(dateText, "20/11/2024", "dateText should return lastSeenDate")
    }
    
    /**
     * Tests that id returns announcement ID.
     */
    func test_id_shouldReturnAnnouncementId() {
        // Given - ViewModel with specific announcement ID
        let announcement = makeTestAnnouncement(id: "test-announcement-456")
        let viewModel = AnimalCardViewModel(announcement: announcement, onAction: { _ in })
        
        // When - accessing id
        let id = viewModel.id
        
        // Then - should return announcement ID
        XCTAssertEqual(id, "test-announcement-456", "id should return announcement.id")
    }
    
    // MARK: - Test handleTap Action
    
    /**
     * Tests that handleTap invokes callback with selected action.
     */
    func test_handleTap_shouldInvokeCallbackWithSelectedAction() {
        // Given - ViewModel with action callback
        let announcement = makeTestAnnouncement(id: "tapped-announcement-789")
        var capturedAction: AnimalAction?
        let viewModel = AnimalCardViewModel(announcement: announcement) { action in
            capturedAction = action
        }
        
        // When - handleTap is called
        viewModel.handleTap()
        
        // Then - should invoke callback with .selected action containing announcement ID
        if case .selected(let announcementId) = capturedAction {
            XCTAssertEqual(announcementId, "tapped-announcement-789", "Should invoke callback with correct announcement ID")
        } else {
            XCTFail("Expected .selected action, got \(String(describing: capturedAction))")
        }
    }
    
    // MARK: - Test update Method
    
    /**
     * Tests that update changes announcement and updates computed properties.
     */
    func test_update_shouldUpdateAnnouncementAndProperties() {
        // Given - ViewModel with initial announcement
        let initialAnnouncement = makeTestAnnouncement(
            id: "same-id",
            breed: "Golden Retriever",
            latitude: 52.2297,
            longitude: 21.0122,
            status: .active
        )
        let viewModel = AnimalCardViewModel(announcement: initialAnnouncement, onAction: { _ in })
        
        // Verify initial state
        XCTAssertEqual(viewModel.breedName, "Golden Retriever")
        XCTAssertEqual(viewModel.locationText, "52.2297° N, 21.0122° E")
        XCTAssertEqual(viewModel.statusText, AnimalStatus.active.displayName)
        
        // When - update is called with modified announcement
        let updatedAnnouncement = makeTestAnnouncement(
            id: "same-id",
            breed: "Labrador",
            latitude: 50.0647,
            longitude: 19.9450,
            status: .found
        )
        viewModel.update(with: updatedAnnouncement)
        
        // Then - computed properties should reflect new values
        XCTAssertEqual(viewModel.breedName, "Labrador", "breedName should update")
        XCTAssertEqual(viewModel.locationText, "50.0647° N, 19.9450° E", "locationText should update")
        XCTAssertEqual(viewModel.statusText, AnimalStatus.found.displayName, "statusText should update")
    }
    
    /**
     * Tests that isExpanded state is independent of announcement updates.
     */
    func test_isExpanded_shouldRemainIndependentAfterUpdate() {
        // Given - ViewModel with isExpanded set to true
        let initialAnnouncement = makeTestAnnouncement(id: "test-id")
        let viewModel = AnimalCardViewModel(announcement: initialAnnouncement, onAction: { _ in })
        viewModel.isExpanded = true
        
        // When - announcement is updated
        let updatedAnnouncement = makeTestAnnouncement(id: "test-id", breed: "New Breed")
        viewModel.update(with: updatedAnnouncement)
        
        // Then - isExpanded should remain true (local state preserved)
        XCTAssertTrue(viewModel.isExpanded, "isExpanded state should be preserved after update")
    }
}

