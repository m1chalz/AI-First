import XCTest
@testable import PetSpot

/**
 * Unit tests for UserLocation domain model.
 * Tests struct initialization, equality, and default timestamp behavior.
 * Follows Given-When-Then structure per constitution.
 */
final class UserLocationTests: XCTestCase {
    
    // MARK: - Initialization Tests
    
    func test_init_whenProvidedWithValidCoordinates_shouldCreateLocation() {
        // Given
        let latitude = 52.2297
        let longitude = 21.0122
        let timestamp = Date()
        
        // When
        let location = UserLocation(
            latitude: latitude,
            longitude: longitude,
            timestamp: timestamp
        )
        
        // Then
        XCTAssertEqual(location.latitude, latitude, accuracy: 0.0001)
        XCTAssertEqual(location.longitude, longitude, accuracy: 0.0001)
        XCTAssertEqual(location.timestamp, timestamp)
    }
    
    func test_init_whenTimestampNotProvided_shouldUseCurrentDate() {
        // Given
        let latitude = 52.2297
        let longitude = 21.0122
        let beforeCreation = Date()
        
        // When
        let location = UserLocation(
            latitude: latitude,
            longitude: longitude
        )
        
        // Then
        let afterCreation = Date()
        XCTAssertTrue(location.timestamp >= beforeCreation, "Timestamp should be at or after creation time")
        XCTAssertTrue(location.timestamp <= afterCreation, "Timestamp should be at or before current time")
    }
    
    // MARK: - Equatable Tests
    
    func test_equatable_whenSameCoordinatesAndTimestamp_shouldBeEqual() {
        // Given
        let timestamp = Date()
        let location1 = UserLocation(latitude: 52.2297, longitude: 21.0122, timestamp: timestamp)
        let location2 = UserLocation(latitude: 52.2297, longitude: 21.0122, timestamp: timestamp)
        
        // When / Then
        XCTAssertEqual(location1, location2, "Locations with same coordinates and timestamp should be equal")
    }
    
    func test_equatable_whenDifferentLatitude_shouldNotBeEqual() {
        // Given
        let timestamp = Date()
        let location1 = UserLocation(latitude: 52.2297, longitude: 21.0122, timestamp: timestamp)
        let location2 = UserLocation(latitude: 50.0647, longitude: 21.0122, timestamp: timestamp)
        
        // When / Then
        XCTAssertNotEqual(location1, location2, "Locations with different latitude should not be equal")
    }
    
    func test_equatable_whenDifferentLongitude_shouldNotBeEqual() {
        // Given
        let timestamp = Date()
        let location1 = UserLocation(latitude: 52.2297, longitude: 21.0122, timestamp: timestamp)
        let location2 = UserLocation(latitude: 52.2297, longitude: 19.9450, timestamp: timestamp)
        
        // When / Then
        XCTAssertNotEqual(location1, location2, "Locations with different longitude should not be equal")
    }
    
    func test_equatable_whenDifferentTimestamp_shouldNotBeEqual() {
        // Given
        let location1 = UserLocation(latitude: 52.2297, longitude: 21.0122, timestamp: Date())
        let location2 = UserLocation(latitude: 52.2297, longitude: 21.0122, timestamp: Date().addingTimeInterval(100))
        
        // When / Then
        XCTAssertNotEqual(location1, location2, "Locations with different timestamp should not be equal")
    }
    
    // MARK: - Coordinate Range Tests (Domain Validation)
    
    func test_coordinates_shouldAcceptValidLatitudeRange() {
        // Given / When - Valid latitude range: -90 to +90
        let minLat = UserLocation(latitude: -90.0, longitude: 0.0)
        let maxLat = UserLocation(latitude: 90.0, longitude: 0.0)
        
        // Then - Should create successfully
        XCTAssertEqual(minLat.latitude, -90.0, accuracy: 0.0001)
        XCTAssertEqual(maxLat.latitude, 90.0, accuracy: 0.0001)
    }
    
    func test_coordinates_shouldAcceptValidLongitudeRange() {
        // Given / When - Valid longitude range: -180 to +180
        let minLon = UserLocation(latitude: 0.0, longitude: -180.0)
        let maxLon = UserLocation(latitude: 0.0, longitude: 180.0)
        
        // Then - Should create successfully
        XCTAssertEqual(minLon.longitude, -180.0, accuracy: 0.0001)
        XCTAssertEqual(maxLon.longitude, 180.0, accuracy: 0.0001)
    }
}

