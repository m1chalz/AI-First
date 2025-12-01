import XCTest
@testable import PetSpot

/**
 * Unit tests for Coordinate domain model.
 * Tests struct initialization and equality.
 * Follows Given-When-Then structure per constitution.
 */
final class CoordinateTests: XCTestCase {
    
    // MARK: - Initialization Tests
    
    func test_init_whenProvidedWithValidCoordinates_shouldCreateCoordinate() {
        // Given
        let latitude = 52.2297
        let longitude = 21.0122
        
        // When
        let coordinate = Coordinate(
            latitude: latitude,
            longitude: longitude
        )
        
        // Then
        XCTAssertEqual(coordinate.latitude, latitude, accuracy: 0.0001)
        XCTAssertEqual(coordinate.longitude, longitude, accuracy: 0.0001)
    }
    
    // MARK: - Equatable Tests
    
    func test_equatable_whenSameCoordinates_shouldBeEqual() {
        // Given
        let coordinate1 = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let coordinate2 = Coordinate(latitude: 52.2297, longitude: 21.0122)
        
        // When / Then
        XCTAssertEqual(coordinate1, coordinate2, "Coordinates with same latitude and longitude should be equal")
    }
    
    func test_equatable_whenDifferentLatitude_shouldNotBeEqual() {
        // Given
        let coordinate1 = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let coordinate2 = Coordinate(latitude: 50.0647, longitude: 21.0122)
        
        // When / Then
        XCTAssertNotEqual(coordinate1, coordinate2, "Coordinates with different latitude should not be equal")
    }
    
    func test_equatable_whenDifferentLongitude_shouldNotBeEqual() {
        // Given
        let coordinate1 = Coordinate(latitude: 52.2297, longitude: 21.0122)
        let coordinate2 = Coordinate(latitude: 52.2297, longitude: 19.9450)
        
        // When / Then
        XCTAssertNotEqual(coordinate1, coordinate2, "Coordinates with different longitude should not be equal")
    }
    
    // MARK: - Coordinate Range Tests (Domain Validation)
    
    func test_coordinates_shouldAcceptValidLatitudeRange() {
        // Given / When - Valid latitude range: -90 to +90
        let minLat = Coordinate(latitude: -90.0, longitude: 0.0)
        let maxLat = Coordinate(latitude: 90.0, longitude: 0.0)
        
        // Then - Should create successfully
        XCTAssertEqual(minLat.latitude, -90.0, accuracy: 0.0001)
        XCTAssertEqual(maxLat.latitude, 90.0, accuracy: 0.0001)
    }
    
    func test_coordinates_shouldAcceptValidLongitudeRange() {
        // Given / When - Valid longitude range: -180 to +180
        let minLon = Coordinate(latitude: 0.0, longitude: -180.0)
        let maxLon = Coordinate(latitude: 0.0, longitude: 180.0)
        
        // Then - Should create successfully
        XCTAssertEqual(minLon.longitude, -180.0, accuracy: 0.0001)
        XCTAssertEqual(maxLon.longitude, 180.0, accuracy: 0.0001)
    }
}

