import XCTest
@testable import PetSpot

/// Unit tests for AnnouncementListQuery model.
/// Tests factory methods and range property for geographic filtering.
/// Follows Given-When-Then structure per project constitution.
final class AnnouncementListQueryTests: XCTestCase {
    
    // MARK: - T003: Range Property Tests
    
    func test_range_whenQueryCreated_shouldHaveRangeProperty() {
        // Given
        let location = Coordinate(latitude: 52.23, longitude: 21.01)
        
        // When
        let query = AnnouncementListQuery.landingPageQuery(location: location)
        
        // Then - Query should have range property (will fail until implemented)
        XCTAssertEqual(query.range, 10, "landingPageQuery should have range of 10 km")
    }
    
    // MARK: - T004: Landing Page Query Range Tests
    
    func test_landingPageQuery_shouldHaveRange10() {
        // Given
        let location = Coordinate(latitude: 52.23, longitude: 21.01)
        
        // When
        let query = AnnouncementListQuery.landingPageQuery(location: location)
        
        // Then
        XCTAssertEqual(query.range, 10, "Landing page query should use 10 km range (FR-001)")
    }
    
    func test_landingPageQuery_withNilLocation_shouldStillHaveRange10() {
        // Given / When
        let query = AnnouncementListQuery.landingPageQuery(location: nil)
        
        // Then
        XCTAssertEqual(query.range, 10, "Landing page query should have range even without location")
    }
    
    func test_defaultQuery_shouldHaveRange100() {
        // Given
        let location = Coordinate(latitude: 52.23, longitude: 21.01)
        
        // When
        let query = AnnouncementListQuery.defaultQuery(location: location)
        
        // Then
        XCTAssertEqual(query.range, 100, "Default query should use 100 km range")
    }
    
    func test_defaultQuery_withNilLocation_shouldHaveRange100() {
        // Given / When
        let query = AnnouncementListQuery.defaultQuery(location: nil)
        
        // Then
        XCTAssertEqual(query.range, 100, "Default query should have range even without location")
    }
    
    // MARK: - Existing Properties Tests
    
    func test_landingPageQuery_shouldHaveLimit5() {
        // Given / When
        let query = AnnouncementListQuery.landingPageQuery(location: nil)
        
        // Then
        XCTAssertEqual(query.limit, 5, "Landing page query should have limit of 5")
    }
    
    func test_defaultQuery_shouldHaveNilLimit() {
        // Given / When
        let query = AnnouncementListQuery.defaultQuery(location: nil)
        
        // Then
        XCTAssertNil(query.limit, "Default query should have no limit")
    }
    
    func test_query_equatable_whenSameRange_shouldBeEqual() {
        // Given
        let query1 = AnnouncementListQuery.landingPageQuery(location: nil)
        let query2 = AnnouncementListQuery.landingPageQuery(location: nil)
        
        // When / Then
        XCTAssertEqual(query1, query2, "Queries with same properties should be equal")
    }
}

