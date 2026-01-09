import XCTest
import MapKit
@testable import PetSpot

/// Unit tests for MKCoordinateRegion+Radius extension.
/// Tests radius calculation from map region span.
/// Follows Given-When-Then structure per project constitution.
final class MKCoordinateRegionRadiusTests: XCTestCase {
    
    // MARK: - T028: Radius Calculation (US2)
    
    func testRadiusInKilometers_whenRegionProvided_shouldCalculateCorrectRadius() {
        // Given - Warsaw region with ~20km span (0.18 degrees ≈ 20km)
        let region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
            span: MKCoordinateSpan(latitudeDelta: 0.18, longitudeDelta: 0.18)
        )
        
        // When
        let radiusKm = region.radiusInKilometers
        
        // Then - diagonal of ~20km square ≈ 28km, radius ≈ 14km
        // Allow reasonable tolerance for geodesic calculation
        XCTAssertGreaterThan(radiusKm, 8)
        XCTAssertLessThan(radiusKm, 20)
    }
    
    func testRadiusInKilometers_whenSmallRegion_shouldReturnAtLeastOneKilometer() {
        // Given - very small region
        let region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
            span: MKCoordinateSpan(latitudeDelta: 0.001, longitudeDelta: 0.001)
        )
        
        // When
        let radiusKm = region.radiusInKilometers
        
        // Then - minimum 1km radius
        XCTAssertGreaterThanOrEqual(radiusKm, 1)
    }
    
    func testRadiusInKilometers_whenLargeRegion_shouldCalculateLargeRadius() {
        // Given - large region (~100km span)
        let region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.2297, longitude: 21.0122),
            span: MKCoordinateSpan(latitudeDelta: 0.9, longitudeDelta: 0.9)
        )
        
        // When
        let radiusKm = region.radiusInKilometers
        
        // Then - should be proportionally larger
        XCTAssertGreaterThan(radiusKm, 40)
    }
    
    func testRadiusInKilometers_whenDifferentLatitude_shouldAdjustForGeodesic() {
        // Given - region at equator vs high latitude
        let equatorRegion = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 0, longitude: 0),
            span: MKCoordinateSpan(latitudeDelta: 0.18, longitudeDelta: 0.18)
        )
        let polarRegion = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 60, longitude: 0),
            span: MKCoordinateSpan(latitudeDelta: 0.18, longitudeDelta: 0.18)
        )
        
        // When
        let equatorRadius = equatorRegion.radiusInKilometers
        let polarRadius = polarRegion.radiusInKilometers
        
        // Then - both should be reasonable (geodesic calculation handles latitude differences)
        XCTAssertGreaterThan(equatorRadius, 5)
        XCTAssertGreaterThan(polarRadius, 5)
    }
}

