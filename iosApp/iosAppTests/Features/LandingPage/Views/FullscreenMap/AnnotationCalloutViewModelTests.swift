import XCTest
import CoreLocation
@testable import PetSpot

final class AnnotationCalloutViewModelTests: XCTestCase {
    
    // MARK: - T053: Model Field Mapping Tests (KAN-32)
    
    func testModelInit_whenAllFieldsProvided_shouldFormatCorrectly() {
        // Given
        let pin = MapPin(
            id: "test-id-123",
            coordinate: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
            species: .cat,
            status: .active,
            petName: "Simba",
            photoUrl: "https://example.com/photo.jpg",
            breed: "Tabby",
            lastSeenDate: "2025-01-08",
            ownerEmail: "owner@example.com",
            ownerPhone: "(555) 123-4567",
            petDescription: "Orange tabby cat with white paws"
        )
        
        // When
        let model = AnnotationCalloutView.Model(from: pin)
        
        // Then
        XCTAssertEqual(model.photoUrl, "https://example.com/photo.jpg")
        XCTAssertEqual(model.petName, "Simba")
        XCTAssertEqual(model.speciesAndBreed, "Cat • Tabby")
        XCTAssertEqual(model.locationText, "📍 52.2297° N, 21.0122° E")
        XCTAssertEqual(model.dateText, "📅 Jan 08, 2025")
        XCTAssertEqual(model.emailText, "📧 owner@example.com")
        XCTAssertEqual(model.phoneText, "📞 (555) 123-4567")
        XCTAssertEqual(model.descriptionText, "Orange tabby cat with white paws")
        XCTAssertEqual(model.statusText, "Active")
        XCTAssertEqual(model.statusColorHex, "#FF9500") // Orange for active
        XCTAssertEqual(model.accessibilityId, "fullscreenMap.annotation.test-id-123")
    }
    
    func testModelInit_whenStatusIsFound_shouldUseBlueColor() {
        // Given
        let pin = MapPin(
            id: "found-pet",
            coordinate: CLLocationCoordinate2D(latitude: 40.7128, longitude: -74.0060),
            species: .dog,
            status: .found,
            petName: "Max",
            photoUrl: "https://example.com/max.jpg",
            breed: "Golden Retriever",
            lastSeenDate: "2025-01-07",
            ownerEmail: nil,
            ownerPhone: nil,
            petDescription: nil
        )
        
        // When
        let model = AnnotationCalloutView.Model(from: pin)
        
        // Then (FR-009)
        XCTAssertEqual(model.statusText, "Found")
        XCTAssertEqual(model.statusColorHex, "#155DFC") // Blue for found
    }
    
    func testModelInit_whenStatusIsClosed_shouldUseGrayColor() {
        // Given
        let pin = MapPin(
            id: "closed-case",
            coordinate: CLLocationCoordinate2D(latitude: 51.5074, longitude: -0.1278),
            species: .other,
            status: .closed,
            petName: "Tweety",
            photoUrl: "",
            breed: nil,
            lastSeenDate: "2024-12-25",
            ownerEmail: nil,
            ownerPhone: nil,
            petDescription: nil
        )
        
        // When
        let model = AnnotationCalloutView.Model(from: pin)
        
        // Then
        XCTAssertEqual(model.statusText, "Closed")
        XCTAssertEqual(model.statusColorHex, "#93A2B4") // Gray for closed
    }
    
    // MARK: - T054: Graceful Nil Handling Tests (KAN-32)
    
    func testModelInit_whenPhotoUrlIsEmpty_shouldReturnNilPhotoUrl() {
        // Given
        let pin = MapPin(
            id: "no-photo",
            coordinate: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
            species: .cat,
            status: .active,
            petName: "Mystery",
            photoUrl: "", // Empty string
            breed: nil,
            lastSeenDate: "2025-01-08",
            ownerEmail: nil,
            ownerPhone: nil,
            petDescription: nil
        )
        
        // When
        let model = AnnotationCalloutView.Model(from: pin)
        
        // Then (FR-005)
        XCTAssertNil(model.photoUrl, "Empty photo URL should become nil for placeholder display")
    }
    
