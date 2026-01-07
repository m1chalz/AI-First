import XCTest
import MapKit
@testable import PetSpot

/// Unit tests for FullscreenMapViewModel.
/// Tests map region centering and legend configuration.
/// Follows Given-When-Then structure per project constitution.
@MainActor
final class FullscreenMapViewModelTests: XCTestCase {
    
    // MARK: - Test Properties
    
    private var sut: FullscreenMapViewModel!
    
    // MARK: - Teardown
    
    override func tearDown() {
        sut = nil
        super.tearDown()
    }
    
    // MARK: - T008: Map Region Centering Tests (US1)
    
    func testInit_withUserLocation_shouldSetMapRegionCenterFromLocation() {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122) // Warsaw
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then
        XCTAssertEqual(sut.mapRegion.center.latitude, 52.2297, accuracy: 0.001)
        XCTAssertEqual(sut.mapRegion.center.longitude, 21.0122, accuracy: 0.001)
    }
    
    func testInit_withUserLocation_shouldUseCityLevelZoom() {
        // Given
        let userLocation = Coordinate(latitude: 52.2297, longitude: 21.0122)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then - ~20km span (10km radius * 2 = ~0.18 degrees latitude)
        XCTAssertEqual(sut.mapRegion.span.latitudeDelta, 0.18, accuracy: 0.05)
    }
    
    func testInit_withDifferentLocation_shouldCenterOnThatLocation() {
        // Given
        let krakowLocation = Coordinate(latitude: 50.0647, longitude: 19.9450) // Krakow
        
        // When
        sut = FullscreenMapViewModel(userLocation: krakowLocation)
        
        // Then
        XCTAssertEqual(sut.mapRegion.center.latitude, 50.0647, accuracy: 0.001)
        XCTAssertEqual(sut.mapRegion.center.longitude, 19.9450, accuracy: 0.001)
    }
    
    // MARK: - T014: Legend Model Configuration Tests (US2)
    
    func testLegendModel_shouldHaveTwoLegendItems() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then
        XCTAssertEqual(sut.legendModel.legendItems.count, 2)
    }
    
    func testLegendModel_shouldHaveMissingAndFoundItems() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then
        XCTAssertEqual(sut.legendModel.legendItems[0].id, "missing")
        XCTAssertEqual(sut.legendModel.legendItems[1].id, "found")
    }
    
    func testLegendModel_shouldHaveCorrectColors() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then
        XCTAssertEqual(sut.legendModel.legendItems[0].colorHex, "#FF0000") // Red for missing
        XCTAssertEqual(sut.legendModel.legendItems[1].colorHex, "#0074FF") // Blue for found
    }
    
    func testLegendModel_shouldHaveNilTitle() {
        // Given - fullscreen map uses navigation bar for title
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then
        XCTAssertNil(sut.legendModel.title)
    }
    
    func testLegendModel_shouldHaveCorrectAccessibilityIdPrefix() {
        // Given
        let userLocation = Coordinate(latitude: 52.0, longitude: 21.0)
        
        // When
        sut = FullscreenMapViewModel(userLocation: userLocation)
        
        // Then
        XCTAssertEqual(sut.legendModel.legendAccessibilityIdPrefix, "fullscreenMap.legend")
    }
}
