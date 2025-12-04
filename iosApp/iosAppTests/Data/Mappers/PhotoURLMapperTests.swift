import XCTest
@testable import PetSpot

/**
 * Unit tests for PhotoURLMapper
 * Tests URL resolution for relative and absolute paths
 * Follows Given-When-Then structure per constitution
 */
final class PhotoURLMapperTests: XCTestCase {
    
    var sut: PhotoURLMapper!
    
    override func setUp() {
        super.setUp()
        sut = PhotoURLMapper(baseURL: "http://localhost:3000")
    }
    
    override func tearDown() {
        sut = nil
        super.tearDown()
    }
    
    // MARK: - Relative URL Tests
    
    func test_resolve_whenRelativeURL_shouldPrependBaseURL() {
        // Given - relative URL starting with /
        let relativeURL = "/images/photo.jpg"
        
        // When
        let result = sut.resolve(relativeURL)
        
        // Then
        XCTAssertEqual(result, "http://localhost:3000/images/photo.jpg")
    }
    
    func test_resolve_whenRelativeURLWithMultipleSegments_shouldPrependBaseURL() {
        // Given
        let relativeURL = "/api/v1/images/animals/dog.png"
        
        // When
        let result = sut.resolve(relativeURL)
        
        // Then
        XCTAssertEqual(result, "http://localhost:3000/api/v1/images/animals/dog.png")
    }
    
    // MARK: - Absolute URL Tests
    
    func test_resolve_whenAbsoluteHTTPURL_shouldReturnUnchanged() {
        // Given - already absolute HTTP URL
        let absoluteURL = "http://example.com/photo.jpg"
        
        // When
        let result = sut.resolve(absoluteURL)
        
        // Then
        XCTAssertEqual(result, absoluteURL)
    }
    
    func test_resolve_whenAbsoluteHTTPSURL_shouldReturnUnchanged() {
        // Given - already absolute HTTPS URL
        let absoluteURL = "https://example.com/photo.jpg"
        
        // When
        let result = sut.resolve(absoluteURL)
        
        // Then
        XCTAssertEqual(result, absoluteURL)
    }
    
    func test_resolve_whenExternalCDNURL_shouldReturnUnchanged() {
        // Given - external CDN URL
        let cdnURL = "https://cdn.example.com/images/photo.jpg"
        
        // When
        let result = sut.resolve(cdnURL)
        
        // Then
        XCTAssertEqual(result, cdnURL)
    }
    
    // MARK: - Edge Cases
    
    func test_resolve_whenEmptyString_shouldReturnEmpty() {
        // Given
        let emptyURL = ""
        
        // When
        let result = sut.resolve(emptyURL)
        
        // Then
        XCTAssertEqual(result, "")
    }
    
    func test_resolve_whenJustSlash_shouldPrependBaseURL() {
        // Given - just a slash
        let justSlash = "/"
        
        // When
        let result = sut.resolve(justSlash)
        
        // Then
        XCTAssertEqual(result, "http://localhost:3000/")
    }
}