    func testModelInit_whenPetNameIsNil_shouldUseFallbackText() {
        // Given
        let pin = MapPin(
            id: "unknown-pet",
            coordinate: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
            species: .dog,
            status: .active,
            petName: nil, // Nil name
            photoUrl: "https://example.com/photo.jpg",
            breed: "Labrador",
            lastSeenDate: "2025-01-08",
            ownerEmail: nil,
            ownerPhone: nil,
            petDescription: nil
        )
        
        // When
        let model = AnnotationCalloutView.Model(from: pin)
        
        // Then (FR-004)
        XCTAssertEqual(model.petName, "Unknown Pet")
    }
    
    func testModelInit_whenBreedIsNil_shouldShowOnlySpecies() {
        // Given
        let pin = MapPin(
            id: "no-breed",
            coordinate: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
            species: .cat,
            status: .active,
            petName: "Whiskers",
            photoUrl: "https://example.com/photo.jpg",
            breed: nil, // No breed
            lastSeenDate: "2025-01-08",
            ownerEmail: nil,
            ownerPhone: nil,
            petDescription: nil
        )
        
        // When
        let model = AnnotationCalloutView.Model(from: pin)
        
        // Then (graceful handling)
        XCTAssertEqual(model.speciesAndBreed, "Cat")
        XCTAssertFalse(model.speciesAndBreed.contains("•"))
    }
    
    func testModelInit_whenOptionalFieldsAreNil_shouldReturnNilValues() {
        // Given
        let pin = MapPin(
            id: "minimal-data",
            coordinate: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
            species: .other,
            status: .active,
            petName: "Pet",
            photoUrl: "https://example.com/photo.jpg",
            breed: nil,
            lastSeenDate: "2025-01-08",
            ownerEmail: nil, // No email
            ownerPhone: nil, // No phone
            petDescription: nil // No description
        )
        
        // When
        let model = AnnotationCalloutView.Model(from: pin)
        
        // Then (FR-006, FR-007, FR-008)
        XCTAssertNil(model.emailText, "Nil email should remain nil")
        XCTAssertNil(model.phoneText, "Nil phone should remain nil")
        XCTAssertNil(model.descriptionText, "Nil description should remain nil")
    }
    
    func testModelInit_whenCoordinatesAreSouthWest_shouldFormatWithCorrectCardinals() {
        // Given - Buenos Aires, Argentina (southern hemisphere)
        let pin = MapPin(
            id: "south-west",
            coordinate: CLLocationCoordinate2D(latitude: -34.6037, longitude: -58.3816),
            species: .dog,
            status: .active,
            petName: "Gaucho",
            photoUrl: "https://example.com/photo.jpg",
            breed: nil,
            lastSeenDate: "2025-01-08",
            ownerEmail: nil,
            ownerPhone: nil,
            petDescription: nil
        )
        
        // When
        let model = AnnotationCalloutView.Model(from: pin)
        
        // Then
        XCTAssertEqual(model.locationText, "📍 34.6037° S, 58.3816° W")
    }
    
    func testModelInit_whenDateFormatIsInvalid_shouldReturnOriginalString() {
        // Given - Invalid date format
        let pin = MapPin(
            id: "invalid-date",
            coordinate: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
            species: .cat,
            status: .active,
            petName: "Kitty",
            photoUrl: "https://example.com/photo.jpg",
            breed: nil,
            lastSeenDate: "invalid-date-string", // Not yyyy-MM-dd format
            ownerEmail: nil,
            ownerPhone: nil,
            petDescription: nil
        )
        
        // When
        let model = AnnotationCalloutView.Model(from: pin)
        
        // Then - graceful fallback
        XCTAssertEqual(model.dateText, "📅 invalid-date-string")
    }
}

