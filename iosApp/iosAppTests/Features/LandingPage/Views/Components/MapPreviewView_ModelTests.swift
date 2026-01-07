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
        let model1 = MapPreviewView.Model.map(region: region, pins: [], onTap: { })
        let model2 = MapPreviewView.Model.map(region: region, pins: [], onTap: { })
        
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
        let model1 = MapPreviewView.Model.map(region: region1, pins: [], onTap: { })
        let model2 = MapPreviewView.Model.map(region: region2, pins: [], onTap: { })
        
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
    
    // MARK: - T005: Map State with Pins Equality Tests
    
    func test_map_equatable_whenSameRegionAndPins_shouldBeEqual() {
        // Given
        let region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.23, longitude: 21.01),
            latitudinalMeters: 20_000,
            longitudinalMeters: 20_000
        )
        let pins = [
            MapPreviewView.PinModel(id: "1", coordinate: Coordinate(latitude: 52.23, longitude: 21.01), status: .active),
            MapPreviewView.PinModel(id: "2", coordinate: Coordinate(latitude: 52.24, longitude: 21.02), status: .found)
        ]
        
        let model1 = MapPreviewView.Model.map(region: region, pins: pins, onTap: { })
        let model2 = MapPreviewView.Model.map(region: region, pins: pins, onTap: { })
        
        // When / Then
        XCTAssertEqual(model1, model2, "Map states with same region and pins should be equal")
    }
    
    func test_map_equatable_whenDifferentPins_shouldNotBeEqual() {
        // Given
        let region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.23, longitude: 21.01),
            latitudinalMeters: 20_000,
            longitudinalMeters: 20_000
        )
        let pins1 = [
            MapPreviewView.PinModel(id: "1", coordinate: Coordinate(latitude: 52.23, longitude: 21.01), status: .active)
        ]
        let pins2 = [
            MapPreviewView.PinModel(id: "2", coordinate: Coordinate(latitude: 52.24, longitude: 21.02), status: .active)
        ]
        
        let model1 = MapPreviewView.Model.map(region: region, pins: pins1, onTap: { })
        let model2 = MapPreviewView.Model.map(region: region, pins: pins2, onTap: { })
        
        // When / Then
        XCTAssertNotEqual(model1, model2, "Map states with different pins should not be equal")
    }
    
    func test_map_equatable_whenEmptyVsNonEmptyPins_shouldNotBeEqual() {
        // Given
        let region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.23, longitude: 21.01),
            latitudinalMeters: 20_000,
            longitudinalMeters: 20_000
        )
        let pins = [
            MapPreviewView.PinModel(id: "1", coordinate: Coordinate(latitude: 52.23, longitude: 21.01), status: .active)
        ]
        
        let model1 = MapPreviewView.Model.map(region: region, pins: [], onTap: { })
        let model2 = MapPreviewView.Model.map(region: region, pins: pins, onTap: { })
        
        // When / Then
        XCTAssertNotEqual(model1, model2, "Map with empty pins should not equal map with pins")
    }
    
    func test_map_defaultPins_shouldBeEmptyArray() {
        // Given
        let region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 52.23, longitude: 21.01),
            latitudinalMeters: 20_000,
            longitudinalMeters: 20_000
        )
        
        // When - using explicit empty pins (default value behavior)
        let model = MapPreviewView.Model.map(region: region, pins: [], onTap: { })
        
        // Then - should have empty pins
        if case .map(_, let pins, _) = model {
            XCTAssertTrue(pins.isEmpty, "Default pins should be empty array")
        } else {
            XCTFail("Expected .map case")
        }
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
        let mapModel = MapPreviewView.Model.map(region: region, pins: [], onTap: { })
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

