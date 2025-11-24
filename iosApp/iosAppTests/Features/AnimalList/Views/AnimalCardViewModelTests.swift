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
     * Creates a test Animal with default values.
     */
    private func makeTestAnimal(
        id: String = "test-id-123",
        name: String = "Buddy",
        breed: String = "Golden Retriever",
        city: String = "Warsaw",
        radiusKm: Int32 = 5,
        species: AnimalSpecies = .dog,
        status: AnimalStatus = .active,
        lastSeenDate: String = "20/11/2024"
    ) -> Animal {
        return Animal(
            id: id,
            name: name,
            photoUrl: "https://example.com/photo.jpg",
            location: Location(city: city, radiusKm: radiusKm),
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
    
    // MARK: - Test Presentation Properties
    
    /**
     * Tests that locationText formats correctly.
     */
    func test_locationText_shouldFormatCityAndRadius() {
        // Given - ViewModel with animal in Warsaw, 5km radius
        let animal = makeTestAnimal(city: "Warsaw", radiusKm: 5)
        let viewModel = AnimalCardViewModel(animal: animal, onAction: { _ in })
        
        // When - accessing locationText
        let locationText = viewModel.locationText
        
        // Then - should format as "City, +XYZkm"
        XCTAssertEqual(locationText, "Warsaw, +5km", "locationText should format as 'City, +Xkm'")
    }
    
    /**
     * Tests that speciesName returns display name.
     */
    func test_speciesName_shouldReturnDisplayName() {
        // Given - ViewModel with dog species
        let animal = makeTestAnimal(species: .dog)
        let viewModel = AnimalCardViewModel(animal: animal, onAction: { _ in })
        
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
        let animal = makeTestAnimal(breed: "Golden Retriever")
        let viewModel = AnimalCardViewModel(animal: animal, onAction: { _ in })
        
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
        let animal = makeTestAnimal(status: .active)
        let viewModel = AnimalCardViewModel(animal: animal, onAction: { _ in })
        
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
        let animal = makeTestAnimal(status: .active)
        let viewModel = AnimalCardViewModel(animal: animal, onAction: { _ in })
        
        // When - accessing statusColorHex
        let colorHex = viewModel.statusColorHex
        
        // Then - should return hex color string
        XCTAssertEqual(colorHex, AnimalStatus.active.badgeColor, "statusColorHex should return badge color hex")
        XCTAssertTrue(colorHex.hasPrefix("#"), "statusColorHex should be hex format starting with #")
    }
    
    /**
     * Tests that dateText returns last seen date.
     */
    func test_dateText_shouldReturnLastSeenDate() {
        // Given - ViewModel with specific date
        let animal = makeTestAnimal(lastSeenDate: "20/11/2024")
        let viewModel = AnimalCardViewModel(animal: animal, onAction: { _ in })
        
        // When - accessing dateText
        let dateText = viewModel.dateText
        
        // Then - should return date string
        XCTAssertEqual(dateText, "20/11/2024", "dateText should return lastSeenDate")
    }
    
    /**
     * Tests that id returns animal ID.
     */
    func test_id_shouldReturnAnimalId() {
        // Given - ViewModel with specific animal ID
        let animal = makeTestAnimal(id: "test-animal-456")
        let viewModel = AnimalCardViewModel(animal: animal, onAction: { _ in })
        
        // When - accessing id
        let id = viewModel.id
        
        // Then - should return animal ID
        XCTAssertEqual(id, "test-animal-456", "id should return animal.id")
    }
    
    // MARK: - Test handleTap Action
    
    /**
     * Tests that handleTap invokes callback with selected action.
     */
    func test_handleTap_shouldInvokeCallbackWithSelectedAction() {
        // Given - ViewModel with action callback
        let animal = makeTestAnimal(id: "tapped-animal-789")
        var capturedAction: AnimalAction?
        let viewModel = AnimalCardViewModel(animal: animal) { action in
            capturedAction = action
        }
        
        // When - handleTap is called
        viewModel.handleTap()
        
        // Then - should invoke callback with .selected action containing animal ID
        if case .selected(let animalId) = capturedAction {
            XCTAssertEqual(animalId, "tapped-animal-789", "Should invoke callback with correct animal ID")
        } else {
            XCTFail("Expected .selected action, got \(String(describing: capturedAction))")
        }
    }
    
    // MARK: - Test update Method
    
    /**
     * Tests that update changes animal and updates computed properties.
     */
    func test_update_shouldUpdateAnimalAndProperties() {
        // Given - ViewModel with initial animal
        let initialAnimal = makeTestAnimal(
            id: "same-id",
            breed: "Golden Retriever",
            city: "Warsaw",
            status: .active
        )
        let viewModel = AnimalCardViewModel(animal: initialAnimal, onAction: { _ in })
        
        // Verify initial state
        XCTAssertEqual(viewModel.breedName, "Golden Retriever")
        XCTAssertEqual(viewModel.locationText, "Warsaw, +5km")
        XCTAssertEqual(viewModel.statusText, AnimalStatus.active.displayName)
        
        // When - update is called with modified animal
        let updatedAnimal = makeTestAnimal(
            id: "same-id",
            breed: "Labrador",
            city: "Krakow",
            radiusKm: 10,
            status: .found
        )
        viewModel.update(with: updatedAnimal)
        
        // Then - computed properties should reflect new values
        XCTAssertEqual(viewModel.breedName, "Labrador", "breedName should update")
        XCTAssertEqual(viewModel.locationText, "Krakow, +10km", "locationText should update")
        XCTAssertEqual(viewModel.statusText, AnimalStatus.found.displayName, "statusText should update")
    }
    
    /**
     * Tests that isExpanded state is independent of animal updates.
     */
    func test_isExpanded_shouldRemainIndependentAfterUpdate() {
        // Given - ViewModel with isExpanded set to true
        let initialAnimal = makeTestAnimal(id: "test-id")
        let viewModel = AnimalCardViewModel(animal: initialAnimal, onAction: { _ in })
        viewModel.isExpanded = true
        
        // When - animal is updated
        let updatedAnimal = makeTestAnimal(id: "test-id", breed: "New Breed")
        viewModel.update(with: updatedAnimal)
        
        // Then - isExpanded should remain true (local state preserved)
        XCTAssertTrue(viewModel.isExpanded, "isExpanded state should be preserved after update")
    }
}

