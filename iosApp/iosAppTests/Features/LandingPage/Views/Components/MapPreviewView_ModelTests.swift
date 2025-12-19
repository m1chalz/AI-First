import XCTest
import MapKit
@testable import PetSpot

/// Unit tests for MapPreviewView.Model enum.
/// Tests Equatable conformance and all three cases.
/// Follows Given-When-Then structure per constitution.
final class MapPreviewView_ModelTests: XCTestCase {
    
    // MARK: - Loading State Tests
    
    func test_loading_equatable_whenBothLoading_shouldBeEqual() {
        // Given
        let model1 = MapPreviewView.Model.loading
        let model2 = MapPreviewView.Model.loading
        
        // When / Then
        XCTAssertEqual(model1, model2, "Two loading states should be equal")
    }
    
    // MARK: - Map State Tests
    
    func test_map_equatable_whenSameRegion_shouldBeEqual() {
        // Given
        let region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.23, longitude: 21.01),
            latitudinalMeters: 20_000,
            longitudinalMeters: 20_000
        )
        let model1 = MapPreviewView.Model.map(region: region, onTap: { })
        let model2 = MapPreviewView.Model.map(region: region, onTap: { })
        
        // When / Then
        XCTAssertEqual(model1, model2, "Map states with same region should be equal (closures ignored)")
    }
    
    func test_map_equatable_whenDifferentRegion_shouldNotBeEqual() {
        // Given
        let region1 = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.23, longitude: 21.01),
            latitudinalMeters: 20_000,
            longitudinalMeters: 20_000
        )
        let region2 = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 50.06, longitude: 19.94),
            latitudinalMeters: 20_000,
            longitudinalMeters: 20_000
        )
        let model1 = MapPreviewView.Model.map(region: region1, onTap: { })
        let model2 = MapPreviewView.Model.map(region: region2, onTap: { })
        
        // When / Then
        XCTAssertNotEqual(model1, model2, "Map states with different regions should not be equal")
    }
    
    // MARK: - Permission Required State Tests
    
    func test_permissionRequired_equatable_whenSameMessage_shouldBeEqual() {
        // Given
        let model1 = MapPreviewView.Model.permissionRequired(
            message: "Enable location",
            onGoToSettings: { }
        )
        let model2 = MapPreviewView.Model.permissionRequired(
            message: "Enable location",
            onGoToSettings: { }
        )
        
        // When / Then
        XCTAssertEqual(model1, model2, "Permission states with same message should be equal (closures ignored)")
    }
    
    func test_permissionRequired_equatable_whenDifferentMessage_shouldNotBeEqual() {
        // Given
        let model1 = MapPreviewView.Model.permissionRequired(
            message: "Enable location",
            onGoToSettings: { }
        )
        let model2 = MapPreviewView.Model.permissionRequired(
            message: "Different message",
            onGoToSettings: { }
        )
        
        // When / Then
        XCTAssertNotEqual(model1, model2, "Permission states with different messages should not be equal")
    }
    
    // MARK: - Cross-State Equality Tests
    
    func test_equatable_whenDifferentCases_shouldNotBeEqual() {
        // Given
        let loadingModel = MapPreviewView.Model.loading
        let region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.23, longitude: 21.01),
            latitudinalMeters: 20_000,
            longitudinalMeters: 20_000
        )
        let mapModel = MapPreviewView.Model.map(region: region, onTap: { })
        let permissionModel = MapPreviewView.Model.permissionRequired(
            message: "Enable location",
            onGoToSettings: { }
        )
        
        // When / Then
        XCTAssertNotEqual(loadingModel, mapModel, "Loading and map states should not be equal")
        XCTAssertNotEqual(loadingModel, permissionModel, "Loading and permission states should not be equal")
        XCTAssertNotEqual(mapModel, permissionModel, "Map and permission states should not be equal")
    }
}

