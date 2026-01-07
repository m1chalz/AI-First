import XCTest
@testable import PetSpot

/// Unit tests for FullscreenMapViewModel.
/// Tests minimal ViewModel for MVVM-C compliance.
/// Follows Given-When-Then structure per project constitution.
@MainActor
final class FullscreenMapViewModelTests: XCTestCase {
    
    // MARK: - Test Properties
    
    private var sut: FullscreenMapViewModel!
    
    // MARK: - Setup & Teardown
    
    override func setUp() {
        super.setUp()
    }
    
    override func tearDown() {
        sut = nil
        super.tearDown()
    }
    
    // MARK: - T005: Initialization Tests
    
    func testInit_shouldCreateViewModel() {
        // Given & When
        sut = FullscreenMapViewModel()
        
        // Then
        XCTAssertNotNil(sut, "FullscreenMapViewModel should be created successfully")
    }
}

