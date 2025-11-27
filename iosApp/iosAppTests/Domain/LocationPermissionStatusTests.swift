import XCTest
import CoreLocation
@testable import PetSpot

/**
 * Unit tests for LocationPermissionStatus domain model.
 * Tests enum cases, computed properties, and CLAuthorizationStatus conversion.
 * Follows Given-When-Then structure per constitution.
 */
final class LocationPermissionStatusTests: XCTestCase {
    
    // MARK: - isAuthorized Computed Property Tests
    
    func test_isAuthorized_whenAuthorizedWhenInUse_shouldReturnTrue() {
        // Given
        let status = LocationPermissionStatus.authorizedWhenInUse
        
        // When
        let result = status.isAuthorized
        
        // Then
        XCTAssertTrue(result, "authorizedWhenInUse should be considered authorized")
    }
    
    func test_isAuthorized_whenAuthorizedAlways_shouldReturnTrue() {
        // Given
        let status = LocationPermissionStatus.authorizedAlways
        
        // When
        let result = status.isAuthorized
        
        // Then
        XCTAssertTrue(result, "authorizedAlways should be considered authorized")
    }
    
    func test_isAuthorized_whenNotDetermined_shouldReturnFalse() {
        // Given
        let status = LocationPermissionStatus.notDetermined
        
        // When
        let result = status.isAuthorized
        
        // Then
        XCTAssertFalse(result, "notDetermined should not be considered authorized")
    }
    
    func test_isAuthorized_whenDenied_shouldReturnFalse() {
        // Given
        let status = LocationPermissionStatus.denied
        
        // When
        let result = status.isAuthorized
        
        // Then
        XCTAssertFalse(result, "denied should not be considered authorized")
    }
    
    func test_isAuthorized_whenRestricted_shouldReturnFalse() {
        // Given
        let status = LocationPermissionStatus.restricted
        
        // When
        let result = status.isAuthorized
        
        // Then
        XCTAssertFalse(result, "restricted should not be considered authorized")
    }
    
    // MARK: - CLAuthorizationStatus Conversion Tests
    
    func test_initFromCLStatus_whenNotDetermined_shouldMapCorrectly() {
        // Given
        let clStatus = CLAuthorizationStatus.notDetermined
        
        // When
        let status = LocationPermissionStatus(from: clStatus)
        
        // Then
        XCTAssertEqual(status, .notDetermined, "Should convert CLAuthorizationStatus.notDetermined to .notDetermined")
    }
    
    func test_initFromCLStatus_whenAuthorizedWhenInUse_shouldMapCorrectly() {
        // Given
        let clStatus = CLAuthorizationStatus.authorizedWhenInUse
        
        // When
        let status = LocationPermissionStatus(from: clStatus)
        
        // Then
        XCTAssertEqual(status, .authorizedWhenInUse, "Should convert CLAuthorizationStatus.authorizedWhenInUse to .authorizedWhenInUse")
    }
    
    func test_initFromCLStatus_whenAuthorizedAlways_shouldMapCorrectly() {
        // Given
        let clStatus = CLAuthorizationStatus.authorizedAlways
        
        // When
        let status = LocationPermissionStatus(from: clStatus)
        
        // Then
        XCTAssertEqual(status, .authorizedAlways, "Should convert CLAuthorizationStatus.authorizedAlways to .authorizedAlways")
    }
    
    func test_initFromCLStatus_whenDenied_shouldMapCorrectly() {
        // Given
        let clStatus = CLAuthorizationStatus.denied
        
        // When
        let status = LocationPermissionStatus(from: clStatus)
        
        // Then
        XCTAssertEqual(status, .denied, "Should convert CLAuthorizationStatus.denied to .denied")
    }
    
    func test_initFromCLStatus_whenRestricted_shouldMapCorrectly() {
        // Given
        let clStatus = CLAuthorizationStatus.restricted
        
        // When
        let status = LocationPermissionStatus(from: clStatus)
        
        // Then
        XCTAssertEqual(status, .restricted, "Should convert CLAuthorizationStatus.restricted to .restricted")
    }
    
    // MARK: - Equatable Tests
    
    func test_equatable_whenSameStatus_shouldBeEqual() {
        // Given
        let status1 = LocationPermissionStatus.authorizedWhenInUse
        let status2 = LocationPermissionStatus.authorizedWhenInUse
        
        // When / Then
        XCTAssertEqual(status1, status2, "Same status values should be equal")
    }
    
    func test_equatable_whenDifferentStatus_shouldNotBeEqual() {
        // Given
        let status1 = LocationPermissionStatus.authorizedWhenInUse
        let status2 = LocationPermissionStatus.denied
        
        // When / Then
        XCTAssertNotEqual(status1, status2, "Different status values should not be equal")
    }
}

